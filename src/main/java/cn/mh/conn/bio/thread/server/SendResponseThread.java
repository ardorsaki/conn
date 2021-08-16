package cn.mh.conn.bio.thread.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketServiceStarter;
import cn.mh.conn.util.MonitorInfo;
import cn.mh.conn.util.StringUtil;
import cn.mh.conn.util.TCPConstants;

/**
 * 向长连接发送响应线程
 * @author ardorsaki
 * @date 2021年8月14日20:21:02
 *
 */
public class SendResponseThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Integer k;
	
	private Socket s;
	
	public SendResponseThread(Integer k, Socket s) {
		this.k = k;
		this.s = s;
	}

	@Override
	public void run() {
		try {
			//多个线程take
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			LinkedBlockingQueue<String> bq = SocketServiceStarter.sendRespMap.get(k);
			while (!Thread.currentThread().isInterrupted()) {
				String response = bq.take();
				String head = StringUtil.rightAlignZero(response.length(), 8);
				dos.write((head + response).getBytes(TCPConstants.CHARSET_UTF8));
				dos.flush();
				logger.info("********** SendResponseThread send response:{} **********", response);
			}
			
			//单个线程take,需要把serverSockets传入
//			LinkedBlockingQueue<String> bq = SocketServiceStarter.sendRespMap.get(1);
//			while (!Thread.currentThread().isInterrupted()) {
//				String response = bq.take();
//				String requestId = response.substring(0, 10);
//				Socket socket = serverSockets.get(SocketServiceStarter.reqConSocketMap.remove(requestId));
//				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//				String head = StringUtil.rightAlignZero(response.length(), 8);
//				dos.write((head + response).getBytes(TCPConstants.CHARSET_UTF8));
//				dos.flush();
//				logger.info("********** SendResponseThread send response:{} **********", response);
//			}
			
		} catch (InterruptedException e) {
			logger.info("**********	SendResponseThread was Interrupted **********");
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("**********	SendResponseThread catch IOException:{} **********", e.getMessage());
			SocketServiceStarter.monitorQueue.offer(MonitorInfo.SEND_RESP_ERR);
		}
	}
}
