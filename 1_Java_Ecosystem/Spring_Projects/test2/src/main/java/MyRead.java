import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MyRead{
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    StringTokenizer st = new StringTokenizer("");

    public String next() throws IOException {
        while(!st.hasMoreTokens()){
            String line = br.readLine();
            if(line == null)return null;
            st = new StringTokenizer(line);
        }
        return st.nextToken();
    }

    public String nextLine() throws IOException{
        return br.readLine();
    }

    public int nextInt() throws IOException{
        return Integer.parseInt(next());
    }
    public double nextDouble() throws IOException{
        return Double.parseDouble(next());
    }
}

