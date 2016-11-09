package cn.com.xbed.app.commons.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConsolePrinter {
	private static boolean isPrintToConsole;

	static {
		String flag = PropertiesReader.consvars.get("switch.print_to_console");
		if (flag != null && flag.equalsIgnoreCase("true")) {
			isPrintToConsole = true;
		} else {
			isPrintToConsole = false;
		}
	}

	public static void println(Object obj) {
		if (isPrintToConsole) {
			System.out.println(obj);
		}
	}

	public static void print(Object obj) {
		if (isPrintToConsole) {
			System.out.print(obj);
		}
	}

	public static void println() {
		if (isPrintToConsole) {
			System.out.println();
		}
	}

	public static void printMap(Map map) {
		if (isPrintToConsole) {
			Set set = map.keySet();
			System.out.println("map.size()---->" + set.size());
			Iterator it = map.keySet().iterator();
			for (; it.hasNext();) {
				Object key = it.next();
				Object value = map.get(key);
				System.out.println(key + "<--->" + value);
			}
		}
	}

}
