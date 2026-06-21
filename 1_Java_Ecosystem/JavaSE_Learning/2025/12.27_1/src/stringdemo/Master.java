package stringdemo;

public class Master {
        public static void main(String[] args) {
            System.out.println("程序开始...");
            int a = 10;
            int b = 0; // 故意设置为0，制造故障
            try {
                // 监控区域：JVM 执行到这里发现除以0，会立刻抛出一个 ArithmeticException 对象
                int result = a / b;
                System.out.println("计算结果是：" + result); // 这行代码不会被执行
            } catch (ArithmeticException e) {
                // 捕获区域：只有出现算术异常，才会进到这里
                System.out.println("出大事了！不能除以 0！");
                System.out.println("错误信息：" + e.getMessage());// 获取简单的错误描述
                e.printStackTrace();
            }

            System.out.println("程序结束，正常退出。");
            // 如果没有 try-catch，程序在上面就崩了，这行字永远打印不出来。
            // 有了 try-catch，程序“带伤”也能坚持跑完。
        }


    public static void main11(String[] args) {
        int[] array = {1,2,4,5,6};
        try{
            int a = array[6];
        } catch (RuntimeException e) {
            System.out.println("数组越界");
            e.printStackTrace();
        }
        System.out.println("解决异常继续执行程序");
    }

    public static int chek1(String s){
        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            count[ch - 'a']++;
        }
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if(count[ch - 'a'] == 1){
                return i;
            }
        }
        return -1;

    }
    public static int find(String s){
        return s.lastIndexOf(' ');
    }

     public static boolean chek2(String s){

         for (int i = 0; i < s.length() / 2; i++) {
             char ch = s.charAt(s.length()-i-1);
             char ch1 =s.charAt(i);
             if(ch1!=ch){

                 return false;
             }
         }
        return true;
    }
    public static void main10(String[] args) {
        String s = "abcd";
    }
    public static void main9(String[] args) {
        String s = "hello word kuonyuma liuxie";
        int len = s.length() -1;
        int log = len-  s.lastIndexOf(' ');
        System.out.println(log);

       String s1 = s.substring(s.lastIndexOf(' '));


    }
    public static void main8(String[] args) {
        String s = "leelcode";
        System.out.println( chek1(s));



    }
    public static void main7(String[] args) {
        String s = null;
        System.out.println(s.length());
    }

    //split
    public static void main6(String[] args) {
        String s = "java Programming";
        String[] s1 =  s.split(" ");
        for(String e :s1){
            System.out.print(e+"    ");
        }

        String s2 = "192.168.74.200";
        String[] s3 = s2.split("\\.");
        for(String e :s3){
            System.out.print(e+" ");
        }
    }
    //indexOf
    public static void main5(String[] args) {
        String s = "java Programming";

        int index = s.indexOf('P');
        System.out.println(index);

        int index1 = s.indexOf('a',2);
        System.out.println(index1);

        int index2 = s.indexOf("Pro");
        System.out.println(index2);

        int index3 = s.indexOf("Pro",6);
        System.out.println(index3);

    }

    //charAt
    public static void main4(String[] args) {
        String s = "java Programming";
        char tmp = s.charAt(s.length()-1);
        System.out.println(tmp);
        for (int i = 0; i < s.length(); i++) {
            System.out.print(" "+ s.charAt(i));
        }


    }
    //equals
    public static void main3(String[] args) {

        String s = "abcd";
        String s1 = "abcd";
        System.out.println(s.equals(s1));

        String s2 = "abcde";
        String s3 = "abcdE";
        System.out.println(s2.equals(s3));

        if("abcd".equals(s1)){
            System.out.println("ture!!");
        }


    }
    //compareToIgnoreCase的学习
    public static void main2(String[] args) {
        String s = "abcd";
        String s1 = "Abcd";
        System.out.println(s.compareToIgnoreCase(s1));
        System.out.println(s.compareTo(s1));//a-A == 32;

        String s2 = "Abcde";
        System.out.println(s.compareToIgnoreCase(s2));

    }
    //compareTo的学习
    public static void main1(String[] args) {
        String str = "abc";
        String str1 = "abd";
        System.out.println(str.compareTo(str1));

        String str2 = "abC";
        System.out.println(str.compareTo(str2));

        String str3 = "abcdef";
        System.out.println(str.compareTo(str3));

    }
}
