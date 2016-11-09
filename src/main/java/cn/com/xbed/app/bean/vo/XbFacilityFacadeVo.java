package cn.com.xbed.app.bean.vo;

import cn.com.xbed.app.bean.XbFacility;

public class XbFacilityFacadeVo implements java.io.Serializable {

	private XbFacility xbFacility;
	private boolean checked = false;

	public XbFacility getXbFacility() {
		return xbFacility;
	}

	public void setXbFacility(XbFacility xbFacility) {
		this.xbFacility = xbFacility;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
