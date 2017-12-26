package com.rekoe.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.credential.Sha256CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.nutz.castor.Castors;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import com.rekoe.bean.acl.User;
import com.rekoe.service.PermissionService;
import com.rekoe.service.RoleService;
import com.rekoe.service.UserService;

@SuppressWarnings("deprecation")
@IocBean(create = "_init")
public class LoginRealm extends AuthorizingRealm {

	@Inject
	private UserService userService;

	private HashedCredentialsMatcher credentialsMatcher = new Sha256CredentialsMatcher();

	@Inject
	private org.apache.shiro.cache.CacheManager shiroCacheManager;

	public void _init() {
		credentialsMatcher.setStoredCredentialsHexEncoded(false);
		credentialsMatcher.setHashIterations(1024);
		credentialsMatcher.setHashSalted(true);
		setCacheManager(shiroCacheManager);
	}

	@Override
	public CredentialsMatcher getCredentialsMatcher() {
		return credentialsMatcher;
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String userName = upToken.getUsername();
		User user = userService.fetchByName(userName);
		if (Lang.isEmpty(user))// 用户不存在
			return null;
		if (user.isLocked())// 用户被锁定
			throw new LockedAccountException("Account [" + upToken.getUsername() + "] is locked.");
		ByteSource salt = ByteSource.Util.bytes(user.getSalt());
		Long uid = user.getId();
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(uid, user.getPassword(), getName());
		info.setCredentialsSalt(salt);
		return info;
	}

	@Inject
	private RoleService roleService;

	@Inject
	private PermissionService permissionService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		Object obj = principalCollection.getPrimaryPrincipal();
		Long uid = Castors.me().castTo(obj, Long.class);
		User user = userService.fetch(uid);
		List<String> roleNameList = roleService.roleInfos(user.getName());
		List<String> permissionNames = new ArrayList<>();
		permissionNames.addAll(permissionService.permissionInfos(user.getName()));
		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		auth.addRoles(roleNameList);// 添加角色
		auth.addStringPermissions(permissionNames);// 添加权限
		return auth;
	}
}
