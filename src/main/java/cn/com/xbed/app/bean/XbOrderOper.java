package cn.com.xbed.app.bean;

import java.util.Date;

public class XbOrderOper implements java.io.Serializable {
	private Integer operId;
	private Integer orderId;
	private Integer userId;
	private Integer operType;
	private Date createDtm;
	private Date refundDtm;
	private Integer isRefund;
	private Integer refund;
	private String remark;

	public Date getRefundDtm() {
		return refundDtm;
	}

	public void setRefundDtm(Date refundDtm) {
		this.refundDtm = refundDtm;
	}

	public Integer getOperId() {
		return operId;
	}

	public void setOperId(Integer operId) {
		this.operId = operId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getOperType() {
		return operType;
	}

	public void setOperType(Integer operType) {
		this.operType = operType;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public Integer getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(Integer isRefund) {
		this.isRefund = isRefund;
	}

	public Integer getRefund() {
		return refund;
	}

	public void setRefund(Integer refund) {
		this.refund = refund;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
