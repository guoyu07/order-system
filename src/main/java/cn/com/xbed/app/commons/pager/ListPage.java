package cn.com.xbed.app.commons.pager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListPage {
	
	//Map的key为数据表的字段名
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	
	private Pager pager;
	
	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}
}
