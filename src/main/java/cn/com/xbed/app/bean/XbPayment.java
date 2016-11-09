package cn.com.xbed.app.bean;

import java.util.Date;

public class XbPayment implements java.io.Serializable {

	private Integer paymentId;
	private String orderId;
	private Integer accId;
	private String source;
	private String subSource;
	private String chainId;
	private Integer lodgerId;
	private String channelId;
	private String platformId;
	private String platformSn;
	private String payType;
	private Integer payMoney;
	private Date payTime;
	private Integer sendStatus;
	private Date sendTime;
	private Integer sendTimes;
	private Integer recvStatus;
	private String recvCode;
	private String recvDesc;
	private Date recvTime;
	private Integer notifyStatus;
	private Date notifyTime;
	private String additionData;

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getAccId() {
		return accId;
	}

	public void setAccId(Integer accId) {
		this.accId = accId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSubSource() {
		return subSource;
	}

	public void setSubSource(String subSource) {
		this.subSource = subSource;
	}

	public String getChainId() {
		return chainId;
	}

	public void setChainId(String chainId) {
		this.chainId = chainId;
	}

	public Integer getLodgerId() {
		return lodgerId;
	}

	public void setLodgerId(Integer lodgerId) {
		this.lodgerId = lodgerId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getPlatformSn() {
		return platformSn;
	}

	public void setPlatformSn(String platformSn) {
		this.platformSn = platformSn;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Integer getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Integer payMoney) {
		this.payMoney = payMoney;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Integer getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Integer getSendTimes() {
		return sendTimes;
	}

	public void setSendTimes(Integer sendTimes) {
		this.sendTimes = sendTimes;
	}

	public Integer getRecvStatus() {
		return recvStatus;
	}

	public void setRecvStatus(Integer recvStatus) {
		this.recvStatus = recvStatus;
	}

	public String getRecvCode() {
		return recvCode;
	}

	public void setRecvCode(String recvCode) {
		this.recvCode = recvCode;
	}

	public String getRecvDesc() {
		return recvDesc;
	}

	public void setRecvDesc(String recvDesc) {
		this.recvDesc = recvDesc;
	}

	public Date getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(Date recvTime) {
		this.recvTime = recvTime;
	}

	public Integer getNotifyStatus() {
		return notifyStatus;
	}

	public void setNotifyStatus(Integer notifyStatus) {
		this.notifyStatus = notifyStatus;
	}

	public Date getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(Date notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getAdditionData() {
		return additionData;
	}

	public void setAdditionData(String additionData) {
		this.additionData = additionData;
	}

}
