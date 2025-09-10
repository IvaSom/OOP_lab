import myfirstpackage.*;

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
