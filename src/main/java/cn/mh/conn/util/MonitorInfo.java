package cn.mh.conn.util;

/**
 * 控制队列枚举
 * @author ardorsaki
 *
 */
public enum MonitorInfo {
	
	CONNECT_ERR("E01001","long connect error"),
	RECV_RESP_ERR("E01002","recv resp thread error"),
	SEND_REQ_ERR("E01003","send req thread error"),
	RECV_REQ_ERR("E01004","recv req thread error"),
	SEND_RESP_ERR("E01005","send resp thread error"),
	
	SERVICE_STOP("N09999","stop server");
	
	private String code;
	
	private String message;
	
	private MonitorInfo(String code, String message) {
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
