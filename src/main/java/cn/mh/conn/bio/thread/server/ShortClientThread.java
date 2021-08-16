package cn.mh.conn.bio.thread.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketConfig;
import cn.mh.conn.bio.SocketServiceStarter;

/**
 * 接收到长连接请求，建立短连接线程的处理线程
 * @author ardorsaki
 * @date 2021年8月14日20:20:44
 *
 */
public class ShortClientThread extends Thread {

private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SocketConfig socketConfig;
	
	public ShortClientThread(SocketConfig socketConfig) {
		this.socketConfig = socketConfig;
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				String request = SocketServiceStarter.receiveReqQueue.take();
				SocketServiceStarter.pool.execute(new ReqShortServerThread(request, socketConfig));
			}
		} catch (InterruptedException e1) {
			logger.info("**********	ShortClientThread was Interrupted **********");
		}
	}
}
