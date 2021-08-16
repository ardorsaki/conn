package cn.mh.conn.bio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读配置类
 * @author ardorsaki
 * @date 2021年8月14日20:22:15
 *
 */
@Component
public class SocketConfig {

	@Value("${bio.socket.long.server.ip}")
	private String lip;
	
	@Value("${bio.socket.long.server.cport}")
	private int lcport;
	
	@Value("${bio.socket.long.server.sport}")
	private int lsport;
	
	@Value("${bio.socket.long.connect.size}")
	private int conSize;
	
	@Value("${bio.socket.short.connect.server.ip}")
	private String severIp;
	
	@Value("${bio.socket.short.connect.server.port}")
	private int severPort;
	
	@Value("${bio.socket.short.connect.server.localPort}")
	private int localPort;
	
	@Value("${time.out.val}")
	private int timeOutVal;

	public String getLip() {
		return lip;
	}

	public void setLip(String lip) {
		this.lip = lip;
	}

	public int getLcport() {
		return lcport;
	}

	public void setLcport(int lcport) {
		this.lcport = lcport;
	}

	public int getLsport() {
		return lsport;
	}

	public void setLsport(int lsport) {
		this.lsport = lsport;
	}

	public int getConSize() {
		return conSize;
	}

	public void setConSize(int conSize) {
		this.conSize = conSize;
	}

	public String getSeverIp() {
		return severIp;
	}

	public void setSeverIp(String severIp) {
		this.severIp = severIp;
	}

	public int getSeverPort() {
		return severPort;
	}

	public void setSeverPort(int severPort) {
		this.severPort = severPort;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public int getTimeOutVal() {
		return timeOutVal;
	}

	public void setTimeOutVal(int timeOutVal) {
		this.timeOutVal = timeOutVal;
	}

}
