package Demo1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
    public static void main(String[] args) throws IOException {

        //创建一个持续监听的Socket
        DatagramSocket socket = new DatagramSocket(8080);

        byte[] getbase = new byte[1024];

        DatagramPacket packet = new DatagramPacket(getbase, getbase.length);

        while (true) {
            socket.receive(packet);

            //获取包中的信息
            String meesage = new String(packet.getData(),
                    0,
                    packet.getLength());

            System.out.println("收到来自客户端: " + packet.getPort() + "的信息" + meesage);

            //准备回显
            byte[] severResponse = packet.getData();
            int port = packet.getPort();
            InetAddress IP = packet.getAddress();
            DatagramPacket ResponsePacket = new DatagramPacket(severResponse,
                    packet.getLength(),
                    IP,
                    port);

            socket.send(ResponsePacket);
            System.out.println("服务器已经回显..");
        }
    }
}
