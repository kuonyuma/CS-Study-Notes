public class Machine {
    private int count;
    private final int money;
    public Machine(){
        this.count = 5;
        this.money = 10;
    }

    public int getMoney() {
        return money;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void buy(int money,int count)throws MyException{
        if(money<=4){
            throw new MyException("金额不足");
        }if(count<1){
           throw  new MyException("要买的数量必须大于0");
        }if(this.count > 0){
            this.count -= count;
            System.out.println("购买成功");
        }else {
            System.out.println("库存已空");
        }
    }

}
