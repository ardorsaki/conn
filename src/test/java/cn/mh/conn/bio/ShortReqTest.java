package cn.mh.conn.bio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ShortReqTest {
	
	public static AtomicInteger count = new AtomicInteger(0);

	public static void main(String[] args) {
		CountDownLatch cdl = new CountDownLatch(1);
		
		for (int i = 0; i < 10; i++) {
			Thread t = new RequestThread(cdl, i);
			t.start();
		}
		
		cdl.countDown();
	}
	
	private static class RequestThread extends Thread {
		
		private int i;
		
		private CountDownLatch cdl;
		
		public RequestThread(CountDownLatch cdl, int i) {
			this.cdl = cdl;
			this.i = i;
		}

		@Override
		public void run() {
			Socket s = null;
			try {
				cdl.countDown();
				s = new Socket("127.0.0.1", 15536);
				s.setSoTimeout(30000);
				
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				
				String request = leftAlign("i" + i, 20);
				String head = rightAlignZero(request.length(), 8);
				
				dos.write((head + request).getBytes("UTF-8"));
				dos.flush();
				System.out.println("short servive write request:" + request);
				
				byte[] lenBytes = new byte[8];
				dis.readFully(lenBytes);
				int length = Integer.parseInt(new String(lenBytes));
				byte[] bodyBytes = new byte[length];
				dis.readFully(bodyBytes);
				String response = new String(bodyBytes, "UTF-8");
				System.out.println("client servive read response:" + response);
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(s != null) {
						s.close();
					}
				} catch (IOException e) {
				}
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
