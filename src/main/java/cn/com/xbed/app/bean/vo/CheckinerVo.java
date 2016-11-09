package cn.com.xbed.app.bean.vo;

import java.util.List;

public class CheckinerVo {
	private CheckinerUnit main;
	private List<CheckinerUnit> other;

	public CheckinerVo() {
		super();
	}

	public CheckinerVo(CheckinerUnit main, List<CheckinerUnit> other) {
		super();
		this.main = main;
		this.other = other;
	}

	public CheckinerUnit getMain() {
		return main;
	}

	public void setMain(CheckinerUnit main) {
		this.main = main;
	}

	public List<CheckinerUnit> getOther() {
		return other;
	}

	public void setOther(List<CheckinerUnit> other) {
		this.other = other;
	}

	@Override
	public String toString() {
		return "CheckinerVo [main=" + main + ", other=" + other + "]";
	}

}
