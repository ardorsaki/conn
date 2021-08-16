package cn.mh.conn.bio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 长连接客户通道端口
 * short -> long
 * @author ardorsaki
 *
 */
public class ShortServerTest {

	public static void main(String[] args) {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(15436);
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
					
				byte[] lenBytes = new byte[8];
				dis.readFully(lenBytes);
				int length = Integer.parseInt(new String(lenBytes));
				byte[] bodyBytes = new byte[length];
				dis.readFully(bodyBytes);
				String request = new String(bodyBytes, "UTF-8");
				System.out.println(pos + ":short server read request:" + request);
				
				String response = request + System.currentTimeMillis();
				String head = rightAlignZero(response.length(), 8);
				dos.write((head + response).getBytes("UTF-8"));
				dos.flush();
				System.out.println(pos + ":short server write response:" + response);
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					s.close();
				} catch (IOException e) {
				}
			}
			
		}
		
	}
	
	public static String rightAlignZero(int i, int length) {
		return String.format("%0" + length + "d", i);
	}
}
