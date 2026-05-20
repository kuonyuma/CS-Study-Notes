import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDPEchoServer {

    public static void main(String[] args) {

        try {
            //定义服务器监听的端口

            DatagramSocket socket = new DatagramSocket(8080);

            //创建字节数组用于接受数据
            byte[] base = new byte[1024];


            while (true) {
                //准备接收包

                DatagramPacket packet = new DatagramPacket(base, base.length);

                //接收数据
                socket.receive(packet);

                //数据转为字符串
                String meesage = new String(packet.getData(),
                        0,
                        packet.getLength());
                System.out.println("收到来自客户端的数据" + meesage);
                //获取客户端的IP，端口
                InetAddress inetAddress = packet.getAddress();
                int clientport = packet.getPort();

                //准备返回的数据
                byte[] putmeesage = packet.getData();;

                DatagramPacket EchoPacket = new DatagramPacket(putmeesage,
                        packet.getLength(),
                        inetAddress,
                        clientport);
                //发送回显数据
                socket.send(EchoPacket);
            }

        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }


}
