package cn.com.xbed.app.service.ordermodule.intf;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.com.xbed.app.bean.XbCheckiner;

@Service
@Transactional
public abstract class AbstractCheckinTemplate {

	//
	public String checkin(int checkinId, List<XbCheckiner> checkinerList) {
		// 1.本地进行checkin
		localCheckin(checkinId, checkinerList);

		String openPwd = null;
		// 3.修改房态房间

		try {
			JSONObject json = roomStateCheckin(checkinId);
			
			if(json!= null && json.toJSONString().indexOf("openDoorPwd") != -1){
				openPwd = json.getJSONObject("data").getJSONObject("synchroResult").getString("openDoorPwd");
			}
		} catch (Exception e) {
			throw new RuntimeException("房态系统报错,入住:", e);
		}

		// 4.维护开门密码
		maintainOpenPwd(checkinId, openPwd);
		
		// 5.发送短信

		// 6.扩展方法(钩子)
		hook(checkinId);
		
		return openPwd;
	}

	public abstract int localCheckin(int checkinId, List<XbCheckiner> checkinerList);

	public abstract JSONObject roomStateCheckin(int checkinId);

	//public abstract String otaCheckin(int checkinId);

	public abstract void maintainOpenPwd(int checkinId, String openPwd);

	public Object hook(int checkinId) {
		return null;
	}
}
