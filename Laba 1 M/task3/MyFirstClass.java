public class MyFirstClass {
    public static void main(String[] s) {
            MySecondClass temp = new MySecondClass(5, 6);
            System.out.println(temp.XOR());
    }
}

class MySecondClass {
    private int valueOne = 0;
    private int valueTwo = 0; 

    public MySecondClass(int val1, int val2) {
        this.valueOne = val1;
        this.valueTwo = val2;
    }
    
    public int XOR(){
        return valueOne ^ valueTwo;
    }
}
