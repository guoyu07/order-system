package cn.com.xbed.app.service.localordermodule.vo;

import cn.com.xbed.app.bean.XbCheckin;
import cn.com.xbed.app.bean.XbCheckinExt;
import cn.com.xbed.app.bean.XbRoom;

public class NewCheckinOut {
	private XbCheckin checkinInfo;
	private XbCheckinExt checkinExt;
	private XbRoom roomInfo;

	public XbCheckin getCheckinInfo() {
		return checkinInfo;
	}

	public XbCheckinExt getCheckinExt() {
		return checkinExt;
	}

	public XbRoom getRoomInfo() {
		return roomInfo;
	}

	public NewCheckinOut(XbCheckin checkinInfo, XbCheckinExt checkinExt, XbRoom roomInfo) {
		super();
		this.checkinInfo = checkinInfo;
		this.checkinExt = checkinExt;
		this.roomInfo = roomInfo;
	}

}