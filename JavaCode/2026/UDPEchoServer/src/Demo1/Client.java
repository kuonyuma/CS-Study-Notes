package Demo1;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {

        //系统自动获取端口
        DatagramSocket clientSocket = new DatagramSocket();

        //固定服务器的Address
        InetAddress address = InetAddress.getByName("127.0.0.1");
        int port = 8080;
        Scanner in = new Scanner(System.in);
        while(true) {


            byte[] clientIn = in.nextLine().getBytes();

            DatagramPacket packet = new DatagramPacket(clientIn,
                    clientIn.length,
                    address,
                    port);

            //发送数据
            clientSocket.send(packet);
            System.out.println("客户端已经发送了数据");
            //得到回显的数据

            byte[] buf = new byte[1024];

            DatagramPacket ResponsePacket = new DatagramPacket(buf,
                    buf.length);

            clientSocket.receive(ResponsePacket);

            String ResponseMeesage = new String(ResponsePacket.getData(), 0, ResponsePacket.getLength());
            System.out.println("得到服务器的回显数据" + ResponseMeesage);
        }
    }
}
