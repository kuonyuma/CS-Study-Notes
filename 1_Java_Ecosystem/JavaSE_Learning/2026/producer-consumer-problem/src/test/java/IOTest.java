import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IOTest {
    public static void main(String[] args) {
        // 先测试裸用 FileWriter
        testFileWriter();

        System.out.println("-----------------");

        // 等上面彻底跑完，释放了硬盘，再单独测试 BufferedWriter
        testBufferedWriter();
    }

    private static void testFileWriter() {
        // 为了不互相干扰，写到独立的文件 test1.md
        try (FileWriter fileWriter = new FileWriter("test1.md")) {
            long begin = System.currentTimeMillis();
            for (int i = 0; i < 100000000; i++) {
                fileWriter.write(i); // 去掉 i + "" 的干扰，纯测 IO
            }
            long end = System.currentTimeMillis();
            System.out.println("裸用 FileWriter 耗时: " + (end - begin));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testBufferedWriter() {
        // 写到独立的文件 test2.md
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("test2.md"))) {
            long begin = System.currentTimeMillis();
            for (int i = 0; i < 100000000; i++) {
                bufferedWriter.write(i); // 去掉 i + "" 的干扰，纯测 IO
            }
            long end = System.currentTimeMillis();
            System.out.println("包装 BufferedWriter 耗时: " + (end - begin));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}