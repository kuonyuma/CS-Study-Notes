public class Master {
    public static void main(String[] args){
        StringBuilder sb = new StringBuilder("liuxie");
        //append，追加字符串
        sb.append(666);
        System.out.println(sb);
        //insert.中间插入字符串.标签的前面
        sb.insert(3,"中间插入");
        System.out.println(sb);
        //delete,删除字符串
        sb.delete(0,3);//delete。aoe
        System.out.println(sb);
        sb.deleteCharAt(1);//单独删除
        System.out.println(sb);

    }
}
