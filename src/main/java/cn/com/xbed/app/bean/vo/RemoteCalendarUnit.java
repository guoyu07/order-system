package cn.com.xbed.app.bean.vo;

public class RemoteCalendarUnit {
	private int state;
	private int price;
	private String calendarDate;// 存放2015-10-13格式
	private int roomId;

	public RemoteCalendarUnit() {
		super();
	}

	public RemoteCalendarUnit(int state, int price, String calendarDate, int roomId) {
		super();
		this.state = state;
		this.price = price;
		this.calendarDate = calendarDate;
		this.roomId = roomId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getCalendarDate() {
		return calendarDate;
	}

	public void setCalendarDate(String calendarDate) {
		this.calendarDate = calendarDate;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	@Override
	public String toString() {
		return "RemoteCalendarUnit [state=" + state + ", price=" + price + ", calendarDate=" + calendarDate + ", roomId=" + roomId + "]";
	}

}
