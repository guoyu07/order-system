package cn.com.xbed.app.bean;

import java.util.Date;

public class XbChain implements java.io.Serializable {

	private Integer chainId;
	private String name;
	private String address;
	private String tel;
	private String postCode;
	private Date openTime;
	private String descri;
	private String traffic;
	private String environment;
	private String sightSpot;
	private String houseRule;
	private String locate;
	private Integer predictCheckout;
	private Date lastModifyDtm;

	public Integer getPredictCheckout() {
		return predictCheckout;
	}

	public void setPredictCheckout(Integer predictCheckout) {
		this.predictCheckout = predictCheckout;
	}

	public Date getLastModifyDtm() {
		return lastModifyDtm;
	}

	public void setLastModifyDtm(Date lastModifyDtm) {
		this.lastModifyDtm = lastModifyDtm;
	}

	public Integer getChainId() {
		return this.chainId;
	}

	public void setChainId(Integer chainId) {
		this.chainId = chainId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return this.tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Date getOpenTime() {
		return this.openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public String getDescri() {
		return this.descri;
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

	public String getTraffic() {
		return this.traffic;
	}

	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}

	public String getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getSightSpot() {
		return this.sightSpot;
	}

	public void setSightSpot(String sightSpot) {
		this.sightSpot = sightSpot;
	}

	public String getHouseRule() {
		return this.houseRule;
	}

	public void setHouseRule(String houseRule) {
		this.houseRule = houseRule;
	}

	public String getLocate() {
		return this.locate;
	}

	public void setLocate(String locate) {
		this.locate = locate;
	}

	@Override
	public String toString() {
		return "XbChain [chainId=" + chainId + ", name=" + name + ", address=" + address + ", tel=" + tel
				+ ", postCode=" + postCode + ", openTime=" + openTime + ", descri=" + descri + ", traffic=" + traffic
				+ ", environment=" + environment + ", sightSpot=" + sightSpot + ", houseRule=" + houseRule + ", locate="
				+ locate + ", predictCheckout=" + predictCheckout + ", lastModifyDtm=" + lastModifyDtm + "]";
	}
	
	

}
