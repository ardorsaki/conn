package cn.mh.conn.handler;

public interface LongClientHandler {

	/**
	 * 向长连接发送请求线程
	 */
	void startSendRequestThread();
	
	/**
	 * 接收长连接响应线程
	 */
	void startReceiveResponseThread();
	
	/**
	 * 向长连接发送响应线程(若长连接不分通道，则仅使用前两个方法)
	 */
	void startSendResponseThread();
	
	/**
	 * 接收长连接请求线程(若长连接不分通道，则仅使用前两个方法)
	 */
	void startReceiveRequestThread();
}
