package cn.com.xbed.app.service.ordermodule.checkin;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbCheckiner;
import cn.com.xbed.app.service.invoicemodule.InvoiceModuleBase;
import cn.com.xbed.app.service.localordermodule.LocalOrderModuleBase;
import cn.com.xbed.app.service.maintainmodule.MaintainModuleBase;
import cn.com.xbed.app.service.ordermodule.intf.AbstractCheckinTemplate;
import cn.com.xbed.app.service.otamodule.IOtaModuleBase;
import cn.com.xbed.app.service.roomstatemodule.RoomStateModuleBase;

@Service
@Transactional
public class CheckinOutImpl extends AbstractCheckinTemplate {
	@Resource
	private LocalOrderModuleBase localOrderModuleBase;
	@Resource
	private InvoiceModuleBase invoiceModuleBase;

	@Resource(name = "qhhOtaModuleBase")
	private IOtaModuleBase iOtaModuleBase;

	@Resource
	private RoomStateModuleBase roomStateModuleBase;
	@Resource
	private MaintainModuleBase maintainModuleBase;

	@Override
	public int localCheckin(int checkinId, List<XbCheckiner> checkinerList) {
		return localOrderModuleBase.localCheckin(checkinId, checkinerList);
	}

	@Override
	public JSONObject roomStateCheckin(int checkinId) {
		return roomStateModuleBase.roomStateCheckin(checkinId);
	}

//	@Override
//	public String otaCheckin(int checkinId) {
//		return iOtaModuleBase.otaCheckin(checkinId);
//	}

	@Override
	public void maintainOpenPwd(int checkinId, String openPwd) {
		maintainModuleBase.maintainOpenPwd(checkinId, openPwd);
	}
}
