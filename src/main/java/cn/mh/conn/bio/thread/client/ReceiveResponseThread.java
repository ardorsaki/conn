package cn.mh.conn.bio.thread.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.SynchronousQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketServiceStarter;
import cn.mh.conn.util.MonitorInfo;
import cn.mh.conn.util.TCPConstants;


/**
 * 读长连接响应线程
 * @author ardorsaki
 * @date 2021年8月14日20:19:28
 *
 */
public class ReceiveResponseThread extends Thread {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Socket socket;
	
	public ReceiveResponseThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					DataInputStream dis = new DataInputStream(socket.getInputStream());
					
					byte[] lenBytes = new byte[8];
					dis.readFully(lenBytes);
					int length = Integer.parseInt(new String(lenBytes));
					byte[] bodyBytes = new byte[length];
					dis.readFully(bodyBytes);
					String response = new String(bodyBytes, TCPConstants.CHARSET_UTF8);
					
					logger.info("********** ReceiveResponseThread read response:{} **********", response);
					
					SynchronousQueue<String> queue = SocketServiceStarter.receiveRespMap.get(response.substring(0, 10));
					if(queue != null) {
						queue.offer(response);
					}
				} catch (SocketTimeoutException e) {
					continue;
				}
			}
		} catch (IOException e) {
			//测试时打印栈信息，生产根据日志要求输出
			e.printStackTrace();
			logger.info("ReceiveResponseThread IOException");
			SocketServiceStarter.monitorQueue.offer(MonitorInfo.RECV_RESP_ERR);
		}
	}

}
