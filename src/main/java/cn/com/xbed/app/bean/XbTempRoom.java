package cn.com.xbed.app.bean;

import java.util.Date;

public class XbTempRoom implements java.io.Serializable {

	private Integer id;
	private String city;
	private String district;
	private String name;
	private Date operationDate;
	private String ownerMobile;
	private String ownerName;
	private Integer parentId;
	private String province;
	private Integer roomId;
	private String roomType;
	private String status;
	private String stressAddress;
	private String imgUrls;
	private Integer xbRoomId;
	private Date upDate;
	private Date downDate;
	private Date createAt;
	private Date updateAt;
	private Integer updateRoom;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public String getOwnerMobile() {
		return ownerMobile;
	}

	public void setOwnerMobile(String ownerMobile) {
		this.ownerMobile = ownerMobile;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStressAddress() {
		return stressAddress;
	}

	public void setStressAddress(String stressAddress) {
		this.stressAddress = stressAddress;
	}

	public String getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(String imgUrls) {
		this.imgUrls = imgUrls;
	}

	public Integer getXbRoomId() {
		return xbRoomId;
	}

	public void setXbRoomId(Integer xbRoomId) {
		this.xbRoomId = xbRoomId;
	}

	public Date getUpDate() {
		return upDate;
	}

	public void setUpDate(Date upDate) {
		this.upDate = upDate;
	}

	public Date getDownDate() {
		return downDate;
	}

	public void setDownDate(Date downDate) {
		this.downDate = downDate;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	public Integer getUpdateRoom() {
		return updateRoom;
	}

	public void setUpdateRoom(Integer updateRoom) {
		this.updateRoom = updateRoom;
	}

}
