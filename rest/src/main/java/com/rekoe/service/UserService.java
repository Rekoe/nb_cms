package com.rekoe.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.rekoe.bean.acl.Role;
import com.rekoe.bean.acl.User;
import com.rekoe.vo.Result;

/**
 * @author 科技㊣²º¹³ <br />
 *         2014年2月3日 下午4:48:45 <br />
 *         http://www.rekoe.com <br />
 *         QQ:5382211
 */
@IocBean(args = { "refer:dao" })
public class UserService extends BaseService<User> {

	private final static Log log = Logs.get();

	public UserService(Dao dao) {
		super(dao);
	}

	public List<User> list() {
		return query(null, null);
	}

	public int update(User user) {
		return dao().update(user);
	}

	public void update(long uid, String password, boolean isLocked, Integer[] ids) {
		User user = fetch(uid);
		if (!Lang.isEmptyArray(ids)) {
			user.setRoles(dao().query(Role.class, Cnd.where("id", "in", ids)));
		}
		if (StringUtils.isNotBlank(password)) {
			String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
			user.setSalt(salt);
			user.setPassword(new Sha256Hash(password, salt, 1024).toBase64());
		}
		user.setLocked(isLocked);
		dao().update(user);
		if (!Lang.isEmpty(user.getRoles())) {
			dao().insertRelation(user, "roles");
		}
	}

	public void updatePwd(Object uid, String password) {
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		dao().update(User.class, Chain.make("password", new Sha256Hash(password, salt, 1024).toBase64()).add("salt", salt), Cnd.where("id", "=", uid));
	}

	public User insert(User user) {
		user = dao().insert(user);
		return dao().insertRelation(user, "roles");
	}

	public User fetchByProviderid(String providerid, String openid) {
		return dao().fetch(User.class, Cnd.where("providerid", "=", providerid).and("openid", "=", openid));
	}

	public User view(Long id) {
		User user = fetch(id);
		boolean isAdmin = user.isSystem();
		if (isAdmin) {
			List<Role> roles = dao().query(Role.class, null);
			user.setRoles(roles);
		} else
			dao().fetchLinks(user, null);
		return user;
	}

	public User fetchByName(String name) {
		User user = fetch(Cnd.where("name", "=", name));
		if (!Lang.isEmpty(user)) {
			Sql sql = Sqls.create("select roleid from system_user_role $condition");
			sql.setCondition(Cnd.where("userid", "=", user.getId()));
			sql.setCallback(Sqls.callback.longs());
			dao().execute(sql);
			List<Long> roleIds = sql.getList(Long.class);
			if (!Lang.isEmpty(roleIds)) {
				List<Role> roles = dao().query(Role.class, Cnd.where("id", "in", roleIds));
				if (!Lang.isEmpty(roles)) {
					user.setRoles(roles);
				}
			}
		}
		return user;
	}

	public List<String> getRoleNameList(User user) {
		List<String> roleNameList = new ArrayList<String>();
		for (Role role : user.getRoles()) {
			roleNameList.add(role.getName());
		}
		return roleNameList;
	}

	public void addRole(Long userId, Long roleId) {
		User user = fetch(userId);
		Role role = new Role();
		role.setId(roleId);
		user.setRoles(Lang.list(role));
		dao().insertRelation(user, "roles");
	}

	public void removeRole(Long userId, Long roleId) {
		dao().clear("system_user_role", Cnd.where("userid", "=", userId).and("roleid", "=", roleId));
	}

	public User initUser(String name, String openid, String providerid, String addr) {
		return dao().insert(initUser(name, openid, providerid, addr, true));
	}

	public User initUser(String name, String openid, String providerid, String addr, boolean locked) {
		return initUser(name, openid, providerid, addr, locked, R.UU32());
	}

	public User initUser(String name, String openid, String providerid, String addr, boolean locked, String pwd) {
		User temp = dao().fetch(getEntityClass(), Cnd.where("name", "=", name));
		if (!Lang.isEmpty(temp)) {
			name += R.random(2, 5);
		}
		User user = new User();
		user.setCreateDate(Times.now());
		user.setName(name);
		user.setOpenid(openid);
		user.setDescription("注册账号");
		user.setProviderid(providerid);
		user.setRegisterIp(addr);
		user.setLocked(locked);
		user.setSystem(false);
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		user.setSalt(salt);
		user.setPassword(new Sha256Hash(pwd, salt, 1024).toBase64());
		return dao().insert(user);
	}

	public User fetchByOpenID(String openid) {
		User user = fetch(Cnd.where("openid", "=", openid));
		if (!Lang.isEmpty(user) && !user.isLocked()) {
			dao().fetchLinks(user, "roles");
		}
		return user;
	}

	public User regist(User user, String addr) {
		user.setCreateDate(Times.now());
		user.setRegisterIp(addr);
		user.setSystem(false);
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		user.setSalt(salt);
		user.setPassword(new Sha256Hash(user.getPassword(), salt, 1024).toBase64());
		return dao().insert(user);
	}

	public void removeUserUpdata(long uid) {
		dao().clear("system_user_role", Cnd.where("userid", "=", uid));
	}

	public void insertRelations(User user) {
		dao().insertRelation(user, "roles");
	}

	public void updateLock(User user) {
		dao().update(user, "^(locked)$");
	}

	public void loadRolePermission(User user) {
		List<Role> roleList = user.getRoles();
		for (Role role : roleList) {
			dao().fetchLinks(role, "permissions");
		}
	}

	public Result setRole(long[] ids, long uid) {
		removeUserUpdata(uid);
		List<Role> roles = dao().query(Role.class, Cnd.where("id", "in", ids));
		User user = fetch(uid);
		user.setRoles(roles);
		dao().insertRelation(user, "roles");
		return Result.success();
	}

	public Result login(String userName, String password, String ip) {
		try {
			User user = this.fetchByName(userName);
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
			token.setRememberMe(true);
			subject.login(token);
			return Result.success().addData("loginUser", user);
		} catch (LockedAccountException e) {
			log.debug(e);
			return Result.fail("账户被锁定");
		} catch (Exception e) {
			log.debug(e);
			return Result.fail("登录失败");
		}
	}
}
