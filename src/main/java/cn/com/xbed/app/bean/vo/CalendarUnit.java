package cn.com.xbed.app.bean.vo;

public class CalendarUnit {
	private int lockStat;
	private int price;
	private String date;// 存放2015-10-13格式

	public CalendarUnit() {
		super();
	}

	public CalendarUnit(int lockStat, int price, String date) {
		super();
		this.lockStat = lockStat;
		this.price = price;
		this.date = date;
	}

	public int getLockStat() {
		return lockStat;
	}

	public void setLockStat(int lockStat) {
		this.lockStat = lockStat;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "CalendarUnit [lockStat=" + lockStat + ", price=" + price + ", date=" + date + "]";
	}

}
