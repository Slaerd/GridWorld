package bellman;

public class Test1 {
	protected int a;
	protected int b;
	protected int c;
	
	public Test1() {
		a = 1;
		b = 2;
		c = 3;
		ab();
	}
	
	public void ab(){
		a = a + b;
	}
	
	public void bc() {
		ab();
		ab();
	}
	
	public static void main(String[] a){
		Test1 number = new Test1();
		System.out.println(number.a);
		number.bc();
		System.out.println(number.a);
	}
}
