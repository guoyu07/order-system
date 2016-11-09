package cn.com.xbed.app.commons.pager;

public class Pager {

	private int thisPage = 1; // 当前页

	private int pageSize = 50; // 每页条数

	private int pageCount; // 多少页

	private int total; // 所有条数

	public Pager() {
		super();
	}

	public Pager(int pageSize) {
		super();
		this.pageSize = pageSize;
	}

	public int getThisPage() {
		return thisPage;
	}

	public void setThisPage(int thisPage) {
		this.thisPage = thisPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
