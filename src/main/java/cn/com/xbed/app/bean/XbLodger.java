package cn.com.xbed.app.bean;

import java.util.Date;

public class XbLodger implements java.io.Serializable {

	private Integer lodgerId;
	private String lodgerName;
	private String mobile;
	private String password;
	private String password2;
	private String salt;
	private String email;
	private Date createTime;
	private Integer mailingId;
	private String idCard;
	private String cardFront;
	private String cardBack;
	private Integer idcardValid;
	private Integer mobileValid;
	private Integer source;
	private String subSource;
	private Integer stat;
	private String remark;
	private Integer isFollow;
	private String openId;
	private Integer hasChgPwd;
	private Integer lodgerType;


	public Integer getLodgerType() {
		return lodgerType;
	}

	public void setLodgerType(Integer lodgerType) {
		this.lodgerType = lodgerType;
	}

	public XbLodger(Integer lodgerId, String idCard) {
		super();
		this.lodgerId = lodgerId;
		this.idCard = idCard;
	}

	public XbLodger() {
		super();
	}

	public Integer getHasChgPwd() {
		return hasChgPwd;
	}

	public void setHasChgPwd(Integer hasChgPwd) {
		this.hasChgPwd = hasChgPwd;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Integer getIsFollow() {
		return isFollow;
	}

	public void setIsFollow(Integer isFollow) {
		this.isFollow = isFollow;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getStat() {
		return stat;
	}

	public void setStat(Integer stat) {
		this.stat = stat;
	}

	public Integer getLodgerId() {
		return lodgerId;
	}

	public void setLodgerId(Integer lodgerId) {
		this.lodgerId = lodgerId;
	}

	public String getLodgerName() {
		return lodgerName;
	}

	public void setLodgerName(String lodgerName) {
		this.lodgerName = lodgerName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getMailingId() {
		return mailingId;
	}

	public void setMailingId(Integer mailingId) {
		this.mailingId = mailingId;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getCardFront() {
		return cardFront;
	}

	public void setCardFront(String cardFront) {
		this.cardFront = cardFront;
	}

	public String getCardBack() {
		return cardBack;
	}

	public void setCardBack(String cardBack) {
		this.cardBack = cardBack;
	}

	public Integer getIdcardValid() {
		return idcardValid;
	}

	public void setIdcardValid(Integer idcardValid) {
		this.idcardValid = idcardValid;
	}

	public Integer getMobileValid() {
		return mobileValid;
	}

	public void setMobileValid(Integer mobileValid) {
		this.mobileValid = mobileValid;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getSubSource() {
		return subSource;
	}

	public void setSubSource(String subSource) {
		this.subSource = subSource;
	}

	@Override
	public String toString() {
		return "XbLodger [lodgerId=" + lodgerId + ", lodgerName=" + lodgerName + ", mobile=" + mobile + ", password=" + password + ", password2="
				+ password2 + ", stat=" + stat + ", remark=" + remark + ", isFollow=" + isFollow + "]";
	}

}
