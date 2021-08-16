package cn.mh.conn.bio.thread.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketConfig;
import cn.mh.conn.bio.SocketServiceStarter;
import cn.mh.conn.util.StringUtil;
import cn.mh.conn.util.TCPConstants;

/**
 * 建立短连接,发送请求线程
 * @author ardorsaki
 * @date 2021年8月14日20:20:53
 *
 */
public class ReqShortServerThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String request;
	
	private SocketConfig socketConfig;
	
	public ReqShortServerThread(String request, SocketConfig socketConfig) {
		this.request = request;
		this.socketConfig = socketConfig;
	}
	
	@Override
	public void run() {
		Socket socket = null;
		try {
			//connect
			socket = new Socket(socketConfig.getSeverIp(), socketConfig.getSeverPort());
			socket.setSoTimeout(socketConfig.getTimeOutVal());
			//stream
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			//write
			String head = StringUtil.rightAlignZero(request.length(), 8);
			dos.write((head + request).getBytes(TCPConstants.CHARSET_UTF8));
			dos.flush();
			//read
			byte[] lenBytes = new byte[8];
			dis.readFully(lenBytes);
			int length = Integer.parseInt(new String(lenBytes));
			byte[] bodyBytes = new byte[length];
			dis.readFully(bodyBytes);
			String response = new String(bodyBytes, TCPConstants.CHARSET_UTF8);
			String requestId = response.substring(0, 10);
			//offer
			Integer key = SocketServiceStarter.reqConSocketMap.remove(requestId);
			SocketServiceStarter.sendRespMap.get(key).offer(response, socketConfig.getTimeOutVal(), TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			logger.info("**********	ReqShortServerThread was Interrupted **********");
		} catch (SocketTimeoutException e) {
			logger.info("**********	ReqShortServerThread socket time out **********");
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("**********	ReqShortServerThread catch IOException:{} **********", e.getMessage());
		} finally {
			try {
				if(socket != null) {
					socket.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
