package cn.com.xbed.app.commons.pager;

public class PageHelper {
	
	// 获取分页开始的下标
	public static int getStartIndex(int thisPage, int pageSize) {
		int index = (thisPage - 1) * pageSize;
		return index;
	}
	
	// 获得总页数
	public static int getTotalPage(int totalSize, int pageSize) {
		double pageCount = Math.ceil((double)totalSize/pageSize);
		return (int) pageCount;
	}
}
