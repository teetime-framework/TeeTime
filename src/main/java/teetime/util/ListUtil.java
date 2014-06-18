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
}
