package id.ac.webbrowser.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		
		
		System.out.println("Welcome to Simple Browser");
		
		
		boolean status = true;
		while (status) {
			// Open URL prompt
			System.out.print("Open URL\n>");
			String url = scanner.nextLine();
			System.out.print("Type (1:Normal, 2:Login, 3:Download)\n>");
			String type = scanner.nextLine();
			
			// Request Processing
			String response = "";
			if (type.equals("1")) {
				response = makeNormalRequest(url);
				
//				boolean status2 = true;
//				while (status2) {
//					System.out.println("What do you want to do?")
//				}
//				showClickable(response);
				System.out.println(response);
				
			}
			else if (type.equals("3")) {
				System.out.print("Download Filename\n>");
				String filename = scanner.nextLine();
				URL urlObject = null;
				try {
					urlObject = new URL(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				int s = download(urlObject, filename);
				if (s == 1)
					System.out.println("--Download Success--");
				else 
					System.out.println("--Download Failed--");
			}
			else if (type.equals("2")) {
				
			}
			
		}
		scanner.close();
	}
	
	public static String makeNormalRequest(String url) {
		
		//
		String pattern, host = "", path = "";
		String res = "";
		
		if (url.contains("http")) {
			pattern = "(http://|https://)([^/]*)(.*)";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(url);
			if (m.find()) {
				host = m.group(2);
				path = m.group(3);
			}
		}
		else {
			pattern = "([^/]*)(.*)";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(url);
			if (m.find()) {
				host = m.group(1);
				path = m.group(2);
				if (!url.contains("/")) path = "/";
			}	
		}
		
		String req = "GET " + path + " HTTP/1.1\r\n" + "Host: " + host + "\r\n\r\n" ;
		
		try {
			Socket sock = new Socket(host, 80);
			
			BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
			
			bos.write(req.getBytes());
			bos.flush();
			
			byte[] bRes = new byte[1024];
			int c = bis.read(bRes);
			
			while (c != -1) {
				res += (new String(bRes));
				c = bis.read(bRes);
			}
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
//	public static String getStatusCode(String response) {
//		
//	}
	
	public static List<String> showClickable(String response) {
		String pattern = "<a.*(href=\"|href=\')([^\"\']*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(response);
		
		List<String> clickable = new ArrayList<String>();
		while(m.find()) {
			if (m.group(2).startsWith("#"))
				continue;
//			clickable.add(m.group(2));
			System.out.println(m.group(2));
		}
		return clickable;
	}
	
	public static int download(URL url, String fileName) {
		try (InputStream in = url.openStream();
                BufferedInputStream bis = new BufferedInputStream(in);
                FileOutputStream fos = new FileOutputStream(fileName)) {
 
            byte[] data = new byte[1024];
            int c = bis.read(data, 0, 1024);
            while (c != -1) {
            	fos.write(data, 0, c);
            	c = bis.read(data, 0, 1024);
            }
        }
		catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
//	public static void downloadAsync(URL url) {
//		HttpRequest httpRequest = HttpRequest
//		        .newBuilder()
//		        .uri(new URI("https://www.google.com"))
//		        .GET()
//		        .build();
//
//		Future<InputStream> futureInputStream =
//		        httpClient
//		                .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream())
//		                .thenApply(HttpResponse::body);
//
//		InputStream inputStream = futureInputStream.get();
//		Files.copy(inputStream, Path.of(outputPath));
//	}
	
	public static void redirect() {
		
	}
	
//	public static String makeLoginRequest(String url, String key1, String key2) {
//		
//	}

}
