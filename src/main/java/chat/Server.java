package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
/**
 * 服务端
 * @author Administrator
 *
 */
public class Server {
	private ServerSocket server;

	//该集合用于保存所有客户端的Socket
	private List<PrintWriter> allOut;
	public Server() throws IOException {
		try {
			allOut = new ArrayList<PrintWriter>();
			/**
			 * java.net.ServerSocket
			 * 运行在服务端的ServerSocket有两个作用
			 * 1:申请服务端口(客户端通过该端口与服务端建立连接)
			 * 2:监听服务端口,等待客户端连接.一旦客户端连接则
			 *   创建一个Socket实例用于与该客户端交互.
			 */
			server = new ServerSocket(8707);
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * 将给定的输出流存入共享集合
	 * @param out
	 */
	private synchronized void addOut(PrintWriter out) {
		allOut.add(out);
	}
	
	/**
	 * 将给定的输出流从共享集合中删除
	 * @param out
	 */
	private synchronized void removeOut(PrintWriter out) {
		allOut.remove(out);
	}
	
	/**
	 * 将给定的消息发送给所有客户端
	 * @param message
	 */
	private synchronized void sendMessage(String message) {
		for (PrintWriter print : allOut) {
			print.println(message);
		}
	}
	
	public void start(){
		try {
			/*
			 * ServerSocket提供了方法:
			 * Socket accept()
			 * 该方法是一个阻塞方法,作用是监听
			 * ServerSocket开启的服务端口,
			 * 直到一个客户端通过该端口连接,该方法
			 * 才会解除阻塞,并返回一个Socket实例
			 * 通过该Socket实例与刚刚建立连接的
			 * 客户端进行通讯.
			 */
			while (true) {
				System.out.println("等待客户端连接...");
				Socket socket = server.accept();
				System.out.println("一个客户端已连接！");
				/**
				 * 当一个客户端连接后,启动一个线程来处理
				 * 与该客户端的交互工作.
				 */
				ClientHandler handler = new ClientHandler(socket);
				Thread t = new Thread(handler);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("服务端-Start!");
		}
	}
	
	public static void main(String[] args){
		try {
			Server server = new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 此线程负责与指定的客户端进行交互
	 * @author Administrator
	 *
	 */
class ClientHandler implements Runnable {
	//当前线程负责与指定客户端交互的Socket
	private Socket socket;
	//客户端地址信息
	private String host;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
		/**
		 * 通过socket获取远程计算机地址信息
		 * 对于服务端而言，远程计算机就是客户端
		 */
		InetAddress address = socket.getInetAddress();
		//获取远端计算机IP
		host = address.getHostAddress();
	}

	
	public void run() {
		PrintWriter pw = null;
	try {
		if (host.equals("127.0.0.1")) {
			host = "客户端01";
			sendMessage(host+"-上线了");
		}
		/**
		 * 通过Socket创建输出流，用于将
		 * 消息发送给客户端
		 */
		//自动行刷新-字符流
		pw = new PrintWriter(
		  new OutputStreamWriter(
			socket.getOutputStream(),"UTF-8"),true);
		addOut(pw);
		
		//客户端发送过来的信息
		BufferedReader br = new BufferedReader(
			new InputStreamReader(
				socket.getInputStream(), "UTF-8"));
		
		String message = null;
		/*
		 * br.readLine读取客户端发送过来的一行字符串
		 * 时,客户端断开连接时,由于客户端所在系统不同,
		 * 这里readLine方法的执行结果也不相同:
		 * 当windows的客户端断开连接时,readLine方法
		 * 会直接抛出异常
		 * 当linux的客户端断开连接时,readLine方法会
		 * 返回null.
		 * 
		 */
		while ((message = br.readLine()) != null) {
			//服务端控制台显示的消息
			System.out.println(host+"说:"+message);
//			pw.println(host+"说:"+message);
			//转发给所有客户端
			sendMessage(host+"说:"+message);
		}
	} catch (Exception e) {
		throw new RuntimeException("项客户端发送消息失败");
		} finally {
			//客户端与服务端断开连接.
			//客户端下线后从共享集合删除输出流
			removeOut(pw);
			sendMessage(host+"下线了！");
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("关闭现有客户端失败");
		  }
		}
	 }
   }
}