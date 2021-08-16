package cn.mh.conn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("cn.mh.conn")
@SpringBootApplication
public class ConnApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnApplication.class, args);
	}

}
