class MyFirstClass {
	public static void main(String[] s) {
		MySecondClass o = new MySecondClass();
		System.out.println(o.metod());
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				o.setA1(i);
				o.setA2(j);
				System.out.print(o.metod());
				System.out.print(" ");
			}
			System.out.println();
		}
		
	}
}
class MySecondClass {
	private int a1;
	private int a2;
	MySecondClass(){
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