package cn.mh.conn.bio.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.mh.conn.bio.SocketServiceStarter;
import cn.mh.conn.util.MonitorInfo;

public class MonitorThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private SocketServiceStarter starter;
	
	public MonitorThread(SocketServiceStarter starter) {
		this.starter = starter;
	}
	
	@Override
	public void run() {
		try {
			MonitorInfo monitorInfo = SocketServiceStarter.monitorQueue.take();
			logger.info("********** MonitorThread take message:{}, restart **********", monitorInfo);
			starter.destory();
			/**********	清理数据内存 **********/
			SocketServiceStarter.sendReqQueue.clear();
			SocketServiceStarter.sendRespMap.clear();
			SocketServiceStarter.receiveRespMap.clear();
			SocketServiceStarter.reqConSocketMap.clear();
			SocketServiceStarter.monitorQueue.clear();
			/**********	清理数据内存 **********/
			if(!MonitorInfo.SERVICE_STOP.equals(monitorInfo)) {
				starter.start();
			}
		} catch (InterruptedException e) {
		}
	}

}
