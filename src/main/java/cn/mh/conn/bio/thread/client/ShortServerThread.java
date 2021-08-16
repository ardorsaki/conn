package cn.mh.conn.bio.thread.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketServiceStarter;
import cn.mh.conn.bio.SocketConfig;

/**
 * 短连接服务端
 * @author ardorsaki
 * @date 2021年8月14日20:20:07
 *
 */
public class ShortServerThread extends Thread {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SocketConfig constants;
	
	private ServerSocket shortServer;


	public ShortServerThread(SocketConfig constants, ServerSocket shortServer) {
		this.constants = constants;
		this.shortServer = shortServer;
	}


	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			logger.info("**********	ShortServerThread start **********");
			logger.info("**********	Short Server Start localPort:{} **********", constants.getLocalPort());
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Socket s = shortServer.accept();
					s.setSoTimeout(constants.getTimeOutVal());
					SocketServiceStarter.pool.execute(new ShortRequestThread(constants, s));
				} catch (IOException e) {
					logger.info("**********	shortServer accept catch IOException:{} **********", e.getMessage());
				}
			}
				
			try {
				TimeUnit.SECONDS.sleep(30);
			} catch (InterruptedException e) {
				logger.info("**********	ShortServerThread was Interrupted **********");
				Thread.currentThread().interrupt();
			}
		}
	}
	
}
