package util;

import soot.toolkits.scalar.Pair;

public class Triples<T,U,C> extends Pair<T, U>{
	C o3;
	public Triples(T t, U u, C c) {
		super(t, u);
		this.o3 = c;
	}
	
	public void setTriple( T no1, U no2, C c ) { o1 = no1; o2 = no2; o3 = c;};
	
	public void setO3( C no3 ) { o3 = no3; }
	public C getO3(){
		return o3;
	}
	
	public String toString() {
        return "Triple "+o1+","+o2+","+o3;
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Triples other = (Triples) obj;
		if (o1 == null) {
			if (other.o1 != null)
				return false;
		} else if (!o1.equals(other.o1))
			return false;
		if (o2 == null) {
			if (other.o2 != null)
				return false;
		} else if (!o2.equals(other.o2))
			return false;
		if (o3 == null) {
			if (other.o3 != null)
				return false;
		} else if (!o3.equals(other.o3))
			return false;
		return true;
		
	}
}
