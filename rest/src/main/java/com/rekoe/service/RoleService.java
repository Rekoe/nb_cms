package com.rekoe.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

import com.rekoe.bean.acl.Permission;
import com.rekoe.bean.acl.Role;
import com.rekoe.bean.acl.User;
import com.rekoe.vo.Result;

/**
 * @author 科技㊣²º¹³M<br/>
 *         2014年2月3日 下午4:48:45 <br/>
 *         http://www.rekoe.com <br/>
 *         QQ:5382211
 */
@IocBean(args = { "refer:dao" })
public class RoleService extends BaseService<Role> {

	public RoleService(Dao dao) {
		super(dao);
	}

	public List<Role> list() {
		return query(null, null);
	}

	public Role insert(Role role) {
		role = dao().insert(role);
		return dao().insertRelation(role, "permissions");
	}

	public void delete(Long id) {
		dao().delete(Role.class, id);
		dao().clear("system_role_permission", Cnd.where("roleid", "=", id));
		dao().clear("system_user_role", Cnd.where("roleid", "=", id));
	}

	public Role view(Long id) {
		return dao().fetchLinks(fetch(id), "permissions");
	}

	public int update(Role role) {
		return dao().update(role);
	}

	public Role fetchByName(String name) {
		return fetch(Cnd.where("name", "=", name));
	}

	public List<String> getPermissionNameList(Role role) {
		List<String> permissionNameList = new ArrayList<String>();
		for (Permission permission : role.getPermissions()) {
			permissionNameList.add(permission.getName());
		}
		return permissionNameList;
	}

	public void updateRoleRelation(Role role, List<Permission> perms) {
		dao().clearLinks(role, "permissions");
		role.getPermissions().clear();
		dao().update(role);
		if (!Lang.isEmpty(perms)) {
			role.setPermissions(perms);
			dao().insertRelation(role, "permissions");
		}
	}

	public Map<Long, String> map() {
		Map<Long, String> map = new HashMap<Long, String>();
		List<Role> roles = query(null, null);
		for (Role role : roles) {
			map.put(role.getId(), role.getName());
		}
		return map;
	}

	public void addPermission(Long roleId, Long permissionId) {
		dao().insert("system_role_permission", Chain.make("roleid", roleId).add("permissionid", permissionId));
	}

	public void removePermission(Long roleId, Long permissionId) {
		dao().clear("system_role_permission", Cnd.where("roleid", "=", roleId).and("permissionid", "=", permissionId));
	}

	public List<Role> loadRoles(Integer[] ids) {
		return dao().query(getEntityClass(), Cnd.where("id", "iN", ids));
	}

	public Result setPermission(long[] ids, long roleId) {
		Role role = dao().fetch(getEntityClass(), Cnd.where("id", "=", roleId));
		List<Permission> perms = dao().query(Permission.class, Cnd.where("id", "in", ids));
		updateRoleRelation(role, perms);
		return Result.success();
	}

	public List<String> roleInfos(String userName) {
		final List<String> target = new ArrayList<>();
		Lang.each(roles(userName), new Each<Role>() {

			@Override
			public void invoke(int arg0, Role ele, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				target.add(ele.getName());
			}
		});
		return target;
	}

	@Inject
	private UserService userService;
	public List<Role> roles(String name) {
		User user = dao().fetch(User.class, Cnd.where("name", "=", name));
		if (!Lang.isEmpty(user)) {
			user = dao().fetchLinks(user, "roles");
			return user.getRoles();
		}
		return new ArrayList<>();
	}
	
	public List<Record> getRoles(long userid){
		Sql sql = Sqls.create("SELECT r.id,r.description as description,CASE sur.userid IS NULL WHEN 1 THEN FALSE ELSE TRUE END AS selected FROM system_role r LEFT JOIN (SELECT * FROM system_user_role WHERE userid = @id) sur ON r.id = sur.roleid");
		sql.setCallback(Sqls.callback.records());
		sql.params().set("id", userid);
		dao().execute(sql);
		return sql.getList(Record.class);
	}
}
