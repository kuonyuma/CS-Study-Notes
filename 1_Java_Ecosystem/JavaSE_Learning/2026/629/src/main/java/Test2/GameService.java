package Test2;

import java.lang.reflect.InvocationTargetException;

public class GameService {

    @Command("start")
    public void doStart(){
        System.out.println("游戏开始...");
    }

    @Command("stop")
    public void doStop(){
        System.out.println("游戏结束...");
    }

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {

        GameService gameService = new GameService();
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.execute(gameService,"stop");
        commandExecutor.execute(gameService,"start");

    }

}
