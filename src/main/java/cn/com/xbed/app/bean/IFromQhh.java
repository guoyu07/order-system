package cn.com.xbed.app.bean;

import java.util.Date;

public class IFromQhh implements java.io.Serializable {

	private Integer jobId;
	private Integer syncStat;
	private Integer syncCnt;
	private Date syncDtm;
	private Date createDtm;
	private Date upDate;
	private String orderNo;
	private String orderInfo;
	private String orderResult;
	private Integer orderSn;
	private String remark;

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getSyncStat() {
		return syncStat;
	}

	public void setSyncStat(Integer syncStat) {
		this.syncStat = syncStat;
	}

	public Integer getSyncCnt() {
		return syncCnt;
	}

	public void setSyncCnt(Integer syncCnt) {
		this.syncCnt = syncCnt;
	}

	public Date getSyncDtm() {
		return syncDtm;
	}

	public void setSyncDtm(Date syncDtm) {
		this.syncDtm = syncDtm;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public Date getUpDate() {
		return upDate;
	}

	public void setUpDate(Date upDate) {
		this.upDate = upDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}

	public String getOrderResult() {
		return orderResult;
	}

	public void setOrderResult(String orderResult) {
		this.orderResult = orderResult;
	}

	public Integer getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(Integer orderSn) {
		this.orderSn = orderSn;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
