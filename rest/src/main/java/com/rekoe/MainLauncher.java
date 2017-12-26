package com.rekoe;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.resource.Scans;

import com.rekoe.bean.acl.Permission;
import com.rekoe.bean.acl.Role;
import com.rekoe.bean.acl.User;
import com.rekoe.shiro.OAuth2SubjectFactory;
import com.rekoe.vo.Result;

import club.zhcs.captcha.ImageVerification;

@IocBean(create = "init")
public class MainLauncher {

	private final static Log log = Logs.get();

	@Inject
	protected PropertiesProxy conf;

	@Inject
	private Dao dao;

	@At({ "/" })
	@Ok("->:/index.html")
	public void index() {
	}

	@At
	public void captcha(@Param(value = "length", df = "4") int length, HttpServletResponse resp, HttpSession session) throws IOException {
		resp.setContentType("image/jpeg");
		resp.setHeader("Pragma", "No-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		OutputStream out = resp.getOutputStream();
		// 输出图象到页面
		ImageVerification iv = new ImageVerification();
		if (length != 0) {
			iv.setIMAGE_VERIFICATION_LENGTH(length);
		}
		if (ImageIO.write(iv.creatImage(), "JPEG", out)) {
			log.debug("写入输出流成功:" + iv.getVerifyCode() + ".");
		} else {
			log.debug("写入输出流失败:" + iv.getVerifyCode() + ".");
		}
		session.setAttribute("SINO_CAPTCHA", iv.getVerifyCode());
		out.flush();
		out.close();
	}

	@Inject
	private DefaultWebSecurityManager shiroWebSecurityManager;

	@Inject
	private DefaultWebSessionManager shiroWebSessionManager;

	public void init() {
		shiroWebSessionManager.setDeleteInvalidSessions(true);
		NutShiro.DefaultUnauthorizedAjax = Json.fromJson(NutMap.class, Result.fail("无此授权").toString());
		NutShiro.DefaultUnauthenticatedAjax = NutShiro.DefaultUnauthorizedAjax;
		shiroWebSecurityManager.setSubjectFactory(new OAuth2SubjectFactory());
		Daos.createTablesInPackage(dao, "com.rekoe.bean", false);
		Daos.migration(dao, "com.rekoe.bean", true, true);
		List<Class<?>> clazzs = Scans.me().scanPackage("com.rekoe.module");
		List<Role> roles = dao.query(Role.class, null);
		if (Lang.isEmpty(roles)) {
			dao.insert(new Permission("*", "超级权限", true));
		}
		List<Permission> premissions = dao.query(Permission.class, null);
		List<Permission> newPremissions = new ArrayList<>();

		Lang.each(clazzs, new Each<Class<?>>() {

			@Override
			public void invoke(int index, Class<?> clazz, int length) throws ExitLoop, ContinueLoop, LoopException {
				Method[] methods = clazz.getMethods();
				Lang.each(methods, new Each<Method>() {

					@Override
					public void invoke(int index, Method ele, int length) throws ExitLoop, ContinueLoop, LoopException {
						NutzRequiresPermissions nutzRequiresPermissions = ele.getAnnotation(NutzRequiresPermissions.class);
						if (Lang.isEmpty(nutzRequiresPermissions)) {
							return;
						}
						boolean enable = nutzRequiresPermissions.enable();
						if (enable) {
							if (nutzRequiresPermissions.value().length == 1) {
								Permission perm = new Permission(nutzRequiresPermissions.value()[0], nutzRequiresPermissions.name(), false);
								if (premissions.contains(perm)) {
									return;
								}
								if (newPremissions.contains(perm)) {
									return;
								}
								newPremissions.add(perm);
							}
						}
					}
				});
			}
		});
		if (!Lang.isEmpty(newPremissions)) {
			dao.fastInsert(newPremissions);
		}
		if (Lang.isEmpty(roles)) {
			Role role = new Role();
			role.setName("admin");
			role.setDescription("超级管理组");
			role.setLock(true);
			role.setPermissions(dao.query(Permission.class, null));
			roles.add(dao.insert(role));
			dao.insertRelation(role, "permissions");
		}
		if (dao.count(User.class) == 0) {
			User user = new User();
			user.setCreateDate(Times.now());
			user.setDescription("超级管理员");
			user.setLocked(false);
			user.setName("admin");
			user.setSystem(true);
			user.setRegisterIp("127.0.0.1");
			user.setProviderid("local");
			String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
			user.setSalt(salt);
			user.setPassword(new Sha256Hash("12345678", salt, 1024).toBase64());
			user.setRoles(roles);
			user = dao.insert(user);
			dao.insertRelation(user, "roles");
		}
	}

	public static void main(String[] args) throws Exception {
		new NbApp(MainLauncher.class).run();
	}

}
