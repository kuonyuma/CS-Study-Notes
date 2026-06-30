package Test2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandExecutor {

    private boolean isFind = false;

    public void execute(Object service, String cmdName) throws InvocationTargetException, IllegalAccessException {

        //获取class
        Class<GameService> clazz = GameService.class;
        //获取到所有方法
        Method[] methods = clazz.getMethods();

        for(Method e : methods){

            if(e.isAnnotationPresent(Command.class)){
                Command command = e.getAnnotation(Command.class);

                if(command.value().equals(cmdName)){
                    e.invoke(service);
                    isFind = true;
                    break;
                }
            }
        }
        // 如果遍历完所有方法都没找到对应的指令，给个提示
        if (!isFind) {
            System.out.println("⚠️ 未知的指令: [" + cmdName + "]");
        }
    }
}
