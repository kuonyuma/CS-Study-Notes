package demo2;

public class TestClass {
    private int a;
    private String name;

    public TestClass(int a, String name){
        this.a = a;
        this.name = name;
    }
    public class Heart{
        public int a ;
        public Heart(int a){
            this.a = a;
        }
        public void print1(){
            System.out.println(TestClass.this.a);
        }

        public void print(){
            System.out.println(TestClass.this.a + " " + name);
        }

    }


}
