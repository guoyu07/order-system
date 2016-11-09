package cn.com.xbed.app.zztest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.xbed.app.commons.util.BeanUtilTool;
import cn.com.xbed.app.commons.util.ConsolePrinter;

public class ValidDbAndBeans {

	/**
	 * 检查数据库和bean的字段的对应情况，经常会有改了库表但是忘了相应改bean <br>
	 * 
	 * 用于比对库表和bean对象不一致的情况
	 * 
	 * @param path
	 * @param order
	 */
	public static void validDbAndBeans(String[] packagePath) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 连接库表
			String url = "jdbc:mysql://120.25.106.243:3306/xbed_service";
			String driver = "com.mysql.jdbc.Driver";
			Class.forName(driver);
			conn = DriverManager.getConnection(url, "db_admin", "db_admin2015");
			ConsolePrinter.println("能连上库表吗: " + conn);

			// 获得库表所有的表名+表字段
			DatabaseMetaData metaData = conn.getMetaData();
			rs = metaData.getTables(conn.getCatalog(), "root", null, new String[] { "TABLE" });

			List<String> allTableName = new ArrayList<>();
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				allTableName.add(tableName);
			}

			// 键是table_name，值是该表的 {field_name:field_type}
			LinkedHashMap<String, LinkedHashMap<String, String>> allTables = new LinkedHashMap<>();
			for (int i = 0; i < allTableName.size(); i++) {
				String tableName = allTableName.get(i);
				allTables.put(tableName, new LinkedHashMap<String, String>());
				rs = metaData.getColumns(null, "%", allTableName.get(i), "%");
				while (rs.next()) {
					String columnName = rs.getString("COLUMN_NAME");
					String columnType = rs.getString("TYPE_NAME");
					allTables.get(tableName).put(columnName, columnType);
				}
			}

			// 获得该包下的所有的类(不包括子目录)
			Set<Object> allClasses = new HashSet<>();
			for (int i = 0; i < packagePath.length; i++) {
				Set<Object> tmp = Snippet.getObjectsInPackage(packagePath[i]);
				allClasses.addAll(tmp);
			}

			// 键是类名，值是{属性名:属性值}
			LinkedHashMap<String, Map<String, Object>> allBeans = getBeanMap(allClasses);
			// 键是类名转非驼峰，值是{属性名转非驼峰:属性值}
			LinkedHashMap<String, Map<String, Object>> allBeans2Table = transBean2Table(allBeans);

			// 用【bean】去比较【库表】
			System.out.println("一、 用 bean 去比较 库表 ");
			for (String tableName : allBeans2Table.keySet()) {
				System.out.println("--------- 比对BEAN " + tableName + " -----------");
				if (allTables.get(tableName) != null) {
					// 进一步比对字段
					LinkedHashMap<String, String> fieldMapFromDb = allTables.get(tableName);// 库表字段
					for (String field : allBeans2Table.get(tableName).keySet()) {
						if (!fieldMapFromDb.containsKey(field)) {
							System.out.println("【ERROR.类字段】 " + tableName + " 的字段 " + field + " 在库表中没有对应");
						}
					}

				} else {
					System.out.println("【ERROR.类】 " + tableName + " 在库表中没有对应的表");
				}
			}

			System.out.println("\n\n\n二、 用 库表 去比较 bean ");
			for (String tableName : allTables.keySet()) {
				System.out.println("--------- 比对TABLE " + tableName + " -----------");
				if (allBeans2Table.get(tableName) != null) {
					// 进一步比较
					// 该表所有的字段
					Map<String, Object> fieldmapFromBean = allBeans2Table.get(tableName);
					for (String field : allTables.get(tableName).keySet()) {
						if (!fieldmapFromBean.containsKey(field)) {
							System.out.println("【ERROR.表字段】 " + tableName + " 的字段 " + field + " 在程序中没有对应");
						}
					}
				} else {
					System.out.println("【ERROR.表】 " + tableName + " 在程序中没有对应的bean");
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private static LinkedHashMap<String, Map<String, Object>> transBean2Table(
			LinkedHashMap<String, Map<String, Object>> mapIn) {
		try {
			LinkedHashMap<String, Map<String, Object>> mapOut = new LinkedHashMap<>();
			for (String tableName : mapIn.keySet()) {
				Map<String, Object> mm = mapIn.get(tableName);
				Map<String, Object> out = new HashMap<>();
				for (String pro : mm.keySet()) {
					out.put(transferCamel2underline(pro), mm.get(pro));
				}
				mapOut.put(transferCamel2underline(tableName), out);
			}
			return mapOut;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static LinkedHashMap<String, Map<String, Object>> getBeanMap(Set<Object> set) {
		LinkedHashMap<String, Map<String, Object>> map = new LinkedHashMap<>();
		for (Object object : set) {
			String className = object.getClass().getSimpleName();
			Map<String, Object> property = BeanUtilTool.getBeanPropertyMap(object);// 获得bean的所有属性
			property.remove("class");
			map.put(className, property);
		}
		return map;
	}

	private static String transferCamel2underline(String camel) {
		try {
			String s = underscoreName(camel);
			return s == null ? "" : s.toLowerCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String underscoreName(String name) {
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

	public static void main(String[] args) {
		validDbAndBeans(new String[]{"cn.com.xbed.app.bean","cn.com.xbed.support.bean","cn.com.xbed.commons.busi.bean"});
	}
}
