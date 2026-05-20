package MyTCPEcho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {

        Socket clientSocket = new Socket("127.0.0.1",8080);

        Scanner in = new Scanner(System.in);
        //接收回显数据
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        //发送
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(),true);

        while(in.hasNext()){
            String buf = in.nextLine();
            printWriter.println(buf);

            String response = bufferedReader.readLine();
            System.out.println(response);

        }

    }
}
