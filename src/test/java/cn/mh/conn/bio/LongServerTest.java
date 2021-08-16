package cn.mh.conn.bio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务通道
 * long -> short
 * @author ardorsaki
 *
 */
public class LongServerTest {

	public static void main(String[] args) {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(8602);
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
				int count = 0;
				for(;;) {
					try {
						try {
							Thread.sleep(15000);
						} catch (InterruptedException e) {
						}
						String request = leftAlign(pos + "s" + count, 10) + System.currentTimeMillis();
						String head = rightAlignZero(request.length(), 8);
						dos.write((head + request).getBytes("UTF-8"));
						dos.flush();
						System.out.println(pos + ":server servive write request:" + request);
						
						byte[] lenBytes = new byte[8];
						dis.readFully(lenBytes);
						int length = Integer.parseInt(new String(lenBytes));
						byte[] bodyBytes = new byte[length];
						dis.readFully(bodyBytes);
						String response = new String(bodyBytes, "UTF-8");
						System.out.println(pos + ":server servive read response:" + response);
						
						count++;
					} catch (SocketTimeoutException e) {
						continue;
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String leftAlign(String str, int length) {
		if(str == null) {
			return String.format("%-" + length + "s", "");
		} else if(str.length() < length) {
			return String.format("%-" + length + "s", str);
		} else {
			return str;
		}
	}
	
	public static String rightAlignZero(int i, int length) {
		return String.format("%0" + length + "d", i);
	}
}
