package cn.mh.conn.bio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 长连接客户通道端口
 * short -> long
 * @author ardorsaki
 *
 */
public class LongClientTest {

	public static void main(String[] args) {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(8601);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			AtomicInteger pos = new AtomicInteger(0);
			for(;;) {
				Socket s = ss.accept();
				s.setSoTimeout(30000);
				Thread t = new HandleAcceptThread(pos.getAndIncrement(), s);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class HandleAcceptThread extends Thread {
		
		private int pos;
		
		private Socket s;
		
		public HandleAcceptThread(int pos, Socket s) {
			this.pos = pos;
			this.s = s;
		}

		@Override
		public void run() {
			try {
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				for(;;) {
					
					try {
						byte[] lenBytes = new byte[8];
						dis.readFully(lenBytes);
						int length = Integer.parseInt(new String(lenBytes));
						byte[] bodyBytes = new byte[length];
						dis.readFully(bodyBytes);
						String request = new String(bodyBytes, "UTF-8");
						System.out.println(pos + ":client servive read request:" + request);
						
						String response = request + System.currentTimeMillis();
						String head = rightAlignZero(response.length(), 8);
						dos.write((head + response).getBytes("UTF-8"));
						dos.flush();
						System.out.println(pos + ":client servive write response:" + response);
					} catch (SocketTimeoutException e) {
						continue;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static String rightAlignZero(int i, int length) {
		return String.format("%0" + length + "d", i);
	}
}
