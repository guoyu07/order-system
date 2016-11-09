package cn.com.xbed.app.bean;

import java.util.Date;

public class RpBaseData implements java.io.Serializable, Cloneable {

	private Integer baseId;
	private Integer roomId;
	private String curDate;
	private String financialDate;
	private Date createDtm;
	private Date lastModDtm;
	private String roomName;
	private Integer chainId;
	private Integer orderId;
	private Integer checkinId;
	private String orderNo;
	private String otaOrderNo;
	private String otaStayId;
	private Date orderCreateDtm;
	private Date orderPayDtm;
	private Date checkinTime;
	private Date checkoutTime;
	private Integer orderType;
	private Integer totalPrice;
	private Integer chkinPrice;
	private Integer todayPrice;
	private Integer nightCount;

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		return "RpBaseData [baseId=" + baseId + ", roomId=" + roomId + ", curDate=" + curDate + ", financialDate="
				+ financialDate + ", createDtm=" + createDtm + ", lastModDtm=" + lastModDtm + ", roomName=" + roomName
				+ ", chainId=" + chainId + ", orderId=" + orderId + ", checkinId=" + checkinId + ", orderNo=" + orderNo
				+ ", otaOrderNo=" + otaOrderNo + ", otaStayId=" + otaStayId + ", orderCreateDtm=" + orderCreateDtm
				+ ", orderPayDtm=" + orderPayDtm + ", checkinTime=" + checkinTime + ", checkoutTime=" + checkoutTime
				+ ", orderType=" + orderType + ", totalPrice=" + totalPrice + ", chkinPrice=" + chkinPrice
				+ ", todayPrice=" + todayPrice + ", nightCount=" + nightCount + "]";
	}

	public Integer getNightCount() {
		return nightCount;
	}

	public void setNightCount(Integer nightCount) {
		this.nightCount = nightCount;
	}

	public String getFinancialDate() {
		return financialDate;
	}

	public void setFinancialDate(String financialDate) {
		this.financialDate = financialDate;
	}

	public Integer getBaseId() {
		return baseId;
	}

	public void setBaseId(Integer baseId) {
		this.baseId = baseId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public String getCurDate() {
		return curDate;
	}

	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public Date getLastModDtm() {
		return lastModDtm;
	}

	public void setLastModDtm(Date lastModDtm) {
		this.lastModDtm = lastModDtm;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Integer getChainId() {
		return chainId;
	}

	public void setChainId(Integer chainId) {
		this.chainId = chainId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(Integer checkinId) {
		this.checkinId = checkinId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOtaOrderNo() {
		return otaOrderNo;
	}

	public void setOtaOrderNo(String otaOrderNo) {
		this.otaOrderNo = otaOrderNo;
	}

	public String getOtaStayId() {
		return otaStayId;
	}

	public void setOtaStayId(String otaStayId) {
		this.otaStayId = otaStayId;
	}

	public Date getOrderCreateDtm() {
		return orderCreateDtm;
	}

	public void setOrderCreateDtm(Date orderCreateDtm) {
		this.orderCreateDtm = orderCreateDtm;
	}

	public Date getOrderPayDtm() {
		return orderPayDtm;
	}

	public void setOrderPayDtm(Date orderPayDtm) {
		this.orderPayDtm = orderPayDtm;
	}

	public Date getCheckinTime() {
		return checkinTime;
	}

	public void setCheckinTime(Date checkinTime) {
		this.checkinTime = checkinTime;
	}

	public Date getCheckoutTime() {
		return checkoutTime;
	}

	public void setCheckoutTime(Date checkoutTime) {
		this.checkoutTime = checkoutTime;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Integer getChkinPrice() {
		return chkinPrice;
	}

	public void setChkinPrice(Integer chkinPrice) {
		this.chkinPrice = chkinPrice;
	}

	public Integer getTodayPrice() {
		return todayPrice;
	}

	public void setTodayPrice(Integer todayPrice) {
		this.todayPrice = todayPrice;
	}

}
