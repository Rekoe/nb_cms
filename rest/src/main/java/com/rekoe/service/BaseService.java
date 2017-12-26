package com.rekoe.service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Logs;
import org.nutz.service.IdNameEntityService;

import com.rekoe.service.entity.DataBaseEntity;
import com.rekoe.service.entity.PageredData;

public class BaseService<T extends DataBaseEntity> extends IdNameEntityService<T> {

	protected int PAGESIZE = 15;

	public BaseService() {
	}

	public BaseService(Dao dao) {
		super(dao);
	}

	/**
	 * 保存实体
	 *
	 * @param t
	 *            待保存实体
	 * @return 保存后的实体, 根据配置可能将产生 id 等其他属性
	 */
	public T save(T t) {

		return dao().insert(t);
	}

	/**
	 * 保存对象指定字段
	 *
	 * @param obj
	 *            待保存对象
	 * @param filter
	 *            字段过滤器
	 * @return
	 */
	public T save(final T obj, FieldFilter filter) {
		return dao().insert(obj, filter);
	}

	/**
	 * 保存
	 *
	 * @param tableName
	 *            表名
	 * @param chain
	 *            数据链
	 */
	public void save(String tableName, Chain chain) {
		dao().insert(tableName, chain);
	}

	/**
	 * 保存
	 *
	 * @param classOfT
	 *            类
	 * @param chain
	 *            数据链
	 */
	public void save(Class<?> classOfT, Chain chain) {
		dao().insert(classOfT, chain);
	}

	/**
	 * 保存
	 *
	 * @param t
	 *            数据对象
	 * @param ignoreNull
	 *            是否忽略空值
	 * @param ignoreZero
	 *            是否忽略零值
	 * @param ignoreBlankStr
	 *            是否忽略空字符串
	 * @return
	 */
	public T insert(final T t, boolean ignoreNull, boolean ignoreZero, boolean ignoreBlankStr) {
		return dao().insert(t, ignoreNull, ignoreZero, ignoreBlankStr);
	}

	/**
	 * 保存指定字段
	 *
	 * @param obj
	 *            待保存对象
	 * @param regex
	 *            字段正则
	 * @return
	 */
	public T insertWith(T obj, String regex) {
		return dao().insertWith(obj, regex);
	}

	/**
	 * 保存关联数据
	 *
	 * @param obj
	 *            对象
	 * @param regex
	 *            管理字段正则
	 * @return
	 */
	public T insertLinks(T obj, String regex) {
		return dao().insertLinks(obj, regex);
	}

	/**
	 * 插入中间表
	 *
	 * @param obj
	 * @param regex
	 * @return
	 */
	public T insertRelation(T obj, String regex) {
		return dao().insertRelation(obj, regex);
	}

	// R

	/**
	 * 查询全部
	 *
	 * @return
	 */
	public List<T> queryAll() {
		return query(null);
	}

	/**
	 * 分页查询
	 *
	 * @param condition
	 *            条件
	 * @param currentPage
	 *            当前页码
	 * @param pageSize
	 *            页面大小
	 * @return
	 */
	public List<T> query(Condition condition, int currentPage, int pageSize) {
		if (condition == null) {// 不传入条件那么我就设置一个排序条件
			condition = Cnd.orderBy().desc("id");
		}
		org.nutz.dao.pager.Pager pager = dao().createPager(currentPage, pageSize);
		return dao().query(getEntityClass(), condition, pager);
	}

	/**
	 * 分页查询
	 *
	 * @param condition
	 *            条件
	 * @param currentPage
	 *            页码
	 * @return
	 */
	public List<T> query(Condition condition, int currentPage) {
		return query(condition, currentPage, PAGESIZE);
	}

	/**
	 * 根据指定字段查询(仅限唯一属性,非唯一属性查询第一个满足条件的数据)
	 *
	 * @param field
	 *            字段
	 * @param value
	 *            值
	 * @return 单个对象
	 */
	public T findByField(String field, Object value) {
		return dao().fetch(getEntityClass(), Cnd.where(field, "=", value));
	}

	// sql

