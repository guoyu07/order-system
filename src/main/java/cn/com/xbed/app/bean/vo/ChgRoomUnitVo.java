package cn.com.xbed.app.bean.vo;

public class ChgRoomUnitVo {
	private Integer newRoomId;
	private Integer oldCheckinId;

	public Integer getNewRoomId() {
		return newRoomId;
	}

	public void setNewRoomId(Integer newRoomId) {
		this.newRoomId = newRoomId;
	}

	public Integer getOldCheckinId() {
		return oldCheckinId;
	}

	public void setOldCheckinId(Integer oldCheckinId) {
		this.oldCheckinId = oldCheckinId;
	}

	@Override
	public String toString() {
		return "ChgRoomUnitVo [newRoomId=" + newRoomId + ", oldCheckinId=" + oldCheckinId + "]";
	}

}
