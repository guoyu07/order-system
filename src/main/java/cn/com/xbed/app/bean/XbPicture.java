package cn.com.xbed.app.bean;
// Generated 2015-8-17 17:50:21 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

/**
 * XbPicture generated by hbm2java
 */
public class XbPicture implements java.io.Serializable {

	private Integer picId;
	private Integer roomId;
	private Integer serial;
	private Integer cover;
	private String filePath;
	private String tag;
	private String descri;
	private Date createDtm;
	private Integer picType;

	public Integer getPicType() {
		return picType;
	}

	public void setPicType(Integer picType) {
		this.picType = picType;
	}

	public XbPicture() {
	}

	public XbPicture(Integer roomId, Integer serial, Integer cover, String filePath, String tag, String descri) {
		this.roomId = roomId;
		this.serial = serial;
		this.cover = cover;
		this.filePath = filePath;
		this.tag = tag;
		this.descri = descri;
	}

	public Integer getPicId() {
		return picId;
	}

	public void setPicId(Integer picId) {
		this.picId = picId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getSerial() {
		return serial;
	}

	public void setSerial(Integer serial) {
		this.serial = serial;
	}

	public Integer getCover() {
		return cover;
	}

	public void setCover(Integer cover) {
		this.cover = cover;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getDescri() {
		return descri;
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	@Override
	public String toString() {
		return "XbPicture [picId=" + picId + ", roomId=" + roomId + ", serial=" + serial + ", cover=" + cover + ", filePath=" + filePath + ", tag="
				+ tag + ", descri=" + descri + ", createDtm=" + createDtm + ", picType=" + picType + "]";
	}

}