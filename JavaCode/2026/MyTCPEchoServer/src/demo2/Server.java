package demo2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {

        try {
            //监听
            ServerSocket serverSocket = new ServerSocket(8080);
            ExecutorService executorService = Executors.newCachedThreadPool();

            while(true){
                Socket socket = serverSocket.accept();

                executorService.execute(()->action(socket));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //具体处理客户端的请求
    private static void action(Socket socket) {

            BufferedReader bufferedReader = null;
            PrintWriter writer = null;

            try {
                //接收
                bufferedReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                //发送
                writer = new PrintWriter(socket.getOutputStream(),true);

                //打印数据并且回显
                String meesage;
                while((meesage = bufferedReader.readLine()) != null){

                    if("exit".equals(meesage)){
                        System.out.println("客户端和服务器断开");
                        break;
                    }
                    System.out.println("服务器已收到数据："+ meesage);
                    writer.println(meesage);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally{

                //模拟服务器还得客户端传递数据
                int i = 0;
                while(i < 10){
                    i++;
                    writer.println("已经传第："+i+"份数据和");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(bufferedReader != null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(writer != null){
                    writer.close();
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
