package MyTCPEcho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException {

        //线程池，线程总数为最大值
        ExecutorService executorService = Executors.newCachedThreadPool();
        //持续监听固定端口
        ServerSocket serverSocket = new ServerSocket(8080);
        //获得连接
        while(true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("客户端已连接，地址: " + clientSocket.getInetAddress() + ", 端口: " + clientSocket.getPort());

            executorService.execute(() -> {
                try {
                    function(clientSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    //执行具体逻辑
    private static void function(Socket clientSocket) throws IOException {
        InetAddress address = clientSocket.getInetAddress();
        int port = clientSocket.getPort();
        //接收来自客户端的信息
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        //发送信息给客户端
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(),true);

        String meessage;

        while((meessage = bufferedReader.readLine()) != null){
            System.out.println("服务器收到来自客户端:" +
                    "address:" +
                    address +
                    "prot:" +
                    port+
                    "+meessage:"+
                    meessage);
            //回显
            printWriter.println(meessage);
        }



    }
}
