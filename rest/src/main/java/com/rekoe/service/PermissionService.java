package com.rekoe.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

import com.rekoe.bean.acl.Permission;
import com.rekoe.bean.acl.Role;

/**
 * @author 科技㊣²º¹³<br/>
 *         2014年2月3日 下午4:48:45<br/>
 *         http://www.rekoe.com<br/>
 *         QQ:5382211
 */
@IocBean(args = { "refer:dao" })
public class PermissionService extends BaseService<Permission> {

	public PermissionService(Dao dao) {
		super(dao);
	}

	public List<Permission> list() {
		return query(null, null);
	}

	public Map<Long, String> map() {
		Map<Long, String> map = new HashMap<Long, String>();
		List<Permission> permissions = query(null, null);
		for (Permission permission : permissions) {
			map.put(permission.getId(), permission.getName());
		}
		return map;
	}

	public Permission insert(Permission permission) {
		return dao().insert(permission);
	}

	public Permission view(Long id) {
		return fetch(id);
	}

	public int update(Permission permission) {
		return dao().update(permission);
	}

	protected int getPageNumber(Integer pageNumber) {
		return Lang.isEmpty(pageNumber) ? 1 : pageNumber;
	}

	@Inject
	private RoleService roleService;

	public Set<String> permissionInfos(String name) {
		List<Role> roles = roleService.roles(name);
		List<Permission> permissions = new ArrayList<>();
		for (Role role : roles) {
			role = dao().fetchLinks(role, "permissions");
			if (Lang.isEmpty(role.getPermissions())) {
				continue;
			}
			permissions.addAll(role.getPermissions());
		}

		final Set<String> target = new HashSet<>();
		Lang.each(permissions, new Each<Permission>() {

			@Override
			public void invoke(int arg0, Permission permission, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				target.add(permission.getName());
			}
		});
		return target;
	}
}
