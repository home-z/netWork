package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

/**TCP
 * 
 * @author Administrator
 *客户端重点：socket.getOutputStream()客户端向服务器发送数据
 *	   	    socket.getInputStream()客户端接收数据
 */
public class Client {
	/**
	 * java.net.Socket  套接字
	 * 封装了TCP通讯.使用该类完成与服务端的连接
	 * 并进行相应的通讯.
	 */
	private Socket socket;
	
	/**
	 * 构造器用于初始化客户端
	 */
	public Client() {
		try {
			/*
			 * 实例化Socket时需要传入两个参数
			 * 1:服务端的地址
			 * 2:服务端的端口
			 * 
			 * 通过地址找到服务端的计算机,端口
			 * 则找到该计算机上的服务端应用程序.
			 * 
			 * 实例化Socket的过程就是连接服务端
			 * 的过程.连接不成功该构造方法会抛出
			 * 异常.
			 */
			System.out.println("连接服务器中....");
			socket = new Socket("localhost", 8707);
			System.out.println("已和服务端建立连接！");
		} catch (Exception e) {
			throw new RuntimeException("客户端异常");
		}
	}
	
	/**
	 * 客户端启动方法
	 */
	@SuppressWarnings("resource")
	public void start() {
		try {
		/*
		 * 先启动用于接收服务端发送过来的消息的
		 * 线程
		 */
		ServerHandler handler = new ServerHandler();
		Thread thread = new Thread(handler);
		thread.start();
		Scanner scanner = new Scanner(System.in);
			/*
			 * Socket提供了方法:
			 * OutputStream getOutputStream()
			 * 该方法可以获取一个字节输出流,通过
			 * 该输出流写出的数据会发送至远端计算机
			 * 对于客户端这边而言远端是服务端.
			 */
		//输出流自动行刷新-输出流指定字符集
			PrintWriter pw  = new PrintWriter(
				new OutputStreamWriter
				(socket.getOutputStream(), "UTF-8"),true);
			String message = null;
			while (true) {
				message = scanner.nextLine();
				pw.println(message);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("不支持的编码");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("文件写出异常");
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
	
	/**
	 * 该线程负责循环接收服务端发送过来的消息
	 * 并输出到客户端的控制台
	 * @author Administrator
	 *
	 */
	class ServerHandler implements Runnable {
		public void run() {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), "UTF-8"));
				String message = null;
				while ((message = br.readLine()) != null) {
					System.out.println(message);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.out.println("不支持的编码");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("读取有误");
			}
		}
	}
}
