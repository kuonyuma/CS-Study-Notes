import java.io.*;

public class Main {


    public static void main(String[] args) {

        try(FileWriter fileWriter = new FileWriter("demo.md")) {
            for (int i = 0; i < 300000000; i++) {
                fileWriter.write(i+"");
                Thread.sleep(9000000    );
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }

        try(FileReader fileReader = new FileReader("demo.md")) {
            char[] chs = new char[3];
            int len;
            len = fileReader.read(chs);
            System.out.println(chs);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }



    static void main6() {
        try{
            FileWriter input = new FileWriter("demo.md",true);
            for (int i = 0; i < 3; i++) {
                input.write("hello word");
                input.write('\n');
            }
            input.close();

        }catch(IOException e){
            e.getMessage();
        }
    }
    static void main5() {
       try(BufferedReader bufferedReader = new BufferedReader(new FileReader("demo.md"))){

           for (int i = 0; i < 3; i++) {
               System.out.println(bufferedReader.readLine());
           }
       }catch(IOException e){
           e.getMessage();
       }
    }
    public static void main3() {

        try(BufferedWriter bufferedWriter = new BufferedWriter(
            new FileWriter("demo.md")
        )){
            bufferedWriter.write("hello word");
            bufferedWriter.newLine();
            bufferedWriter.write("java 真好玩");
            bufferedWriter.newLine();
            bufferedWriter.write("高并发这一块");
            bufferedWriter.write("java 不好玩");

        }catch(IOException e){
            e.getMessage();
        }
    }
    static void main2() {

        Thread thread = new Thread(()->{
            try( FileWriter fileWriter = new FileWriter("demo.md",true)){
                Long begin = System.currentTimeMillis();
                for (int i = 0; i < 100000000; i++) {
                    fileWriter.write(i+"");
                }
                Long end = System.currentTimeMillis();
                System.out.println(end - begin);


            }catch(IOException e){
                e.getMessage();
            }
        });

        try(BufferedWriter bufferedWriter =
                new BufferedWriter( new FileWriter("demo.md",true))){
            Long begin = System.currentTimeMillis();
            thread.start();
            for (int i = 0; i < 100000000; i++) {
                bufferedWriter.write(i+"");
            }
            Long end = System.currentTimeMillis();
            System.out.println(end - begin);
            thread.join();
        }catch(IOException e){
            e.getMessage();
        }catch (InterruptedException e){
            e.getMessage();
        }

    }


    public static void main1(String[] args) {
        try(FileWriter fileWriter = new FileWriter("demo.md")){
//            fileWriter.write("hello word",1,2);
//            fileWriter.write("今天天气不错");
//            Long begin = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                fileWriter.write(i);
            }
            fileWriter.flush();
            FileReader fileReader = new FileReader("demo.md");

            char[] chars = new char[1024];
            System.out.println(fileReader.read(chars));



            //            Long end = System.currentTimeMillis();
//            System.out.println("文件操作："+(end - begin));
//            int[] array = new int[100000000];
//            for (int i = 0; i < 100000000; i++) {
//                array[i] = i;
//            }
//            Long end2 = System.currentTimeMillis();
//            System.out.println(end2 -end);
//            for (int i = 0; i < 100000000; i++) {
//                System.out.println("hello word");
//            }
//            Long end3 = System.currentTimeMillis();
//            System.out.println(end3 - end2);

        }catch (IOException e){
            e.getMessage();
        }
        System.out.println("test_done");
    }
}
