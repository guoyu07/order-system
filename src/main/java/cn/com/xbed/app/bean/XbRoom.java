package cn.com.xbed.app.bean;

import java.util.Date;

public class XbRoom implements java.io.Serializable {

	private Integer roomId;
	private Integer chainId;
	private Integer buildingId;
	private Integer roomTypeId;
	private String roomTypeName;
	private String roomName;
	private String area;
	private String houseType;
	private Integer stat;
	private String locate;
	private String province;
	private String city;
	private String district;
	private Byte roomFloor;
	private String addr;
	private String title;
	private String descri;
	private Integer picId;
	private Integer picCount;
	private Integer price;
	private String currency;
	private String tag;
	private String checkin;
	private String checkout;
	private String checkoutPlot;
	private String lodgerCount;
	private String bedCount;
	private Integer flag;
	private String chainName;
	private String chainAddr;
	private Date createDtm;
	private Date busiDate;
	private Integer ownerRoomId;
	private String roomDetail;

	public String getRoomDetail() {
		return roomDetail;
	}

	public void setRoomDetail(String roomDetail) {
		this.roomDetail = roomDetail;
	}

	public Integer getOwnerRoomId() {
		return ownerRoomId;
	}

	public void setOwnerRoomId(Integer ownerRoomId) {
		this.ownerRoomId = ownerRoomId;
	}

	public Date getBusiDate() {
		return busiDate;
	}

	public void setBusiDate(Date busiDate) {
		this.busiDate = busiDate;
	}

	public String getChainName() {
		return chainName;
	}

	public void setChainName(String chainName) {
		this.chainName = chainName;
	}

	public String getChainAddr() {
		return chainAddr;
	}

	public void setChainAddr(String chainAddr) {
		this.chainAddr = chainAddr;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public String getLodgerCount() {
		return lodgerCount;
	}

	public void setLodgerCount(String lodgerCount) {
		this.lodgerCount = lodgerCount;
	}

	public String getBedCount() {
		return bedCount;
	}

	public void setBedCount(String bedCount) {
		this.bedCount = bedCount;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getChainId() {
		return chainId;
	}

	public void setChainId(Integer chainId) {
		this.chainId = chainId;
	}

	public Integer getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(Integer buildingId) {
		this.buildingId = buildingId;
	}

	public Integer getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(Integer roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	public Integer getStat() {
		return stat;
	}

	public void setStat(Integer stat) {
		this.stat = stat;
	}

	public String getLocate() {
		return locate;
	}

	public void setLocate(String locate) {
		this.locate = locate;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
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

	public Byte getRoomFloor() {
		return roomFloor;
	}

	public void setRoomFloor(Byte roomFloor) {
		this.roomFloor = roomFloor;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescri() {
		return descri;
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

	public Integer getPicId() {
		return picId;
	}

	public void setPicId(Integer picId) {
		this.picId = picId;
	}

	public Integer getPicCount() {
		return picCount;
	}

	public void setPicCount(Integer picCount) {
		this.picCount = picCount;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getCheckin() {
		return checkin;
	}

	public void setCheckin(String checkin) {
		this.checkin = checkin;
	}

	public String getCheckout() {
		return checkout;
	}

	public void setCheckout(String checkout) {
		this.checkout = checkout;
	}

	public String getCheckoutPlot() {
		return checkoutPlot;
	}

	public void setCheckoutPlot(String checkoutPlot) {
		this.checkoutPlot = checkoutPlot;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "XbRoom [roomId=" + roomId + ", chainId=" + chainId + ", roomName=" + roomName + ", locate=" + locate + ", province=" + province
				+ ", city=" + city + ", district=" + district + ", roomFloor=" + roomFloor + ", addr=" + addr + ", title=" + title + ", price="
				+ price + ", lodgerCount=" + lodgerCount + ", bedCount=" + bedCount + ", flag=" + flag + ", chainName=" + chainName + ", createDtm="
				+ createDtm + ", busiDate=" + busiDate + "]";
	}

}