	/**
	 * 执行sql并返回记录
	 * 
	 * @param sql
	 *            待执行sql
	 * @return
	 */
	public Record fetch(Sql sql) {
		sql.setCallback(Sqls.callback.record());
		dao().execute(sql);
		return sql.getObject(Record.class);
	}

	/**
	 * 执行sql并返回记录列表
	 * 
	 * @param sql
	 *            待执行sql
	 * @return
	 */
	public List<Record> search(Sql sql) {
		sql.setCallback(Sqls.callback.records());
		dao().execute(sql);
		return sql.getList(Record.class);
	}

	/**
	 * 执行sql并返回map列表,列别名作为key
	 * 
	 * @param sql
	 *            待执行sql
	 * @return
	 */
	public List<NutMap> searchAsMap(Sql sql) {
		sql.setCallback(Sqls.callback.maps());
		dao().execute(sql);
		return sql.getList(NutMap.class);
	}

	/**
	 * 执行sql并返回对象
	 * 
	 * @param sql
	 *            待执行sql
	 * @return
	 */
	public T fetchObj(Sql sql) {
		sql.setCallback(Sqls.callback.entity());
		sql.setEntity(dao().getEntity(getEntityClass()));
		dao().execute(sql);
		return sql.getObject(getEntityClass());
	}

