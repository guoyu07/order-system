package cn.com.xbed.app.bean.vo;

import java.util.List;

import cn.com.xbed.app.bean.XbAddition;
import cn.com.xbed.app.bean.XbRoom;

public class NewRoomInfoVo {
	private XbRoom roomInfo;
	private XbAddition additionInfo;
	private List<FaciPair> faciInfo;

	public XbRoom getRoomInfo() {
		return roomInfo;
	}

	public void setRoomInfo(XbRoom roomInfo) {
		this.roomInfo = roomInfo;
	}

	public List<FaciPair> getFaciInfo() {
		return faciInfo;
	}

	public void setFaciInfo(List<FaciPair> faciInfo) {
		this.faciInfo = faciInfo;
	}

	public XbAddition getAdditionInfo() {
		return additionInfo;
	}

	public void setAdditionInfo(XbAddition additionInfo) {
		this.additionInfo = additionInfo;
	}

	public NewRoomInfoVo() {
		super();
	}

	public NewRoomInfoVo(XbRoom roomInfo, List<FaciPair> faciInfo, XbAddition additionInfo) {
		super();
		this.roomInfo = roomInfo;
		this.faciInfo = faciInfo;
		this.additionInfo = additionInfo;
	}

	public static class FaciPair {
		private int operType = 1;
		private int facilityId = 1;

		public FaciPair() {
			super();
		}

		public FaciPair(int operType, int facilityId) {
			super();
			this.operType = operType;
			this.facilityId = facilityId;
		}

		public int getOperType() {
			return operType;
		}

		public void setOperType(int operType) {
			this.operType = operType;
		}

		public int getFacilityId() {
			return facilityId;
		}

		public void setFacilityId(int facilityId) {
			this.facilityId = facilityId;
		}
	}

}
