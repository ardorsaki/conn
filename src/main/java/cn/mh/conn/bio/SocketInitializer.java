package cn.mh.conn.bio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动init类
 * @author ardorsaki
 * @date 2021年8月14日20:21:49
 *
 */
@Component
public class SocketInitializer implements ApplicationRunner {
	
	@Autowired
	private SocketConfig socketConfig;
	
	@Autowired
	private SocketServiceStarter socketServiceStarter;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//初始化方法
//		SocketServiceStarter sc = new SocketServiceStarter();
		socketServiceStarter.start();
	}

	public SocketConfig getSocketConfig() {
		return socketConfig;
	}

	public void setSocketConfig(SocketConfig socketConfig) {
		this.socketConfig = socketConfig;
	}

}
