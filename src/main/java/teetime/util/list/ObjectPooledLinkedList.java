package teetime.util.list;

public class ObjectPooledLinkedList<T> {

	private final ObjectPool<ListContainer<T>> objectPool = new ListContainerPool<T>(10);

	private static final ListContainer<?> BOTTOM = new ListContainer<Object>();

	private ListContainer<T> top;

	private int size;

	@SuppressWarnings("unchecked")
	public ObjectPooledLinkedList() {
		this.top = (ListContainer<T>) BOTTOM;
	}

	/**
	 * 
	 * @return <code>null</code> if the list is empty.
	 */
	public T pop() {
		if (this.top == BOTTOM) {
			return null;
		}
		T value = this.top.value;
		this.top = this.top.previous;
		this.size--;
		return value;
	}

	public void push(final T element) {
		ListContainer<T> listContainer = this.objectPool.get();
		listContainer.previous = this.top;
		listContainer.value = element;
		this.top = listContainer;
		this.size++;
	}

	public T read() {
		if (this.top == BOTTOM) {
			return null;
		}
		return this.top.value;
	}

	public int size() {
		return this.size;
	}
}
