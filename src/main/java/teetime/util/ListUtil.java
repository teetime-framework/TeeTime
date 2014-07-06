package teetime.util;

import java.util.Collection;
import java.util.List;

public class ListUtil {

	private ListUtil() {
		// utility class
	}

	public static <T> List<T> merge(final List<List<T>> listOfLists) {
		List<T> resultList = listOfLists.get(0);
		for (int i = 1; i < listOfLists.size(); i++) {
			Collection<? extends T> timestampObjectList = listOfLists.get(i);
			resultList.addAll(timestampObjectList);
		}
		return resultList;
	}

	public static <T> List<T> removeFirstHalfElements(final List<T> list) {
		if (list.size() < 2) {
			return list;
		}
		return list.subList(list.size() / 2 - 1, list.size());
	}
}
