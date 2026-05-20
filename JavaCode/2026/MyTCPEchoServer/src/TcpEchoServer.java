import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TCP 回显服务器 (Echo Server)
 * 这是一个基于线程池的多线程 TCP 服务器，监听指定端口并将接收到的任何文本原样返回给客户端。
 */
public class TcpEchoServer {
    private static final int PORT = 18888; // 监听的端口号

    public static void main(String[] args) {
        // 创建一个可缓存的线程池，用于并发处理多个客户端连接
        ExecutorService threadPool = Executors.newCachedThreadPool();

        System.out.println("[服务器] 正在启动...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[服务器] 启动成功，正在监听端口 " + PORT + "，等待客户端连接...");

            // 循环监听客户端请求
            while (true) {
                // accept() 会阻塞当前线程，直到有客户端连接进来
                Socket clientSocket = serverSocket.accept();
                String clientIp = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();
                System.out.println("[服务器] 客户端连接成功！来自 IP: " + clientIp + ", 端口: " + clientPort);

                // 将客户端连接的处理逻辑提交给线程池异步执行，使主线程能迅速回到 accept() 阻塞等待下一个连接
                threadPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("[服务器] 运行中发生异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭线程池
            threadPool.shutdown();
            System.out.println("[服务器] 已关闭。");
        }
    }

    /**
     * 处理与单个客户端的通信逻辑
     * @param clientSocket 客户端连接套接字
     */
    private static void handleClient(Socket clientSocket) {
        String clientInfo = "[" + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "]";

        // 使用 try-with-resources 自动关闭套接字和输入输出流
        try (
            // 获取输入流，并包装为 BufferedReader 按行读取数据，显式指定 UTF-8 编码
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            // 获取输出流，并包装为 PrintWriter 用于按行发送数据，设置 autoFlush 为 true
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;
            // 循环读取客户端发送的一行数据
            while ((message = reader.readLine()) != null) {
                System.out.println("[服务器] 收到来自 " + clientInfo + " 的数据: " + message);
                
                // 将接收到的消息原样回显给客户端
                writer.println(message);
                
                // 如果客户端发送的是退出指令，结束与该客户端的会话
                if ("exit".equalsIgnoreCase(message.trim()) || "quit".equalsIgnoreCase(message.trim())) {
                    System.out.println("[服务器] 客户端 " + clientInfo + " 主动请求断开连接。");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[服务器] 与客户端 " + clientInfo + " 通信时发生异常: " + e.getMessage());
        } finally {
            try {
                // 确保 Socket 被关闭
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
                System.out.println("[服务器] 客户端 " + clientInfo + " 已断开连接。");
            } catch (IOException e) {
                System.err.println("[服务器] 关闭客户端套接字时发生错误: " + e.getMessage());
            }
        }
    }
}
