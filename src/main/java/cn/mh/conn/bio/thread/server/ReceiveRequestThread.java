package cn.mh.conn.bio.thread.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketConfig;
import cn.mh.conn.bio.SocketServiceStarter;
import cn.mh.conn.util.MonitorInfo;
import cn.mh.conn.util.TCPConstants;

/**
 * 接收长连接请求线程
 * @author ardorsaki
 * @date 2021年8月14日20:20:36
 *
 */
public class ReceiveRequestThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SocketConfig socketConfig;
	
	private Integer k;
	
	private Socket s;
	
	public ReceiveRequestThread(SocketConfig socketConfig, Integer k, Socket s) {
		this.socketConfig = socketConfig;
		this.k = k;
		this.s = s;
	}

	@Override
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(s.getInputStream());
			while (!Thread.currentThread().isInterrupted()) {
				try {
					//读数据,前8位代表请求体总长,请求体前10位代表请求序号
					byte[] lenBytes = new byte[8];
					dis.readFully(lenBytes);
					int length = Integer.parseInt(new String(lenBytes));
					byte[] bodyBytes = new byte[length];
					dis.readFully(bodyBytes);
					
					String request = new String(bodyBytes, TCPConstants.CHARSET_UTF8);
					String requestId = request.substring(0, 10);
					logger.info("********** ReceiveRequestThread read request:{} **********", request);
						SocketServiceStarter.receiveReqQueue.offer(request, socketConfig.getTimeOutVal(), TimeUnit.MILLISECONDS);
						SocketServiceStarter.reqConSocketMap.put(requestId, k);
				} catch (SocketTimeoutException e) {
					continue;
				}
			}
		} catch (InterruptedException e) {
			logger.info("**********	ReceiveRequestThread was Interrupted **********");
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("**********	ReceiveRequestThread catch IOException:{} **********", e.getMessage());
			SocketServiceStarter.monitorQueue.offer(MonitorInfo.RECV_REQ_ERR);
		}
	}
}
