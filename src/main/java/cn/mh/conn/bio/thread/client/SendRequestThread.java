package cn.mh.conn.bio.thread.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketServiceStarter;
import cn.mh.conn.util.MonitorInfo;
import cn.mh.conn.util.StringUtil;
import cn.mh.conn.util.TCPConstants;

/**
 * 长连接发送请求线程
 * @author ardorsaki
 * @date 2021年8月14日20:19:42
 *
 */
public class SendRequestThread extends Thread {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private List<Socket> clientSockets;

	public SendRequestThread(List<Socket> clientSockets) {
		this.clientSockets = clientSockets;
	}

	@Override
	public void run() {
		try {
			int pos = 0;
			while (!Thread.currentThread().isInterrupted()) {
				if(pos >= clientSockets.size()) {
					pos = 0;
				}
				logger.info("my pos:{}", pos);
				String request = SocketServiceStarter.sendReqQueue.take();
				DataOutputStream dos = new DataOutputStream(clientSockets.get(pos).getOutputStream());
				String head = StringUtil.rightAlignZero(request.length(), 8);
				dos.write((head + request).getBytes(TCPConstants.CHARSET_UTF8));
				dos.flush();
				logger.info("********** SendRequestThread send request:{} **********", request);
				pos++;
			}
		} catch (InterruptedException e) {
			logger.info("**********	SendRequestThread was Interrupted **********");
		} catch (IOException e) {
			e.printStackTrace();
			SocketServiceStarter.monitorQueue.offer(MonitorInfo.SEND_REQ_ERR);
		}
	}
}
