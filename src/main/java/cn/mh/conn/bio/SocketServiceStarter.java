package cn.mh.conn.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.mh.conn.bio.thread.MonitorThread;
import cn.mh.conn.bio.thread.client.ReceiveResponseThread;
import cn.mh.conn.bio.thread.client.SendRequestThread;
import cn.mh.conn.bio.thread.client.ShortServerThread;
import cn.mh.conn.bio.thread.server.ReceiveRequestThread;
import cn.mh.conn.bio.thread.server.SendResponseThread;
import cn.mh.conn.bio.thread.server.ShortClientThread;
import cn.mh.conn.handler.LongClientHandler;
import cn.mh.conn.handler.ShortClientHandler;
import cn.mh.conn.util.MonitorInfo;

/**
 * 长短连接转换，只需要部分可以只使用其中部分代码
 * @author ardorsaki
 * @date 2021年8月10日20:52:58
 */
@Component
public class SocketServiceStarter implements LongClientHandler, ShortClientHandler {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SocketConfig socketConfig;
	
//	private SocketServiceStarter starter;
	
	/**
	 * 线程池
	 */
	public static ThreadPoolExecutor pool;
	
	/**
	 * 长连接:接收请求
	 * @key 长连接Map对应位置
	 * @value 连接
	 */
	private Map<Integer, Socket> serverSockets;
	
	/**
	 * 长连接:发送请求
	 */
	private List<Socket> clientSockets;
	
	/**
	 * 短连接服务端
	 */
	private ServerSocket shortServer;
	
	/**
	 * 对长连接发送请求queue
	 */
	public static LinkedBlockingQueue<String> sendReqQueue;
	
	/**
	 * 接收长连接响应Map
	 * @key 请求id
	 * @value 接收响应阻塞队列
	 */
	public static ConcurrentHashMap<String, SynchronousQueue<String>> receiveRespMap;
	
	/**
	 * 对长连接接收请求queue
	 */
	public static LinkedBlockingQueue<String> receiveReqQueue;
	
	/**
	 * 请求对应长连接的Map
	 * @key requestId
	 * @value {@link SocketServiceStarter#serverSockets} key
	 */
	public static ConcurrentHashMap<String, Integer> reqConSocketMap;
	
	/**
	 * 对长连接发送响应Map
	 * @key {@link SocketServiceStarter#serverSockets} key
	 * @value sendRespQueue
	 */
	public static ConcurrentHashMap<Integer, LinkedBlockingQueue<String>> sendRespMap;
	
	/**
	 * 错误queue
	 */
	public static LinkedBlockingQueue<MonitorInfo> monitorQueue;
	
	public SocketServiceStarter() {
		init();
	}
	
	public void init() {
		sendReqQueue = new  LinkedBlockingQueue<String>(1000);
		receiveRespMap = new ConcurrentHashMap<String, SynchronousQueue<String>>();
		
		receiveReqQueue = new LinkedBlockingQueue<String>(1000);
		reqConSocketMap = new ConcurrentHashMap<String, Integer>();
		sendRespMap = new ConcurrentHashMap<Integer, LinkedBlockingQueue<String>>();
		
		monitorQueue = new LinkedBlockingQueue<MonitorInfo>(1);
		
		serverSockets = new ConcurrentHashMap<Integer, Socket>();
		clientSockets = new ArrayList<Socket>();
		
		logger.info("********** init SocketServiceStarter **********");
	}
	
	public void start() {
		pool = new ThreadPoolExecutor(40, 40, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100), new rejectExec());
		
//		connectClient();
		connectServer();
		
		//short server
//		createShortServer();
		
		//short -> long
//		startShortServerThread();
//		startSendRequestThread();
//		startReceiveResponseThread();
		
		//long -> short
		startShortClientThread();
		startSendResponseThread();
		startReceiveRequestThread();
		
		startMonitorThread();
	}
	
	public void createShortServer() {
		try {
			shortServer = new ServerSocket(socketConfig.getLocalPort());
		} catch (IOException e) {
			logger.error("********** createShortServer IOException **********");
		}
	}
	
	public void connectClient() {
		try {
			for (int i = 0; i < socketConfig.getConSize(); i++) {
				Socket s = new Socket(socketConfig.getLip(), socketConfig.getLcport());
				s.setKeepAlive(true);
				s.setSoTimeout(60000);
				clientSockets.add(s);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			monitorQueue.offer(MonitorInfo.CONNECT_ERR);
		}
	}
	
	public void connectServer() {
		try {
			int size = 1000 / socketConfig.getConSize();
			for (int i = 0; i < socketConfig.getConSize(); i++) {
				Socket s = new Socket(socketConfig.getLip(), socketConfig.getLsport());
				s.setKeepAlive(true);
				s.setSoTimeout(60000);
				clientSockets.add(s);
				serverSockets.put(i, s);
				sendRespMap.put(i, new LinkedBlockingQueue<String>(size));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			monitorQueue.offer(MonitorInfo.CONNECT_ERR);
		}
	}
	
	@Override
	public void startShortServerThread() {
		pool.execute(new ShortServerThread(socketConfig, shortServer));
	}
	
	@Override
	public void startShortClientThread() {
		pool.execute(new ShortClientThread(socketConfig));
	}
	
	@Override
	public void startSendRequestThread() {
		pool.execute(new SendRequestThread(clientSockets));
	}
	
	@Override
	public void startReceiveResponseThread() {
		clientSockets.forEach(s -> {
			pool.execute(new ReceiveResponseThread(s));
		});
	}
	
	@Override
	public void startSendResponseThread() {
		serverSockets.forEach((k, s) -> {
			pool.execute(new SendResponseThread(k, s));
		});
	}
	
	@Override
	public void startReceiveRequestThread() {
		serverSockets.forEach((k, s) -> {
			pool.execute(new ReceiveRequestThread(socketConfig, k, s));
		});
	}
	
	public void startMonitorThread() {
		new MonitorThread(this).start();
	}
	
	public void destory() {
		serverSockets.forEach((k, s) -> {
			try {
				s.close();
			} catch (IOException e) {
			}
		});
		serverSockets.clear();
		
		clientSockets.forEach(s -> {
			try {
				s.close();
			} catch (IOException e) {
			}
		});
		clientSockets.clear();
		
		try {
			if(shortServer != null)
				shortServer.close();
		} catch (IOException e1) {
		}
		
		pool.shutdownNow();
		try {
			pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
	}

	private static class rejectExec implements RejectedExecutionHandler {
		
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
			}
			executor.execute(r);
		}
	}
}
