package cn.com.xbed.app.zztest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Snippet {
	/***********
	 * 根据包名来获取此包下所有的类名及其实例
	 * 
	 * @param packName
	 * @return
	 */
	public static Set<Object> getObjectsInPackage(String packName) {
		Set<Object> objs = new HashSet<Object>();
		String packageName = packName;
		String packageDirName = packageName.replace(".", "/");
		Enumeration<URL> dirs = null;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 迭代此 Enumeration
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				File file = new File(url.getFile());
				// 把此目录下的所有文件列出
				String[] classes = file.list();
				// 循环此数组，并把.class去掉
				for (String className : classes) {
					if (className.contains(".class")) {
						className = className.substring(0, className.length() - 6);
						// 拼接上包名，变成全限定名
						String qName = packageName + "." + className;
						// 如有需要，把每个类生实一个实例
						Object obj = Class.forName(qName).newInstance();
						// 添加到集合中
						objs.add(obj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objs;
	}
	
	public static List<String> getSimpleNameInPackage(String packName) {
		Set<Object> set = getObjectsInPackage(packName);
		List<String> list = new ArrayList<>();
		for (Object object : set) {
			list.add(object.getClass().getSimpleName());
		}
		return list;
	}
	
	public static void main(String[] args) {
		List<String> list = getSimpleNameInPackage("cn.com.xbed.app.bean");
		for (String str : list) {
			System.out.println(str);
		}
	}
}
