package demo1;

public class KeyBoard implements USB{
        public void input(){
            System.out.println("键盘输入");
        }
        @Override
        public void openDevice(){
            System.out.println("打开键盘");
        }
        @Override
        public void closeDevice(){
            System.out.println("关闭键盘");
        }
}
