package cn.com.xbed.app.dao.basedao.intf;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IBaseDao {

	public <T> int[] batchInsertEntity(List<T> entityList, String primaryKeyProperty, boolean doesIgnoreAutoGenPk);
	
	public <T> boolean insertEntity(T entity, String primaryKeyProperty, boolean doesIgnoreAutoGenPk);

	public <T> Serializable insertEntityAndGetPk(T entity, String primaryKeyProperty, boolean doesIgnoreAutoGenPk);

	public boolean insert(String sql);

	public boolean insert(String sql, final Object[] args);

	public Serializable insertAndGetPk(final String sql, final Object[] args);

	public <T> int updateEntityByPk(T entity, String primaryKeyProperty);

	public int updateOrDelete(String sql);

	public int updateOrDelete(String sql, final Object[] args);

	public List<Map<String, Object>> queryMapList(String sql, Object[] args, boolean... isQryMaster);

	public List<Map<String, Object>> queryMapList(String sql, boolean... isQryMaster);

	public <T> T queryForSingleColumnAndRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster);

	public <T> T queryForSingleColumnAndRow(Class<T> clazz, String sql, boolean... isQryMaster);

	public <T> T queryForSingleRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster);

	public <T> T queryForSingleRow(Class<T> clazz, String sql, boolean... isQryMaster);

	public <T> List<T> queryForMultiRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster);

	public <T> List<T> queryForMultiRow(Class<T> clazz, String sql, boolean... isQryMaster);

	public <T> List<T> queryForMultiRowByExample(T entity, String primaryKeyProperty, Class<T> clazz, boolean... isQryMaster);

}