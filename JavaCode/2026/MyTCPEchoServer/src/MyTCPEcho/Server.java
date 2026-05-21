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

        // 线程池，线程总数为最大值
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 持续监听固定端口
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("【服务器】启动，等待连接...");
        // 获得连接
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("客户端已连接，地址: " + clientSocket.getInetAddress() + ", 端口: " + clientSocket.getPort());

            executorService.execute(() -> {
                try {
                    function(clientSocket);
                } catch (IOException e) {
                    System.err.println("处理客户端时发生错误: " + e.getMessage());
                }
            });
        }
    }

    // 执行具体逻辑
    private static void function(Socket clientSocket) throws IOException {
        InetAddress address = clientSocket.getInetAddress();
        int port = clientSocket.getPort();
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;

        try {
            // 接收来自客户端的信息
            bufferedReader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            // 发送信息给客户端
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            String meessage;
            while ((meessage = bufferedReader.readLine()) != null) {
                System.out.println("服务器收到来自客户端:" +
                        "address:" +
                        address +
                        "prot:" +
                        port +
                        "+meessage:" +
                        meessage);
                // 回显
                printWriter.println(meessage);
            }

            // 当客户端调用 shutdownOutput 发送了 FIN 包后，readLine() 返回 null，循环正常退出
            System.out.println("【服务器】检测到客户端发送了 FIN（输入流结束）。准备向客户端单向发送 10 份后续数据...");

            // 模拟服务器继续单向传递 10 份数据，每隔一秒发送一份
            for (int i = 1; i <= 10; i++) {
                System.out.println("【服务器】发送第 " + i + " 份数据...");
                printWriter.println("已经传第：" + i + "份数据和");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("【服务器】连接异常断开: " + e.getMessage());
        } finally {
            // 安全关闭资源
            if (printWriter != null) {
                printWriter.close();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("【服务器】客户端连接彻底关闭。");
        }
    }
}
