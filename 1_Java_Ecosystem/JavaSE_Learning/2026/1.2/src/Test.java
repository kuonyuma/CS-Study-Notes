import java.util.List;

public class Test {
    public static void main(String[] args) {
        CardDemo test =new CardDemo();
        List<Card> ret = test.buyCard();
        test.Shuffle(ret);
        List<List<Card>> players =
                test.getCards(ret);

        System.out.println(players);

    }
}
