package rl;

interface Valuable {
    // use richest built-in numeric type
    double value();
    Valuable value(double v);
}

public class Tuple<X extends Number, Y  extends Number> { 
	public final X x; 
	public final Y y; 
	public double x_block;
	public double y_block;
	
	public Tuple(X x, Y y) { 
		this.x = x; 
		this.y = y; 
	}

	public Tuple(X x, Y y, X blocksize_x, Y blocksize_y) { 
		this.x = x; 
		this.y = y; 
		this.x_block = x.doubleValue() * blocksize_x.doubleValue();
		this.y_block = y.doubleValue() * blocksize_y.doubleValue();		
	}
	
	@Override
	public int hashCode() { 
		long l = x.hashCode() * 2654435761L;
		return (int)l + (int)(l>>32) + y.hashCode();
		//return x.hashCode() ^ y.hashCode(); }
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple)) return false;
	    Tuple tu = (Tuple) o;
	    return this.x.equals(tu.x) &&
	           this.y.equals(tu.y);
	}
	
}

