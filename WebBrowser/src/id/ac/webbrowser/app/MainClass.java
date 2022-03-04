package id.ac.webbrowser.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainClass {
	
	private static String root;
	private static String[] paths ;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		
		
		System.out.println("Welcome to Simple Browser");
		
		
		boolean status = true;
		while (status) {
			// Open URL prompt
			System.out.print("Open URL\n>");
			String url = scanner.nextLine();
			System.out.print("Type (1:NormalReq, 2:BasicAuthReq, 3:Download, 4.LoginReq)\n>");
			String type = scanner.nextLine();
			
			// Request Processing
			String response = "";
			if (type.equals("1") || type.equals("2") || type.equals("4")) {
				boolean abort = false;
				if (type.equals("1"))
					response = makeNormalRequest(url, "");
				else {
					System.out.print("Insert Username\n>");
					String id = scanner.nextLine();
					System.out.print("Insert Password\n>");
					String password = scanner.nextLine();
					if (type.equals("2"))
						response = makeBasicAuthRequest(url, id, password);
					else 
						response = makeLoginRequest(url, id, password);
				}
				String statusCode = getStatusCode(response);
				String statusMessage = getStatusMessage(response);
				
				if (statusCode.charAt(0) == '3') {
					System.out.println("You got message as > " + statusMessage);
					System.out.println("Status Code: " + statusCode);
					System.out.println("Redirecting...");
					response = redirect(response);
					System.out.println(response);
				}
				else if (statusCode.charAt(0) == '4') {
					System.out.println("You got an error with message: " + statusMessage);
					System.out.println("Status Code: " + statusCode);
					System.out.println("Aborting Connection...");
					abort = true;
				}
				else {
					System.out.println(response);
				}
				
				while (!abort) {
					System.out.println("What would you like to do?");
					System.out.println("1. Show Clickable Links");
					System.out.println("2. Show Status Code and Message");
					System.out.println("3. Make another request URL");
					System.out.print("Choice > ");
					String choice = scanner.nextLine();
					
					if (choice.equals("1")) {
						showClickable(response);
					}
					else if (choice.equals("2")) {
						System.out.println("Response Status Code: " + statusCode);
						System.out.println("Response Message: " + statusMessage);
					}
					else if (choice.equals("3")) {
						abort = true;
					}
				}
				
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
			
		}
		scanner.close();
	}
	
	public static String makeNormalRequest(String url, String additional) {
		
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
				if (!url.contains("/")) path = "/";
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
		
		root = host;
		paths = path.split("/", 0);
		
		String req = "GET " + path + " HTTP/1.1\r\n" + "Host: " + host
				+ additional + "\r\n\r\n";
		System.out.println(req);
		try {
			Socket sock = new Socket(host, 80);
			
			BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
			
			bos.write(req.getBytes());
			bos.flush();
			
			byte[] bRes = new byte[1024];
			int c = bis.read(bRes, 0, 1024);
			
			while (c != -1) {
				res += (new String(bRes));
				c = bis.read(bRes, 0, 1024);
			}
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	 public static String getStatusCode(String response) {
        String statusCode = "";
        
        String pattern = "(HTTP....) (\\w+) (.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);
        if(m.find()) {
            statusCode = m.group(2);
        }
        return statusCode;
    }
    
    public static String getStatusMessage(String response) {
        String msg = "";
        
        String pattern = "(HTTP....) (\\w+) (.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);
        if(m.find()) {
            msg = m.group(3);
        }
        return msg;
    }
	
	public static List<String> showClickable(String response) {
		String pattern = "<a.*(href=\"|href=\')([^\"\']*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(response);
		
		List<String> clickable = new ArrayList<String>();
		while(m.find()) {
			if (m.group(2).startsWith("#"))
				continue;
			clickable.add(m.group(2));
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
	
	public static String redirect(String response) {
        String pattern = "(Location:) (.*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);
        
        String newUrl = ""; 
        if(m.find()) {
            newUrl = m.group(2);
        }
        
        String absolutePath = "";
        if (!newUrl.contains("http")) {
        	absolutePath = root + "/";
        }
        
        if (paths.length > 1) {
        	for (int i = 0 ; i < paths.length-1 ; i++) {
        		absolutePath += (paths[i] + "/");
        	}
        }
        
        absolutePath += newUrl;
        
        String cookie = "";
        if (isThereCookie(response)) {
        	cookie = "\r\nCookie: " + getCookie(response);
        }
        
        return makeNormalRequest(absolutePath, cookie);
    }
	
	public static String makeBasicAuthRequest(String url, String id, String password) {
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
		
		root = host;
		paths = path.split("/", 0);
		
		// Make Base64
		String idpass = id + ":" + password;
		String encoded = Base64.getEncoder().encodeToString(idpass.getBytes());
		
		String req = "GET " + path + " HTTP/1.1\r\n" + "Host: " + host + "\r\n" 
						+ "Authorization: Basic " + encoded + "\r\n\r\n";
		
		try {
			Socket sock = new Socket(host, 80);
			
			BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
			
			bos.write(req.getBytes());
			bos.flush();
			
			byte[] bRes = new byte[1024];
			int c = bis.read(bRes, 0, 1024);
			
			while (c != -1) {
				res += (new String(bRes));
				c = bis.read(bRes, 0, 1024);
			}
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public static String makeLoginRequest(String url, String key1, String key2) {
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
		
		root = host;
		paths = path.split("/", 0);
		
		try {
			key1 = URLEncoder.encode(key1, "UTF-8" );
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String payload = "email=" + key1 + "&password=" + key2 + "&submit=";
		String req = "POST " + path + " HTTP/1.1\r\n" + "Host: " + host + "\r\nContent-Type: application/x-www-form-urlencoded\r\n"
				+ "Content-Length: " + payload.length() + "\r\n\r\n" + payload;
//		System.out.println(req);
		try {
			Socket sock = new Socket(host, 80);
			
			BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());
			BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
			
			bos.write(req.getBytes());
			bos.flush();
			
			byte[] bRes = new byte[1024];
			int c = bis.read(bRes, 0, 1024);
			
			while (c != -1) {
				res += (new String(bRes));
				c = bis.read(bRes, 0, 1024);
			}
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public static String getCookie(String response) {
		String pattern = "Set-Cookie: (.*)(PHPSESSID=[^\\s]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(response); 
		if (m.find()) {
			return m.group(2);
		}
		return "";
	}
	
	public static boolean isThereCookie(String response) {
		String pattern = "Set-Cookie: ";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(response); 
		if (m.find()) {
			return true;
		}
		return false;
	}
	
}
