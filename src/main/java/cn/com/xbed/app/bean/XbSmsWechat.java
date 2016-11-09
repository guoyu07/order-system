package cn.com.xbed.app.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * XbSmsWechatBean
 * @version 1.0
 * @author 蔡俊杰 */
public class XbSmsWechat implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer recordId;
	private Integer tplId;
	private String telno;
	private String orderId;
	private String message;
	private Date createTime;
	private Date sendTime;
	private Date planTime;
	private Integer sendTimes;
	private Integer status;
	private String retCode;
	private String retMsg;
	private Integer flag;
	private String remark;

	
	
	public XbSmsWechat() {
		super();
		// TODO Auto-generated constructor stub
	}
	public XbSmsWechat(Integer tplId, String telno, String orderId, String message, Integer flag) {
		super();
		this.tplId = tplId;
		this.telno = telno;
		this.orderId = orderId;
		this.message = message;
		this.flag = flag;
	}
	public Integer getRecordId(){
		return recordId;
	}
	public void setRecordId(Integer recordId){
		this.recordId = recordId;
	}
	public Integer getTplId(){
		return tplId;
	}
	public void setTplId(Integer tplId){
		this.tplId = tplId;
	}
	public String getTelno(){
		return telno;
	}
	public void setTelno(String telno){
		this.telno = telno;
	}
	public String getOrderId(){
		return orderId;
	}
	public void setOrderId(String orderId){
		this.orderId = orderId;
	}
	public String getMessage(){
		return message;
	}
	public void setMessage(String message){
		this.message = message;
	}
	public Date getCreateTime(){
		return createTime;
	}
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	public Date getSendTime(){
		return sendTime;
	}
	public void setSendTime(Date sendTime){
		this.sendTime = sendTime;
	}
	public Date getPlanTime(){
		return planTime;
	}
	public void setPlanTime(Date planTime){
		this.planTime = planTime;
	}
	public Integer getSendTimes(){
		return sendTimes;
	}
	public void setSendTimes(Integer sendTimes){
		this.sendTimes = sendTimes;
	}
	public Integer getStatus(){
		return status;
	}
	public void setStatus(Integer status){
		this.status = status;
	}
	public String getRetCode(){
		return retCode;
	}
	public void setRetCode(String retCode){
		this.retCode = retCode;
	}
	public String getRetMsg(){
		return retMsg;
	}
	public void setRetMsg(String retMsg){
		this.retMsg = retMsg;
	}
	public Integer getFlag(){
		return flag;
	}
	public void setFlag(Integer flag){
		this.flag = flag;
	}
	public String getRemark(){
		return remark;
	}
	public void setRemark(String remark){
		this.remark = remark;
	}

}

