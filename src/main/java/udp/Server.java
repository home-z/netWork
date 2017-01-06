package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP通讯的服务端
 * @author Administrator
 *
 */
public class Server {
	public static void main(String[] args) {
		try {
			/*
			 * 接收数据的大致步骤:
			 * 1:创建DatagramSocket
			 * 2:创建一个接收数据用的包
			 *   包创建时要设置一个字节数组,
			 *   接收的数据就存放在这个数组中.
			 *   所以这个数组应当足够大.
			 * 3:通过DatagramSocket接收数据
			 *   当接收数据后,接收包会有一些变化:
			 *   1:包中有远端计算机发送过来的数据
			 *   2:包也记录了数据从哪里来(远端计算
			 *     机的地址信息)
			 * 4:获取包中的数据(字节数组中保存)
			 */
			//TCP协议与UDP协议的端口是不冲突的
			//1
			@SuppressWarnings("resource")
			DatagramSocket socket = new DatagramSocket(8707);
			
			//2
			byte[] data = new byte[101];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
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
//			packet.getData();
			/*
			 * packet.getLength方法可以获取本次
			 * 接收的数据的长度.
			 */
			String message = new 
					String(data, 0, packet.getLength(),"UTF-8");
			System.out.println("客户端说："+message);
			
			/**
			 * 服务端回复客户端
			 */
			message = "已接收客户端回复";
			data = message.getBytes("UTF-8");
			
			// 3 通过接收包得到远程计算机地址信息
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			
			//4
			 packet = new 
				DatagramPacket(data, data.length, address, port);
			 //5
			 socket.send(packet);
		} catch (Exception e) {
			System.out.println("服务端异常");
		}
	}
}
