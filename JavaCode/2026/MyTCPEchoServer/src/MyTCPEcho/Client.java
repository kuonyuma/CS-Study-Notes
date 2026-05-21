package MyTCPEcho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {

        Socket clientSocket = new Socket("127.0.0.1", 8080);

        Scanner in = new Scanner(System.in);
        // 接收回显数据
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        // 发送
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

        System.out.println("连接成功！请输入内容（按 Ctrl+D 触发半关闭并发 FIN 包）：");
        while (in.hasNext()) {
            String buf = in.nextLine();
            printWriter.println(buf);

            String response = bufferedReader.readLine();
            System.out.println(response);
        }

        // 1. 发送 FIN 信号 (半关闭)
        System.out.println("\n【客户端】检测到输入流结束，正在发送 FIN 包半关闭...");
        clientSocket.shutdownOutput();

        // 2. 接收服务端发送的后续数据
        System.out.println("【客户端】开始读取服务端的后续遗言数据...");
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println("收到后续数据: " + line);
        }

        // 3. 关闭资源
        in.close();
        printWriter.close();
        bufferedReader.close();
        clientSocket.close();
        System.out.println("【客户端】连接已完全关闭。");
    }
}
