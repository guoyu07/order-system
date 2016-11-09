package cn.com.xbed.app.bean;

public class XbRoomType implements java.io.Serializable {

	private Integer roomTypeId;
	private String name;
	private Integer area;
	private String roomType;
	private String wc;
	private String bathe;
	private String bed;
	private String sightView;
	private Integer lodgerCount;
	private String baseEquipment;
	private String service;
	private Integer roomCount;

	public Integer getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(Integer roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getArea() {
		return area;
	}

	public void setArea(Integer area) {
		this.area = area;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getWc() {
		return wc;
	}

	public void setWc(String wc) {
		this.wc = wc;
	}

	public String getBathe() {
		return bathe;
	}

	public void setBathe(String bathe) {
		this.bathe = bathe;
	}

	public String getBed() {
		return bed;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}

	public String getSightView() {
		return sightView;
	}

	public void setSightView(String sightView) {
		this.sightView = sightView;
	}

	public Integer getLodgerCount() {
		return lodgerCount;
	}

	public void setLodgerCount(Integer lodgerCount) {
		this.lodgerCount = lodgerCount;
	}

	public String getBaseEquipment() {
		return baseEquipment;
	}

	public void setBaseEquipment(String baseEquipment) {
		this.baseEquipment = baseEquipment;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public void setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
	}

}
