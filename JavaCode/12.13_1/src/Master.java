import com.kuonyuma.www.Phone;

public class Master {
    public static void main(String[] rags){
        Phone myhone = new Phone("kuonyuma");
        myhone.show();
        //通过接口更换手机屏幕
        myhone.setScreen("E5发光材料");
        myhone.show();
        //myhone.battery = 20;private修饰的变量无法更改
        myhone.playPhone();
        myhone.show();
    }
}
