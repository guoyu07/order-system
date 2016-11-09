package cn.com.xbed.app.bean;

public class XbRoomState implements java.io.Serializable {

	private Integer roomStateId;
	private Integer roomId;
	private Integer stat;

	public Integer getRoomStateId() {
		return roomStateId;
	}

	public void setRoomStateId(Integer roomStateId) {
		this.roomStateId = roomStateId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getStat() {
		return stat;
	}

	public void setStat(Integer stat) {
		this.stat = stat;
	}

}
