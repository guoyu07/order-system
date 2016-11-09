package cn.com.xbed.app.zztest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * 作用是读取一个excel，生成插入到地区表cm_location的sql语句
 * 
 * @author Administrator
 *
 */
public class GenerateLocationInsertSqlFromXls {

	public static class Row {
		public String provinceCode;
		public String province;
		public String cityCode;
		public String city;
		public String districtCode;
		public String district;

		public Row(String provinceCode, String province, String cityCode, String city, String districtCode, String district) {
			super();
			this.provinceCode = provinceCode;
			this.province = province;
			this.cityCode = cityCode;
			this.city = city;
			this.districtCode = districtCode;
			this.district = district;
		}

		@Override
		public String toString() {
			return "Row [provinceCode=" + provinceCode + ", province=" + province + ", cityCode=" + cityCode + ", city=" + city + ", districtCode="
					+ districtCode + ", district=" + district + "]";
		}
	}

	public static List<Row> readFromXls() throws Exception {
		List<Row> list = new ArrayList<>();

		// 创建一个list 用来存储读取的内容
		Workbook rwb = null;
		Cell cell = null;

		// 创建输入流
		InputStream stream = new FileInputStream("D:/DevelopFolder/workspaces/javaproject64/order-system/src/main/java/cn/com/xbed/app/zztest/省市区名称编码.xls");

		// 获取Excel文件对象
		rwb = Workbook.getWorkbook(stream);

		// 获取文件的指定工作表 默认的第一个
		Sheet sheet = rwb.getSheet(0);

		// 行数(表头的目录不需要，从1开始)
		for (int i = 1; i < sheet.getRows(); i++) {// 从1开始，过滤第一行的标题

			cell = sheet.getCell(0, i);
			String provinceCode = cell.getContents();
			cell = sheet.getCell(1, i);
			String province = cell.getContents();
			cell = sheet.getCell(2, i);
			String cityCode = cell.getContents();
			cell = sheet.getCell(3, i);
			String city = cell.getContents();
			cell = sheet.getCell(4, i);
			String districtCode = cell.getContents();
			cell = sheet.getCell(5, i);
			String district = cell.getContents();
			Row r = new Row(provinceCode, province, cityCode, city, districtCode, district);
			list.add(r);
		}
		//System.out.println(JsonHelper.toJSONString(list));
		return list;
	}

	public static void main(String[] args) throws Exception {
//		List<Row> l = readFromXls();
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < l.size(); i++) {
//			Row r = l.get(i);
//			System.out.println(r);
//			sb
//			.append("insert into `cm_location` (`loc_code`, `loc_name`, `loc_level`, `parent_code`, `create_dtm`, `remark`) values('")
//			.append(r.districtCode).append("','")// 地区编号
//			.append(r.district).append("','")//地区名
//			.append("DISTRICT").append("','")// 类型
//			.append(r.cityCode).append("',")// 父编号
//			.append("now(),")// 创建时间
//			.append("NULL);").append("\r\n");// 备注
//			
//			
//		}
//		FileWriter fw = new FileWriter(new File("d:\\1.sql"));
//		BufferedWriter bw = new BufferedWriter(fw);
//		bw.write(sb.toString());
	}

}