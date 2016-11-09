package cn.com.xbed.app.bean.vo;

import java.util.ArrayList;
import java.util.List;

public class ChgRoomInputVo {
	private String account;
	private String password;

	private List<ChgRoomUnitVo> chgOperList = new ArrayList<>();

	public ChgRoomInputVo() {
		super();
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<ChgRoomUnitVo> getChgOperList() {
		return chgOperList;
	}

	public void setChgOperList(List<ChgRoomUnitVo> chgOperList) {
		this.chgOperList = chgOperList;
	}

	@Override
	public String toString() {
		return "ChgRoomInputVo [account=" + account + ", password=" + password + ", chgOperList=" + chgOperList + "]";
	}

}
