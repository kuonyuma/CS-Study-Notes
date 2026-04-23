import java.io.IOException;

public class test01 {

    public static void main (String[] args)throws IOException {
        MyRead in = new MyRead();
        //滑动窗口
        int n = in.nextInt();
        int x = in.nextInt();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = in.nextInt();
        }
        int left = 0;
        int right = 0;
        int tmp = 0;
        int l = 0;
        int r = 0;
        int len = Integer.MAX_VALUE;
        while (right < n) {
            tmp += array[right];
            while (left <= right && tmp >= x) {
                if (len > right - left + 1) {
                    len = right - left + 1;
                    r = right;
                    l = left;
                }
                tmp -= array[left];
                left++;
            }
            right++;
        }
        System.out.println(l + " " + r);
    }

}

