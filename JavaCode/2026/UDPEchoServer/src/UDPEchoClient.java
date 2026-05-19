import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPEchoClient {

    public static void main(String[] args) throws IOException {

        //指定服务器的IP
        String serverip = "127.0.0.1";
        InetAddress inetAddress = InetAddress.getByName(serverip);

        Scanner in = new Scanner(System.in);
        DatagramSocket socket = new DatagramSocket();

        while (true) {

            //客户端输入数据
            String buf = in.nextLine();
            byte[] base = buf.getBytes();

            //定义一个包给服务发数据
            DatagramPacket packet = new DatagramPacket(base,
                    base.length,
                    inetAddress,
                    8080);

            //系统自己去找端口
            
            //发送数据
            socket.send(packet);

            //获取服务器的回显数据

            byte[] echobase = new byte[1024];

            DatagramPacket echopacket = new DatagramPacket(echobase,
                    echobase.length);

            socket.receive(echopacket);

            String echomeesage = new String(echopacket.getData(),
                    0,
                    echopacket.getLength());
            System.out.println("收到服务器回显数据"+ echomeesage);
        }
    }
}