	/**
	 * 执行sql并返回对象列表
	 * 
	 * @param sql
	 *            待执行sql
	 * @return
	 */
	public List<T> searchObj(Sql sql) {
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao().getEntity(getEntityClass()));
		dao().execute(sql);
		return sql.getList(getEntityClass());
	}

	/**
	 * 执行删除或者更新语句
	 * 
	 * @param sql
	 *            待执行sql
	 * @return 影响的记录条数
	 */
	public int deleteOrUpdate(Sql sql) {
		sql.setCallback(Sqls.callback.integer());
		dao().execute(sql);
		return sql.getUpdateCount();
	}

	/**
	 * 创建sql对象
	 *
	 * @param key
	 * @return
	 */
	public Sql create(String key) {
		return dao().sqls().create(key);
	}

	// biz

	/**
	 * 分页查询
	 *
	 * @param page
	 *            页码
	 * @return
	 */
	public PageredData<T> searchByPage(int page) {
		return searchByPage(page, null);
	}

	/**
	 * 分页查询
	 *
	 * @param page
	 *            页码
	 * @param condition
	 *            条件
	 * @return 分页对象
	 */
	public PageredData<T> searchByPage(int page, Condition condition) {
		return searchByPage(page, PAGESIZE, condition);
	}

	/**
	 * 根据条件分页查询
	 * 
	 * @param page
	 *            页码
	 * @param pageSize
	 *            页面大小
	 * @param condition
	 *            条件
	 * @return
	 */
	public PageredData<T> searchByPage(int page, int pageSize, Condition condition) {
		PageredData<T> data = new PageredData<T>();
		Pager pager = new Pager(page, pageSize);
		data.setDataList(query(condition, pager));
		pager.setRecordCount(count(condition));
		data.setPager(pager);
		return data;
	}

	/**
	 * 根据条件及关键词进行分页查询
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页面
	 * @param cnd
	 *            条件
	 * @param fields
	 *            关键词匹配的字段列表
	 * @return
	 */
	public PageredData<T> searchByKeyAndPage(String key, int page, Cnd cnd, String... fields) {
		String searchKey = String.format("%%%s%%", key);
		if (cnd == null) {
			cnd = Cnd.NEW();
		}
		SqlExpressionGroup expressionGroup = Exps.begin();
		boolean appendCnd = false;
		int index = 0;
		for (String field : fields) {
			if (index == 0) {
				expressionGroup.and(field, "like", searchKey);
			} else {
				expressionGroup.or(field, "like", searchKey);
			}
			index++;
			appendCnd = true;
		}
		if (appendCnd) {
			cnd.and(expressionGroup);
		}
		return searchByPage(page, PAGESIZE, cnd);
	}

	/**
	 * 关键词搜索
	 *
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @param fields
	 *            检索字段列表
	 * @return 分页对象
	 */
	public PageredData<T> searchByKeyAndPage(String key, int page, String... fields) {
		return searchByKeyAndPage(key, page, null, fields);
	}

	// U

	/**
	 * 更新对象
	 * 
	 * @param obj
	 *            待更新对象
	 * @return
	 */
	public int update(T obj) {
		return dao().update(obj);
	}

	/**
	 * 更新字段的指定字段
	 * 
	 * @param obj
	 *            待更新对象
	 * @param regex
	 *            字段正则
	 * @return 影响的记录条数
	 */
	@Deprecated
	public int update(final T obj, String regex) {
		return dao().update(obj, regex);
	}

	/***
	 * 更新字段的指定字段
	 * 
	 * @param obj
	 *            待更新对象
	 * @param regex
	 *            字段正则
	 * @return 影响的记录条数
	 */
	public int updateFields(final T obj, String regex) {
		return dao().update(obj, regex);
	}

	/**
	 * 更新对象的非空字段
	 * 
	 * @param obj
	 *            待更新对象
	 * @return
	 */
	public int updateIgnoreNull(final T obj) {
		return dao().updateIgnoreNull(obj);
	}

	public T updateWith(T obj, final String regex) {
		return dao().updateWith(obj, regex);
	}

	public T updateLinks(T obj, final String regex) {
		return dao().updateLinks(obj, regex);
	}

	/**
	 * 更新对象的指定字段,使用id或者主键作为条件
	 * 
	 * @param t
	 *            待更新对象
	 * @param fields
	 *            字段列表
	 * @return
	 */
	public boolean update(T t, String... fields) {
		return dao().update(t.getClass(), makeChain(t, fields), getCnd(t)) == 1;
	}

	private Chain makeChain(T t, String[] fields) {
		NutMap map = NutMap.NEW();
		for (String field : fields) {
			MappingField mf = getEntity().getField(field);
			map.put(mf.getColumnName(), mf.getValue(t));
		}
		return Chain.from(map);
	}

	private Condition getCnd(T t) {
		Mirror<T> clazzMirror = Mirror.me(t);// 获取类型的镜像
		Field idField = null;
		try {
			idField = clazzMirror.getField(Id.class);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		String fieldName = idField.getName();
		Object value = clazzMirror.getValue(t, idField);
		return Cnd.where(fieldName, "=", value);
	}

	/**
	 * 根据条件更新数据
	 * 
	 * @param t
	 *            更新目标数据样本
	 * @param cnd
	 *            条件
	 * @param fields
	 *            待更新的字段列表
	 * @return 影响的记录条数
	 */
	public int update(T t, Condition cnd, String... fields) {
		Arrays.sort(fields);
		NutMap map = Lang.map(Json.toJson(t));
		NutMap data = NutMap.NEW();
		for (String key : map.keySet()) {
			if (Arrays.binarySearch(fields, key) >= 0) {
				data.put(key, map.get(key));
			}
		}
		try {
			return dao().update(t.getClass(), Chain.from(data), cnd);
		} catch (Exception e) {
			Logs.get().error(e);
			return 0;
		}
	}

	/**
	 * 根据条件更新数据
	 * 
	 * @param t
	 *            更新目标数据样本
	 * @param cnd
	 *            条件
	 * @return
	 */
	public int update(T t, Condition cnd) {
		try {
			return dao().update(t.getClass(), Chain.from(t), cnd);
		} catch (Exception e) {
			Logs.get().error(e);
			return 0;
		}
	}

	// D

	/**
	 * 清除全部数据
	 *
	 * @return 记录条数
	 */
	@Override
	public int clear() {
		return clear(null);
	}

	/**
	 * 删除关联数据
	 *
	 * @param obj
	 * @param regex
	 * @return
	 */
	public T clearLinks(T obj, final String regex) {
		return dao().clearLinks(obj, regex);
	}

	/**
	 * 删除对象
	 * 
	 * @param obj
	 *            待删除对象
	 * @return
	 */
	public int delete(T obj) {
		return dao().delete(obj);
	}

	public int deleteWith(T obj, final String regex) {
		return dao().deleteWith(obj, regex);
	}

	public int deleteLinks(T obj, final String regex) {
		return dao().deleteLinks(obj, regex);
	}

}
