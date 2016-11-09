package cn.com.xbed.app.bean;

public class XbRoomCleanServ implements java.io.Serializable {

	private Integer cleanServId;
	private Integer roomId;
	private Integer cleanStepId;
	private Integer cleanPriceId;
	private Integer goodsSetId;

	public Integer getGoodsSetId() {
		return goodsSetId;
	}

	public void setGoodsSetId(Integer goodsSetId) {
		this.goodsSetId = goodsSetId;
	}

	public Integer getCleanServId() {
		return cleanServId;
	}

	public void setCleanServId(Integer cleanServId) {
		this.cleanServId = cleanServId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getCleanStepId() {
		return cleanStepId;
	}

	public void setCleanStepId(Integer cleanStepId) {
		this.cleanStepId = cleanStepId;
	}

	public Integer getCleanPriceId() {
		return cleanPriceId;
	}

	public void setCleanPriceId(Integer cleanPriceId) {
		this.cleanPriceId = cleanPriceId;
	}

}
