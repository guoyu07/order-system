package cn.com.xbed.app.dao.basedao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import cn.com.xbed.app.commons.util.SqlCreatorTool;
import cn.com.xbed.app.commons.exception.ExceptionHandler;
import cn.com.xbed.app.dao.basedao.intf.IBaseDao;

@Repository
public class BaseDaoImpl implements IBaseDao {

	@Resource
	protected JdbcTemplate jdbcTemplate;// 子类可用

	@Resource
	protected JdbcTemplate jdbcTemplateRead;

	ExceptionHandler exceptionHandler = new ExceptionHandler(LogFactory.getLog(BaseDaoImpl.class));

	/**
	 * 批量新增实体【一定要注意该方法不支持事务,在抛出异常的时候不回滚】
	 * 
	 * @param entityList
	 *            新增的实体类集合, 主键值可以指定或忽略。
	 * @param primaryKeyProperty
	 *            必传, 指定实体类中对应的主键的属性名。
	 * @param doesIgnoreAutoGenPk
	 *            是否忽略自动增长的主键, true则生成的SQL忽略主键, false则反之
	 * @return an array containing the numbers of rows affected by each update
	 *         in the batch
	 */
	public <T> int[] batchInsertEntity(List<T> entityList, String primaryKeyProperty, boolean doesIgnoreAutoGenPk) {
		try {
			String sql = "";
			List<Object[]> params = new ArrayList<>();
			int sz = entityList.size();
			for (int i = 0; i < sz; i++) {
				T entity = entityList.get(i);
				Map<String, Object> resultMap = SqlCreatorTool.getInsertSqlAndParams(entity, primaryKeyProperty, doesIgnoreAutoGenPk);
				sql = (String) resultMap.get("sql");
				Object[] objArr = (Object[]) resultMap.get("params");
				params.add(objArr);
			}
			return jdbcTemplate.batchUpdate(sql, params);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
	}

	/**
	 * 新增实体
	 * 
	 * @param entity
	 *            新增的实体类, 主键值可以指定或忽略。
	 * @param primaryKeyProperty
	 *            必传, 指定实体类中对应的主键的属性名。
	 * @param doesIgnoreAutoGenPk
	 *            是否忽略自动增长的主键, true则生成的SQL忽略主键, false则反之
	 * @return true 新增成功, false 新增失败
	 */
	public <T> boolean insertEntity(T entity, String primaryKeyProperty, boolean doesIgnoreAutoGenPk) {
		boolean result = false;
		try {
			Map<String, Object> resultMap = SqlCreatorTool.getInsertSqlAndParams(entity, primaryKeyProperty, doesIgnoreAutoGenPk);
			result = insert((String) resultMap.get("sql"), (Object[]) resultMap.get("params"));
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return result;
	}

	/**
	 * 新增实体
	 * 
	 * @param entity
	 *            新增的实体类, 主键值可以指定或忽略。
	 * @param primaryKeyProperty
	 *            必传, 指定实体类中对应的主键的属性名。
	 * @param doesIgnoreAutoGenPk
	 *            是否忽略自动增长的主键, true则生成的SQL忽略主键, false则反之
	 * @return 返回新增的主键
	 */
	public <T> Serializable insertEntityAndGetPk(T entity, String primaryKeyProperty, boolean doesIgnoreAutoGenPk) {
		Serializable result = -1;
		try {
			Map<String, Object> resultMap = SqlCreatorTool.getInsertSqlAndParams(entity, primaryKeyProperty, doesIgnoreAutoGenPk);
			result = insertAndGetPk((String) resultMap.get("sql"), (Object[]) resultMap.get("params"));
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return result;
	}

	/**
	 * 执行插入语句, 返回true插入成功, false则失败。
	 * 
	 * @param sql
	 * @return
	 */
	public boolean insert(String sql) {
		boolean result = false;
		try {
			result = jdbcTemplate.update(sql) > 0 ? true : false;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return result;
	}

	/**
	 * 执行插入语句, 返回true插入成功, false则失败。
	 * 
	 * @param sql
	 * @param args
	 *            SQL语句的参数, 没有的时候传null, 或者调用无参的重载方法。
	 * @return
	 */
	public boolean insert(String sql, final Object[] args) {
		boolean result = false;
		try {
			if (args == null) {
				return insert(sql);
			}
			result = jdbcTemplate.update(sql, new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < args.length; i++) {
						ps.setObject(i + 1, args[i]);
					}
				}
			}) > 0 ? true : false;
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return result;
	}

	/**
	 * 执行insert的sql语句, 返回自动增长的主键。若sql语句指定了主键的值, 返回的是传入的主键值。
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Serializable insertAndGetPk(final String sql, final Object[] args) {
		KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		int affected = -1;
		try {
			affected = jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql);
					for (int i = 0; i < args.length; i++) {
						ps.setObject(i + 1, args[i]);
					}
					return ps;// conn由回调传入, 可以猜测conn由spring容器进行管理事务,
								// ps的话肯定也不能在这里close(), 应该也是自动管理的
				}
			}, generatedKeyHolder);
			/*
			 * 这个方法在 5.1.7 之前的任何一个版本都是可行的，但在 5.1.7 这个JDBC驱动版本就会抛出一个异常：
			 * java.sql.SQLException: !Statement.GeneratedKeysNotRequested!
			 */

		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return generatedKeyHolder.getKey();
	}

	/**
	 * 修改的实体, 注意这个实体一定要有主键值。
	 * 
	 * @param entity
	 *            要修改的实体, 所有非null的字段都会作为SQL的set条件(除了主键)。
	 * @param primaryKeyProperty
	 *            该实体类的主键, 注意传的不是数据库的字段值。
	 * @return 返回受影响数
	 */
	public <T> int updateEntityByPk(T entity, String primaryKeyProperty) {
		int result = -1;
		try {
			Map<String, Object> resultMap = SqlCreatorTool.getUpdateSqlAndParams(entity, primaryKeyProperty);
			result = updateOrDelete((String) resultMap.get("sql"), (Object[]) resultMap.get("params"));
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return result;
	}

	/**
	 * 执行update或deleted的sql语句, 返回受影响数。 也支持insert语句, 但是不推荐使用该方法,
	 * 该方法返回受影响数,但不返回主键值。
	 * 
	 * @param sql
	 * @return 返回受影响数
	 */
	public int updateOrDelete(String sql) {
		int affected = -1;
		try {
			affected = jdbcTemplate.update(sql);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return affected;
	}

	/**
	 * 执行update或deleted的sql语句, 返回受影响数。 也支持insert语句, 但是不推荐使用该方法, 该方法返回受影响数,
	 * 但不返回主键值。
	 * 
	 * @param sql
	 *            待执行的SQL语句
	 * @param args
	 * @return 返回受影响数
	 */
	public int updateOrDelete(String sql, final Object[] args) {
		int affected = -1;
		try {
			affected = jdbcTemplate.update(sql, new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < args.length; i++) {
						ps.setObject(i + 1, args[i]);
					}
				}
			});

		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return affected;
	}

	/**
	 * 对于CRUD操作的R方法,要从这个方法里获取究竟用读库还是写库<br>
	 * 对于有特殊要求的R方法(查询),实际中也有可能需要从写库进行读取
	 * 
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return
	 */
	private JdbcTemplate decideRMethodJdbcTemplate(boolean... isQryMaster) {
		JdbcTemplate whitchJdbcTemplate = jdbcTemplateRead;
		if (isQryMaster.length > 0 && isQryMaster[0]) {
			whitchJdbcTemplate = jdbcTemplate;
		}
		return whitchJdbcTemplate;
	}

	/**
	 * 查询返回的数据结构是List<Map<String, Object>>, 注意: 联表查询的时候, 不同表相同字段名会导致后面的字段值覆盖前面的,
	 * 解决办法是在SQL语句中起别名！！！若要使用分页和分页排序,体现在SQL语句中即可。
	 * 
	 * @param sql
	 * @param args
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return 返回结果不会是null,但可能是size0
	 */
	public List<Map<String, Object>> queryMapList(String sql, Object[] args, boolean... isQryMaster) {
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			RowMapper<Map<String, Object>> rowMapper = new ColumnMapRowMapper();
			list = this.decideRMethodJdbcTemplate(isQryMaster).query(sql, args, rowMapper);// 返回的是ArrayList类型,
			// 源码显示里面装的是Map<String,
			// Object>
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return list;
	}

	/**
	 * 查询返回的数据结构是List<Map<String, Object>>, 注意: 联表查询的时候, 不同表相同字段名会导致后面的字段值覆盖前面的,
	 * 解决办法是在SQL语句中起别名！！！若要使用分页和分页排序,体现在SQL语句中即可。
	 * 
	 * @param sql
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return 返回结果不会是null,但可能是size0
	 */
	public List<Map<String, Object>> queryMapList(String sql, boolean... isQryMaster) {
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			list = queryMapList(sql, null, isQryMaster);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return list;
	}

	/**
	 * 查询单行单列的值, 例如select count(*)是Long
	 * 
	 * @param clazz
	 *            如select count(*)语句传入Long.class或String.class或Integer.class。
	 *            查询结果必须是单行且单列记录, 否则都会抛异常。
	 * @param sql
	 * @param args
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return
	 */
	public <T> T queryForSingleColumnAndRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster) {
		T t = null;
		try {
			t = this.decideRMethodJdbcTemplate(isQryMaster).queryForObject(sql, args, clazz);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return t;
	}

	/**
	 * 查询单行单列的值, 例如select count(*)是Long
	 * 
	 * @param clazz
	 *            如select count(*)语句传入Long.class或String.class或Integer.class。
	 *            查询结果必须是单行且单列记录, 否则都会抛异常。
	 * @param sql
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return
	 */
	public <T> T queryForSingleColumnAndRow(Class<T> clazz, String sql, boolean... isQryMaster) {
		T t = null;
		try {
			t = queryForSingleColumnAndRow(clazz, sql, null, isQryMaster);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return t;
	}

	/**
	 * 查询单行记录, 例如通过主键查询单个的实体。
	 * 
	 * @param clazz
	 *            查询结果要封装成的实体类
	 * @param sql
	 *            待执行的SQL语句
	 * @param args
	 *            若没有参数传null, 但建议调用重载的方法
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return 有唯一值返回第一条值，有多个值抛出异常，无记录返回null
	 */
	public <T> T queryForSingleRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster) {
		try {
			List<T> list = queryForMultiRow(clazz, sql, args, isQryMaster);
			if (!list.isEmpty()) {
				if (list.size() > 1) {
					throw new RuntimeException("查出多条记录，期待1条");
				}
				return list.get(0);
			}
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return null;
	}

	/**
	 * 查询单行记录, 例如通过主键查询单个的实体。没有查询参数。
	 * 
	 * @param clazz
	 *            查询结果要封装成的实体类
	 * @param sql
	 *            待执行的SQL语句
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return 有唯一值返回第一条值，有多个值抛出异常，无记录返回null
	 */
	public <T> T queryForSingleRow(Class<T> clazz, String sql, boolean... isQryMaster) {
		T t = null;
		try {
			t = queryForSingleRow(clazz, sql, null, isQryMaster);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return t;
	}

	/**
	 * 查询多行记录,
	 * 
	 * @param clazz
	 *            查询结果要封装成的实体类。若要使用分页和分页排序,体现在SQL语句中即可。
	 * @param sql
	 *            待执行的SQL语句
	 * @param args
	 *            若没有参数传null, 但建议调用重载的方法
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return 返回结果不会是null,但可能是size0
	 */
	public <T> List<T> queryForMultiRow(Class<T> clazz, String sql, Object[] args, boolean... isQryMaster) {
		List<T> list = new ArrayList<>();
		try {
			BeanPropertyRowMapper<T> beanMapper = new BeanPropertyRowMapper<T>(clazz);
			list = this.decideRMethodJdbcTemplate(isQryMaster).query(sql, args, beanMapper);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return list;
	}

	/**
	 * 查询多行记录,
	 * 
	 * @param clazz
	 *            查询结果要封装成的实体类。若要使用分页和分页排序,体现在SQL语句中即可。
	 * @param sql
	 *            待执行的SQL语句
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return 返回结果不会是null,但可能是size0
	 */
	public <T> List<T> queryForMultiRow(Class<T> clazz, String sql, boolean... isQryMaster) {
		List<T> list = new ArrayList<>();
		try {
			list = queryForMultiRow(clazz, sql, null, isQryMaster);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return list;
	}

	/**
	 * 使用POJO作为例子来查询
	 * 
	 * @param entity
	 *            例子实例，若所有字段都是null，则不会拼任何查询条件，即查出所有; 如果传了ID值(只要传了ID值)就只会查出一个;
	 *            其他情况有可能查出多个
	 * @param primaryKeyProperty
	 *            该POJO的主键属性
	 * @param clazz
	 *            返回值的类型
	 * @param isQryMaster
	 *            true则在主库里查询,false或不传则按照正常逻辑(查询方法走查库,写方法走写库)
	 * @return 返回结果不会是null,但可能是size0
	 */
	public <T> List<T> queryForMultiRowByExample(T entity, String primaryKeyProperty, Class<T> clazz, boolean... isQryMaster) {
		List<T> list = new ArrayList<>();
		try {
			Map<String, Object> resultMap = SqlCreatorTool.getSelectSqlByExample(entity, primaryKeyProperty);
			list = queryForMultiRow(clazz, (String) resultMap.get("sql"), (Object[]) resultMap.get("params"), isQryMaster);
		} catch (Exception e) {
			throw exceptionHandler.logDaoException(e);
		}
		return list;
	}

}
