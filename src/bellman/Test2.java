package bellman;

public class Test2 extends Test1{
	public Test2() {
		super();
	}
	
	public void ab() {
		a = a + b + c;
	}
	
	public static void main(String[] a) {
		Test2 number = new Test2();
		System.out.println(number.a);
		number.bc();
		System.out.println(number.a);
	}
}
