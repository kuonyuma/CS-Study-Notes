package demo2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) {

        //准备好IP与端口
        InetAddress address = null;
        Scanner in = null;
        PrintWriter writer = null;
        BufferedReader bufferedReader = null;
        Socket socket = null;
        try {
            address = InetAddress.getByName(IP);

            socket = new Socket(address, PORT);

            writer = new PrintWriter(socket.getOutputStream(),true);
            bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            //输入
            in = new Scanner(System.in);

            while(in.hasNext()){
                String message = in.nextLine();
                writer.println(message);

                message = bufferedReader.readLine();
                System.out.println(message);
            }
            // 1. 发送 FIN 包（半关闭）
            socket.shutdownOutput();
            // 2. 尝试读取服务端在收到 FIN 后发过来的后续数据
            String line;
            while((line = bufferedReader.readLine()) != null){
                System.out.println("客户端收到后续数据：" + line);

            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(in != null){
                in.close();
                System.out.println("客户端已经关闭scanner");
            }

            if(writer != null){
                writer.close();
                System.out.println("客户端已关闭:writer");
            }

            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                    System.out.println("客户端已关闭:bufferedReader");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}