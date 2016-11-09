package cn.com.xbed.app.bean;

public class XbFacility implements java.io.Serializable {

	private Integer facilityId;
	private String name;
	private Integer facilityType;
	private String remark;
	private Integer flag;

	public Integer getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Integer facilityId) {
		this.facilityId = facilityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(Integer facilityType) {
		this.facilityType = facilityType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "XbFacility [facilityId=" + facilityId + ", name=" + name + ", facilityType=" + facilityType
				+ ", remark=" + remark + ", flag=" + flag + "]";
	}

}
