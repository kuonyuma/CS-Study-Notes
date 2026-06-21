import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardDemo {
    private static final String[] suits = {"红心", "桃花", "梅花", "方片"};


    //买扑克
    public List<Card> buyCard() {
        // 分为四种花色（红桃、方块、梅花、黑桃），每种花色各13张（A, 2-10, J, Q, K）
        // 52张牌。一共有个13数，一张有四种类型
        List<Card> cardList = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            for (int j = 0; j < 4; j++) {
                Card card = new Card(i, suits[j]);
                cardList.add(card);
            }
        }
        return cardList;
    }

    //洗扑克牌
    public void Shuffle(List<Card> cardList) {
        Random random = new Random();
        for (int i = 51; i > 0; i--) {
            int index = random.nextInt(i);
            Card tmp = cardList.get(index);
            cardList.set(index, cardList.get(i));
            cardList.set(i, tmp);
        }
    }

    //发扑克,假设三个人，一人五张牌，一张一张的轮流发
    public List<List<Card>> getCards(List<Card> cardList) {
        List<List<Card>> players = new ArrayList<>();
        List<Card> list1 = new ArrayList<>();
        players.add(list1);
        List<Card> list2 = new ArrayList<>();
        players.add(list2);
        List<Card> list3 = new ArrayList<>();
        players.add(list3);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                players.get(j).add(cardList.get(0));
                cardList.remove(0);
            }
        }
        return players;
    }
}

