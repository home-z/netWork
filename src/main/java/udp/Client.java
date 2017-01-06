package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * UDP通讯的客户端
 * @author Administrator
 *
 */
public class Client {
	public static void main(String[] args) {
		try {
			/*
			 * UDP通讯大致过程
			 * 1:创建DatagramSocket
			 * 2:准备要发送的数据
			 * 3:准备发送的目标地址信息(远端
			 *   计算机地址信息)
			 * 4:打包
			 *   创建DatagramPacket并且将
			 *   数据与地址信息设置进去
			 * 5:通过DatagramSocket将包发送
			 *   至远端
			 * 若需要再次发送数据,重复2-5      
			 * 
			 */
			//1
			@SuppressWarnings("resource")
			DatagramSocket socket = new DatagramSocket();
			
			//2
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String message = null;
			message = scanner.nextLine();
			byte[] data = message.getBytes("UTF-8");
			
			//3
			InetAddress address = InetAddress.getByName("localhost");
			int port = 8707;
			
			//4
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			//5
			socket.send(packet);
			
			/**
			 * 接收服务端发送回来的消息
			 */
			data = new byte[100];
			packet = new DatagramPacket(data, data.length);
			
			/*
			 * 3
			 * 该方法是一个阻塞方法,直到远程计算机
			 * 发送数据过来为止,该方法才会解除阻塞
			 * 并将数据等信息设置到接收用的包中
			 */
			socket.receive(packet);
			/*
			 * 4 下面的方法可以获取包中的数组
			 *   这个数组就是上面定义的data
			 */
			message = new String(data, 0, packet.getLength(),"UTF-8");
			System.out.println("服务端说:"+message);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("客户端异常");
		}
		
		
		
	}
}
