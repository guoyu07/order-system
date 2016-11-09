package cn.com.xbed.app.bean;

public class XbRoomFacilityRel implements java.io.Serializable {

	private Integer relId;
	private Integer roomId;
	private Integer facilityId;

	public XbRoomFacilityRel() {
	}

	public XbRoomFacilityRel(Integer roomId, Integer facilityId) {
		this.roomId = roomId;
		this.facilityId = facilityId;
	}

	public Integer getRelId() {
		return relId;
	}

	public void setRelId(Integer relId) {
		this.relId = relId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Integer facilityId) {
		this.facilityId = facilityId;
	}

}
