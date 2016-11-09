package cn.com.xbed.app.bean;

import java.util.Date;

public class IDirtySync implements java.io.Serializable {

	private Integer syncId;
	private String datagram;
	private Byte syncCnt;
	private Byte syncResult;
	private Date createDtm;
	private Date syncDtm;
	private String batchId;

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public Integer getSyncId() {
		return syncId;
	}

	public void setSyncId(Integer syncId) {
		this.syncId = syncId;
	}

	public String getDatagram() {
		return datagram;
	}

	public void setDatagram(String datagram) {
		this.datagram = datagram;
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

}
