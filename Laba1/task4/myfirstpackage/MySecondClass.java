package myfirstpackage;

public class MySecondClass {
	private int a1;
	private int a2;
	public MySecondClass(){
		a1=0;
		a2=0;
	}
	//вар 3 Умножение
	public int metod(){
		return a1*a2;
	}
	public int setA1(int temp){
		a1=temp;
		return a1;
	}
	public int setA2(int temp){
		a2=temp;
		return a2;
	}
}