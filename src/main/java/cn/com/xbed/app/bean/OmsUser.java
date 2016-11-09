package cn.com.xbed.app.bean;

import java.util.Date;

public class OmsUser implements java.io.Serializable {

	private Integer userId;
	private String account;
	private String password;
	private String password2;
	private Integer roleId;
	private String salt;
	private Date createTime;
	private Byte userSts;
	private String remark;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Byte getUserSts() {
		return userSts;
	}

	public void setUserSts(Byte userSts) {
		this.userSts = userSts;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "OmsUser [userId=" + userId + ", account=" + account + ", password=" + password + ", password2=" + password2 + ", roleId=" + roleId
				+ ", salt=" + salt + ", createTime=" + createTime + ", userSts=" + userSts + ", remark=" + remark + "]";
	}

}
