package cn.com.xbed.app.bean.vo;

public class CalEditVo {
	private String date;// 2015-08-21
	private Integer newPrice;
	private Integer statOper;// 1解锁(可订) 3锁定(禁售)
	private Integer roomId;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(Integer newPrice) {
		this.newPrice = newPrice;
	}

	public Integer getStatOper() {
		return statOper;
	}

	public void setStatOper(Integer statOper) {
		this.statOper = statOper;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

}
