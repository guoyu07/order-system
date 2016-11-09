package cn.com.xbed.app.commons.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.xbed.app.bean.XbRoomCtrlSheet;

public class SqlCreatorTool {
	private static boolean isCamel = false;// 数据库表名、字段名是否驼峰的命名
	
	/**
	 * 获得select 语句的字段,如ctrl_type ctrlType,begin_dtm beginDtm,...
	 * @param entity
	 * @return
	 */
	public static <T> String getSelectFields(Class<T> clazz) {
		try {
			Object entity = clazz.newInstance(); 
			Map<String, Object> propertyMap = BeanUtilTool.getBeanPropertyMap(entity);
			StringBuffer buf = new StringBuffer();
			for (Iterator<String> iterator = propertyMap.keySet().iterator(); iterator.hasNext();) {
				String propertyName = iterator.next();
				if (!propertyName.equals("class")) {
					String field = getDbFieldByProperyName(propertyName, isCamel);
					buf.append(field).append(" ").append(propertyName).append(",");
				}
			}
			if (buf.length() > 0) {
				buf.deleteCharAt(buf.length() -1);
			}
			return buf.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param clazz
	 * @param alias 前缀别名,例如传入a就会在字段前加上a.
	 * @return
	 */
	public static <T> String getSelectFields(Class<T> clazz, String alias) {
		try {
			Object entity = clazz.newInstance(); 
			Map<String, Object> propertyMap = BeanUtilTool.getBeanPropertyMap(entity);
			StringBuffer buf = new StringBuffer();
			for (Iterator<String> iterator = propertyMap.keySet().iterator(); iterator.hasNext();) {
				String propertyName = iterator.next();
				if (!propertyName.equals("class")) {
					String field = getDbFieldByProperyName(propertyName, isCamel);
					buf.append(alias).append(".").append(field).append(" ").append(propertyName).append(",");
				}
			}
			if (buf.length() > 0) {
				buf.deleteCharAt(buf.length() -1);
			}
			return buf.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static <T> Map<String, Object> getSelectSqlByExample(T entity, String primaryKeyProperty) {
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String tableName = getTableNameByEntity(entity.getClass(), isCamel);
			StringBuffer sql = new StringBuffer("select * from ").append(tableName);
			Map<String, Object> propertyMap = BeanUtilTool.getBeanPropertyMap(entity);
			List<Object> params = new ArrayList<Object>();
			boolean flag = true;
			for (Iterator<String> iterator = propertyMap.keySet().iterator(); iterator.hasNext();) {
				String propertyName = iterator.next();
				if (!propertyName.equals("class")) {
					Object propertyValue = propertyMap.get(propertyName);
					if (propertyValue == null) {
						continue;
					}
					if (flag) {
						sql.append(" where ");
						flag = false;
					} else {
						sql.append(" and ");
					}
					sql.append(getDbFieldByProperyName(propertyName, isCamel)).append("=?");
					params.add(propertyValue);
				}
			}
			resultMap.put("sql", sql.toString());
			resultMap.put("params", params.toArray());
			return resultMap;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param entity
	 * @param primaryKeyProperty 指定实体的属性名(注意不是库表的字段名)
	 * @param doesIgnoreAutoGenPk 是否忽略自动增长的主键, true则生成的SQL忽略主键, false则反之
	 * @return
	 */
	public static <T> Map<String, Object> getInsertSqlAndParams(T entity, String primaryKeyProperty, boolean doesIgnoreAutoGenPk) {
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String tableName = getTableNameByEntity(entity.getClass(), isCamel);
			StringBuffer sql = new StringBuffer("insert into ").append(tableName).append("(");
			Map<String, Object> propertyMap = BeanUtilTool.getBeanPropertyMap(entity);
			List<Object> params = new ArrayList<Object>();
			for (Iterator<String> iterator = propertyMap.keySet().iterator(); iterator.hasNext();) {
				String propertyName = iterator.next();
				if (!propertyName.equals("class")) {
					if (propertyName.equals(primaryKeyProperty) && doesIgnoreAutoGenPk) {// 是主键值
						continue;
					}
					Object propertyValue = propertyMap.get(propertyName);
					if (propertyValue == null) {
						continue;
					}
					sql.append(getDbFieldByProperyName(propertyName, isCamel)).append(",");
					params.add(propertyValue);
				}
			}
			sql.deleteCharAt(sql.length() -1);
			sql.append(") values(");
			for (int i = 0; i < params.size(); i++) {
				sql.append("?,");
			}
			sql.deleteCharAt(sql.length() -1);
			sql.append(")");
			resultMap.put("sql", sql.toString());
			resultMap.put("params", params.toArray());
			return resultMap;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	
	
	/**
	 * 获取update的SQL语句
	 * @param entity
	 * @param primaryKeyProperty
	 * @return
	 */
	public static <T> Map<String, Object> getUpdateSqlAndParams(T entity, String primaryKeyProperty) {
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String tableName = getTableNameByEntity(entity.getClass(), isCamel);
			StringBuffer sql = new StringBuffer("update ").append(tableName).append(" set ");
			Map<String, Object> propertyMap = BeanUtilTool.getBeanPropertyMap(entity);

			List<Object> params = new ArrayList<Object>();
			for (Iterator<String> iterator = propertyMap.keySet().iterator(); iterator.hasNext();) {
				String propertyName = iterator.next();
				if (!propertyName.equals("class") && !propertyName.equals(primaryKeyProperty)) {// 从Object继承来的方法,
																								// 要去掉
					Object propertyValue = propertyMap.get(propertyName);
					if (propertyValue == null) {
						continue;
					}
					sql.append(getDbFieldByProperyName(propertyName, isCamel)).append("=?").append(",");
					params.add(propertyValue);
				}

			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" where ").append(getDbFieldByProperyName(primaryKeyProperty, isCamel)).append("=?");
			params.add(BeanUtilTool.getBeanPropertyValue(entity, primaryKeyProperty));
			resultMap.put("sql", sql.toString());
			resultMap.put("params", params.toArray());
			return resultMap;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据POJO实体名字获取对应的表名, 如果表名跟实体名一致, isSameAsEntityName传true,
	 * 如果不一致传false。例如实体名是UserInfo而表名是user_info则传false
	 * 
	 * @param clazz
	 * @param isSameAsEntityName
	 * @return
	 */
	protected static <T> String getTableNameByEntity(Class<T> clazz, boolean isSameAsEntityName) {
		try {
			return isSameAsEntityName ? clazz.getSimpleName() : transferCamel2underline(clazz.getSimpleName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 从属性名得到对应的库表字段名, 如果属性名和字段名一样, 传true, 如果不一样,
	 * 传false(例如属性是userName而字段是user_name)
	 * 
	 * @param propertyName
	 * @return
	 */
	protected static String getDbFieldByProperyName(String propertyName, boolean isSameAsPropertyName) {
		try {
			return isSameAsPropertyName ? propertyName : transferCamel2underline(propertyName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将驼峰的名字转成下划线形式, 如userName转成user_name, userId转成user_id等
	 * 
	 * @param propertyName
	 * @return
	 */
	protected static String transferCamel2underline(String camel) {
		try {
			String s = underscoreName(camel);
			return s == null ? "" : s.toLowerCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将驼峰式命名的字符串转换为下划线大写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。</br>
	 * 例如：HelloWorld->HELLO_WORLD
	 * 
	 * @param name
	 *            转换前的驼峰式命名的字符串
	 * @return 转换后下划线大写方式命名的字符串
	 */
	protected static String underscoreName(String name) {
		StringBuilder result = new StringBuilder();
		if (name != null && name.length() > 0) {
			// 将第一个字符处理成大写
			result.append(name.substring(0, 1).toUpperCase());
			// 循环处理其余字符
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				// 在大写字母前添加下划线
				if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
					result.append("_");
				}
				// 其他字符直接转成大写
				result.append(s.toUpperCase());
			}
		}
		return result.toString();
	}

	/**
	 * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。</br>
	 * 例如：HELLO_WORLD->HelloWorld
	 * 
	 * @param name
	 *            转换前的下划线大写方式命名的字符串
	 * @return 转换后的驼峰式命名的字符串
	 */
	protected static String camelName(String name) {
		StringBuilder result = new StringBuilder();
		// 快速检查
		if (name == null || name.isEmpty()) {
			// 没必要转换
			return "";
		} else if (!name.contains("_")) {
			// 不含下划线，仅将首字母小写
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		// 用下划线将原始字符串分割
		String camels[] = name.split("_");
		for (String camel : camels) {
			// 跳过原始字符串中开头、结尾的下换线或双重下划线
			if (camel.isEmpty()) {
				continue;
			}
			// 处理真正的驼峰片段
			if (result.length() == 0) {
				// 第一个驼峰片段，全部字母都小写
				result.append(camel.toLowerCase());
			} else {
				// 其他的驼峰片段，首字母大写
				result.append(camel.substring(0, 1).toUpperCase());
				result.append(camel.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}
}
