package cn.com.xbed.app.service.otamodule.vo;

import java.util.Date;

public class OtaNewOrderInput {
	private int chainId;
	private int roomId;
	private String orderNo;
	private Date checkinBeginTime;
	private Date checkinEndTime;
	private int totalPriceCentUnit;
	private String custName;
	private String custMobile;
	private String contName;
	private String contMobile;

	public OtaNewOrderInput(int chainId, int roomId, String orderNo, Date checkinBeginTime, Date checkinEndTime, int totalPriceCentUnit,
			String custName, String custMobile, String contName, String contMobile) {
		super();
		this.chainId = chainId;
		this.roomId = roomId;
		this.orderNo = orderNo;
		this.checkinBeginTime = checkinBeginTime;
		this.checkinEndTime = checkinEndTime;
		this.totalPriceCentUnit = totalPriceCentUnit;
		this.custName = custName;
		this.custMobile = custMobile;
		this.contName = contName;
		this.contMobile = contMobile;
	}

	public int getChainId() {
		return chainId;
	}

	public int getRoomId() {
		return roomId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public Date getCheckinBeginTime() {
		return checkinBeginTime;
	}

	public Date getCheckinEndTime() {
		return checkinEndTime;
	}

	public int getTotalPriceCentUnit() {
		return totalPriceCentUnit;
	}

	public String getCustName() {
		return custName;
	}

	public String getCustMobile() {
		return custMobile;
	}

	public String getContName() {
		return contName;
	}

	public String getContMobile() {
		return contMobile;
	}
}
