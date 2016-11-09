package cn.com.xbed.app.bean;

import java.util.Date;

public class IQhhChangeRoom implements java.io.Serializable {

	private Integer syncId;
	private String dataQuery;
	private String dataResult;
	private Byte syncCnt;
	private Byte syncResult;
	private Date createDtm;
	private Date syncDtm;
	private String remark;
	private Integer checkinId;
	private Integer hotelId;

	public Integer getHotelId() {
		return hotelId;
	}

	public void setHotelId(Integer hotelId) {
		this.hotelId = hotelId;
	}

	public Integer getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(Integer checkinId) {
		this.checkinId = checkinId;
	}

	public Integer getSyncId() {
		return syncId;
	}

	public void setSyncId(Integer syncId) {
		this.syncId = syncId;
	}

	public String getDataQuery() {
		return dataQuery;
	}

	public void setDataQuery(String dataQuery) {
		this.dataQuery = dataQuery;
	}

	public String getDataResult() {
		return dataResult;
	}

	public void setDataResult(String dataResult) {
		this.dataResult = dataResult;
	}

	public Byte getSyncCnt() {
		return syncCnt;
	}

	public void setSyncCnt(Byte syncCnt) {
		this.syncCnt = syncCnt;
	}

	public Byte getSyncResult() {
		return syncResult;
	}

	public void setSyncResult(Byte syncResult) {
		this.syncResult = syncResult;
	}

	public Date getCreateDtm() {
		return createDtm;
	}

	public void setCreateDtm(Date createDtm) {
		this.createDtm = createDtm;
	}

	public Date getSyncDtm() {
		return syncDtm;
	}

	public void setSyncDtm(Date syncDtm) {
		this.syncDtm = syncDtm;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
