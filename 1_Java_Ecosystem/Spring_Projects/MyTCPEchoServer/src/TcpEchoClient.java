import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * TCP 回显客户端 (Echo Client)
 * 该客户端连接到指定的 TCP 服务器，读取用户控制台输入并发送，接收服务器的回显并输出。
 */
public class TcpEchoClient {
    private static final String SERVER_HOST = "127.0.0.1"; // 服务器主机地址
    private static final int SERVER_PORT = 18888;          // 服务器端口号

    public static void main(String[] args) {
        System.out.println("[客户端] 正在连接到服务器 " + SERVER_HOST + ":" + SERVER_PORT + "...");

        // 使用 try-with-resources 自动关闭客户端套接字与输入输出流
        try (
            // 建立与服务器的 TCP 连接
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            // 获取输入流，读取服务器返回的数据，显式指定 UTF-8 编码
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            // 获取输出流，向服务器发送数据，设置 autoFlush 为 true
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);
            // 用于从控制台读取用户输入
            Scanner consoleScanner = new Scanner(System.in)
        ) {
            System.out.println("[客户端] 已成功连接到服务器！");
            System.out.println("[客户端] 请输入你要发送的内容（输入 'exit' 或 'quit' 退出）：");

            while (true) {
                System.out.print("> ");
                if (!consoleScanner.hasNextLine()) {
                    break;
                }
                String userInput = consoleScanner.nextLine();

                // 将输入发送给服务器
                serverWriter.println(userInput);

                // 读取服务器传回的回显内容
                String response = serverReader.readLine();
                if (response == null) {
                    System.out.println("[客户端] 服务器连接已断开。");
                    break;
                }
                System.out.println("[客户端] 收到回显: " + response);

                // 判断是否输入了退出命令
                if ("exit".equalsIgnoreCase(userInput.trim()) || "quit".equalsIgnoreCase(userInput.trim())) {
                    System.out.println("[客户端] 正在退出...");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("[客户端] 发生网络异常: " + e.getMessage());
        }
        System.out.println("[客户端] 已退出关闭。");
    }
}
