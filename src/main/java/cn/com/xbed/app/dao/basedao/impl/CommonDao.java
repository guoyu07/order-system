package cn.com.xbed.app.dao.basedao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.com.xbed.app.dao.basedao.intf.IBaseDao;

public class CommonDao implements IBaseDao {
	@Resource(name="baseDaoImplV2")
	private IBaseDao baseDaoImpl;

	@Override
	public <T> int[] batchInsertEntity(List<T> entityList, String primaryKeyProperty, boolean doesIgnoreAutoGenPk) {
		return baseDaoImpl.batchInsertEntity(entityList, primaryKeyProperty, doesIgnoreAutoGenPk);
	}

	@Override
	public <T> boolean insertEntity(T entity, String primaryKeyProperty, boolean doesIgnoreAutoGenPk) {
		return baseDaoImpl.insertEntity(entity, primaryKeyProperty, doesIgnoreAutoGenPk);
	}

	@Override
	public <T> Serializable insertEntityAndGetPk(T entity, String primaryKeyProperty, boolean doesIgnoreAutoGenPk) {
		return baseDaoImpl.insertEntityAndGetPk(entity, primaryKeyProperty, doesIgnoreAutoGenPk);
	}

	@Override
	public <T> int updateEntityByPk(T entity, String primaryKeyProperty) {
		return baseDaoImpl.updateEntityByPk(entity, primaryKeyProperty);
	}

	@Override
	public List<Map<String, Object>> queryMapList(String sql, Object[] args, boolean... isQryMaster) {
		return baseDaoImpl.queryMapList(sql, args, isQryMaster);
	}

	@Override
	public List<Map<String, Object>> queryMapList(String sql, boolean... isQryMaster) {
		return baseDaoImpl.queryMapList(sql, isQryMaster);
	}

	@Override
	public <T> T queryForSingleColumnAndRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster) {
		return baseDaoImpl.queryForSingleColumnAndRow(clazz, sql, args, isQryMaster);
	}

	@Override
	public <T> T queryForSingleColumnAndRow(Class<T> clazz, String sql, boolean... isQryMaster) {
		return baseDaoImpl.queryForSingleColumnAndRow(clazz, sql, isQryMaster);
	}

	@Override
	public <T> T queryForSingleRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster) {
		return baseDaoImpl.queryForSingleRow(clazz, sql, args, isQryMaster);
	}

	@Override
	public <T> T queryForSingleRow(Class<T> clazz, String sql, boolean... isQryMaster) {
		return baseDaoImpl.queryForSingleRow(clazz, sql, isQryMaster);
	}

	@Override
	public <T> List<T> queryForMultiRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster) {
		return baseDaoImpl.queryForMultiRow(clazz, sql, args, isQryMaster);
	}

	@Override
	public <T> List<T> queryForMultiRow(Class<T> clazz, String sql, boolean... isQryMaster) {
		return baseDaoImpl.queryForMultiRow(clazz, sql, isQryMaster);
	}

	@Override
	public <T> List<T> queryForMultiRowByExample(T entity, String primaryKeyProperty, Class<T> clazz, boolean... isQryMaster) {
		return baseDaoImpl.queryForMultiRowByExample(entity, primaryKeyProperty, clazz, isQryMaster);
	}

	@Override
	public int updateOrDelete(String sql, Object[] args) {
		return baseDaoImpl.updateOrDelete(sql, args);
	}

	@Override
	public int updateOrDelete(String sql) {
		return baseDaoImpl.updateOrDelete(sql);
	}

	@Override
	public boolean insert(String sql, Object[] args) {
		return baseDaoImpl.insert(sql, args);
	}

	@Override
	public boolean insert(String sql) {
		return baseDaoImpl.insert(sql);
	}

	@Override
	public Serializable insertAndGetPk(String sql, Object[] args) {
		return baseDaoImpl.insertAndGetPk(sql, args);
	}
}
