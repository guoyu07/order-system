package cn.com.xbed.app.bean;

import java.util.Date;

public class XbRoomCtrlSheet implements java.io.Serializable {

	private Integer ctrlId;
	private Integer ctrlType;
	private Date beginDtm;
	private Date endDtm;
	private Integer orderId;
	private Integer checkinId;
	private String orderNo;
	private Integer roomId;
	private String roomName;
	private Integer chainId;
	private String chainName;
	private Date createDtm;
	private Date lastModDtm;
	private String memo;
	private Integer userId;
	private String account;
	private String openMan;
	private String openManMobile;
	private Integer handleSts;

	public Integer getHandleSts() {
		return handleSts;
	}

	public void setHandleSts(Integer handleSts) {
		this.handleSts = handleSts;
	}

	public Integer getCtrlId() {
		return ctrlId;
	}

	public void setCtrlId(Integer ctrlId) {
		this.ctrlId = ctrlId;
	}

	public Integer getCtrlType() {
		return ctrlType;
	}

	public void setCtrlType(Integer ctrlType) {
		this.ctrlType = ctrlType;
	}

	public Date getBeginDtm() {
		return beginDtm;
	}

	public void setBeginDtm(Date beginDtm) {
		this.beginDtm = beginDtm;
	}

	public Date getEndDtm() {
		return endDtm;
	}

	public void setEndDtm(Date endDtm) {
		this.endDtm = endDtm;
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

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
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

	public String getChainName() {
		return chainName;
	}

	public void setChainName(String chainName) {
		this.chainName = chainName;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getOpenMan() {
		return openMan;
	}

	public void setOpenMan(String openMan) {
		this.openMan = openMan;
	}

	public String getOpenManMobile() {
		return openManMobile;
	}

	public void setOpenManMobile(String openManMobile) {
		this.openManMobile = openManMobile;
	}
}
