package cn.mh.conn.handler;

public interface ShortClientHandler {

	/**
	 * 对外短连接服务线程
	 */
	void startShortServerThread();
	
	/**
	 * 向外发起短连接线程
	 */
	void startShortClientThread();
}
