
package tool.Analy.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtil {
	public static <E> List<E> toList(E t){
		List<E> list = new ArrayList<E>();
		list.add(t);
		return list;
	}
}
