package teetime.util.list;

import java.util.ArrayList;
import java.util.List;

public class ListContainerPool<T> implements ObjectPool<ListContainer<T>> {

	private final List<ListContainer<T>> pool = new ArrayList<ListContainer<T>>();

	public ListContainerPool(int initialPoolSize) {
		while (initialPoolSize-- > 0) {
			this.pool.add(this.createNew());
		}
	}

	@Override
	public ListContainer<T> get() {
		ListContainer<T> obj;
		if (this.pool.size() > 0) {
			obj = this.pool.remove(this.pool.size() - 1);
		} else {
			obj = this.createNew();
			this.pool.add(obj);
		}
		return obj;
	}

	private ListContainer<T> createNew() {
		return new ListContainer<T>();
	}

	@Override
	public void release(final ListContainer<T> obj) {
		this.pool.add(obj);
	}

}
