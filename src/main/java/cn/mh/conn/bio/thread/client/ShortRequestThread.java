package cn.mh.conn.bio.thread.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketServiceStarter;
import cn.mh.conn.bio.SocketConfig;
import cn.mh.conn.util.StringUtil;
import cn.mh.conn.util.TCPConstants;

/**
 * 短连接处理线程
 * @author ardorsaki
 * @date 2021年8月14日20:19:57
 *
 */
public class ShortRequestThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SocketConfig constants;
	
	private Socket socket;

	public ShortRequestThread(SocketConfig constants, Socket socket) {
		this.constants = constants;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			//读数据,前8位代表请求体总长,请求体前10位代表请求序号
			byte[] lenBytes = new byte[8];
			dis.readFully(lenBytes);
			int length = Integer.parseInt(new String(lenBytes));
			byte[] bodyBytes = new byte[length];
			dis.readFully(bodyBytes);
			String request = new String(bodyBytes, TCPConstants.CHARSET_UTF8);
			logger.info("********** ShortRequestThread read request:{} **********", request);
			
			//请求插入发送队列，等待发送线程发送数据
			SocketServiceStarter.sendReqQueue.offer(request);
			
			//阻塞接收响应
			SynchronousQueue<String> queue = new SynchronousQueue<String>();
			SocketServiceStarter.receiveRespMap.put(request.substring(0, 10), queue);
			String response = queue.poll(constants.getTimeOutVal(), TimeUnit.MILLISECONDS);
			
			if(response != null) {
				logger.info("********** ShortRequestThread write response:{} **********", response);
				//正常收到响应
				String head = StringUtil.rightAlignZero(response.length(), 8);
				dos.write((head + response).getBytes(TCPConstants.CHARSET_UTF8));
				dos.flush();
			} else {
				//超时
			}
			
			SocketServiceStarter.receiveRespMap.remove(request.substring(0, 10));
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null)
					socket.close();
			} catch (IOException e) {
			}
		}
	}
}
