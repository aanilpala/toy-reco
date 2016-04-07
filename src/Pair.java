
public class Pair<A,B> extends Object {

	public A a;
	public B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if(!(obj instanceof Pair)) return false;
//		Pair pair_obj = (Pair) obj;
//		if(a == pair_obj.a && b == pair_obj.b) return true;
//		else return false;
//	}
//	
//	@Override
//	public int hashCode() {
//		return new Long(a).hashCode() * 31 + new Long(b).hashCode();
//	}
	 
	 

	

}
