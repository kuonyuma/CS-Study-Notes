public class Card {
    //扑克牌
    private int num;
    private String type;

    public Card(int num,String type){
        this.num = num;
        this.type =type;

    }

    public int getNum(){
        return this.num;
    }
    public void setNum(int num){
        this.num = num;
    }

    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
    @Override
    public String toString() {
        return type + num; // 例如输出：红心1
    }
}
