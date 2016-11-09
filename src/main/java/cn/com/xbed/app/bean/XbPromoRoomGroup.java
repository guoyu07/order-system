package cn.com.xbed.app.bean;

import java.util.Date;

public class XbPromoRoomGroup implements java.io.Serializable {

	private Integer roomGroupId;
	private String roomIds;
	private String title;
	private String descr;
	private String remark;

	public Integer getRoomGroupId() {
		return roomGroupId;
	}

	public void setRoomGroupId(Integer roomGroupId) {
		this.roomGroupId = roomGroupId;
	}

	public String getRoomIds() {
		return roomIds;
	}

	public void setRoomIds(String roomIds) {
		this.roomIds = roomIds;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
