package id.ac.webbrowser.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
//import java.util.logging.Level;
//import java.util.logging.Logger;


public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
//			Socket socket = new Socket ("monta.if.its.ac.id",80);
//			Socket socket = new Socket ("komiku.id",80);
//			Socket socket = new Socket ("youtube.com",80);
			Socket socket = new Socket ("classroom.its.ac.id",80);
			
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			
//			System.out.println("1");
//			bos.write("GET /index.php/berita/lihatBerita HTTP/1.1\r\nHost: monta.if.its.ac.id\r\n\r\n".getBytes());
//			bos.write("GET / HTTP/1.1\r\nHost: komiku.id\r\n\r\n".getBytes());
			bos.write("GET / HTTP/1.1\r\nHost: classroom.its.ac.id\r\n\r\n".getBytes());
//			bos.write("GET / HTTP/1.1\r\nHost: youtube.com\r\n\r\n".getBytes());

			bos.flush();
//			System.out.println("2");
			
			int bufferSize = 1024;
			byte[] bResp = new byte[bufferSize];
			int c = bis.read(bResp);
			String resp = "";
//			String resp = bis.readLine();
//			String[] first_line = resp.split(" ");
			
			while(c != -1) {
				resp += (new String(bResp));
//				resp += (new String("\n"));
				c = bis.read(bResp);
			}
			
//			System.out.println("3");
			System.out.println(resp);
//			System.out.println("");
			
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE,null,ex);
		}

	}

}
