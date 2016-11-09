package cn.com.xbed.app.bean.vo;

import java.util.Date;

public class WarningEntity {
		private String configItemCode;//配置项，日志来源 可以写ORD_SYS.QHH_SYNC
		private Integer logType;//1表示信息2表示警告3表示异常4表示错误5表示崩溃
		private Integer logLevel;//日志级别，分为1,2,3,4,5
		private String logRemark;//日志备注
		private String logContent;//日志内容
		private Date createTime;
		private String logCode;//日志代码
		private String lineNum;//对应代码行号

		public String getConfigItemCode() {
			return configItemCode;
		}

		public void setConfigItemCode(String configItemCode) {
			this.configItemCode = configItemCode;
		}

		public Integer getLogType() {
			return logType;
		}

		public void setLogType(Integer logType) {
			this.logType = logType;
		}

		public Integer getLogLevel() {
			return logLevel;
		}

		public void setLogLevel(Integer logLevel) {
			this.logLevel = logLevel;
		}

		public String getLogRemark() {
			return logRemark;
		}

		public void setLogRemark(String logRemark) {
			this.logRemark = logRemark;
		}

		public String getLogContent() {
			return logContent;
		}

		public void setLogContent(String logContent) {
			this.logContent = logContent;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public String getLogCode() {
			return logCode;
		}

		public void setLogCode(String logCode) {
			this.logCode = logCode;
		}

		public String getLineNum() {
			return lineNum;
		}

		public void setLineNum(String lineNum) {
			this.lineNum = lineNum;
		}

		public WarningEntity(String configItemCode, Integer logType, Integer logLevel, String logRemark, String logContent, Date createTime,
				String logCode, String lineNum) {
			super();
			this.configItemCode = configItemCode;
			this.logType = logType;
			this.logLevel = logLevel;
			this.logRemark = logRemark;
			this.logContent = logContent;
			this.createTime = createTime;
			this.logCode = logCode;
			this.lineNum = lineNum;
		}

		public WarningEntity() {
			super();
		}

	}