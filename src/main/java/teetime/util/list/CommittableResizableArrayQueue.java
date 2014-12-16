package teetime.util.list;

public class CommittableResizableArrayQueue<T> implements CommittableQueue<T> {

	// private final int MIN_CAPACITY;

	private final ArrayPool<T> arrayPool;
	private T[] elements;

	private int lastFreeIndex, lastFreeIndexUncommitted;

	@SuppressWarnings("unchecked")
	public CommittableResizableArrayQueue(final Object emptyObject, final int initialCapacity) {
		super();
		this.arrayPool = new ArrayPool<T>();
		// this.MIN_CAPACITY = initialCapacity + 1;
		this.elements = this.arrayPool.acquire(initialCapacity + 1);

		this.elements[0] = (T) emptyObject; // optimization: avoids the use of an index out-of-bounds check
		this.clear(); // NOPMD
	}

	@Override
	public final T get(final int index) {
		T element = this.elements[index + 1];
		return element;
	}

	@Override
	public void addToTailUncommitted(final T element) {
		if (this.lastFreeIndexUncommitted == this.capacity()) {
			this.grow();
		}
		this.put(this.lastFreeIndexUncommitted++, element);
	}

	@Override
	public T removeFromHeadUncommitted() {
		// if (this.capacity() > this.MIN_CAPACITY && this.lastFreeIndexUncommitted < this.capacity() / 2) { // TODO uncomment
		// this.shrink();
		// }
		T element = this.get(--this.lastFreeIndexUncommitted);
		return element;
	}

	@Override
	public void commit() {
		// TODO set elements to null to help the gc
		this.lastFreeIndex = this.lastFreeIndexUncommitted;
	}

	@Override
	public void rollback() {
		this.lastFreeIndexUncommitted = this.lastFreeIndex;
	}

	@Override
	public int size() {
		return this.lastFreeIndex;
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public void clear() {
		this.lastFreeIndex = this.lastFreeIndexUncommitted = 0;
	}

	@Override
	public T getTail() {
		T element = this.get(this.lastFreeIndex - 1);
		return element;
	}

	private void grow() {
		T[] newElements = this.arrayPool.acquire(this.elements.length * 2);
		// System.out.println("grow: " + this.lastFreeIndexUncommitted);
		this.replaceCurrentArrayBy(newElements);
	}

	// private void shrink() {
	// T[] newElements = this.arrayPool.acquire(this.elements.length / 2);
	// // System.out.println("shrink: " + this.lastFreeIndexUncommitted);
	// this.replaceCurrentArrayBy(newElements);
	// }

	private final void replaceCurrentArrayBy(final T[] newElements) {
		this.copyArray(this.elements, newElements);
		this.arrayPool.release(this.elements);
		this.elements = newElements;
	}

	private final void copyArray(final T[] elements, final T[] newElements) {
		// for (int i = 0; i < this.lastFreeIndexUncommitted; i++) {
		// newElements[i] = elements[i];
		// }
		System.arraycopy(elements, 0, newElements, 0, this.lastFreeIndexUncommitted + 1);
	}

	private final void put(final int index, final T element) {
		this.elements[index + 1] = element;
	}

	private final int capacity() {
		return this.elements.length - 1;
	}

	@Override
	public T removeFromHead() {
		T element = this.removeFromHeadUncommitted();
		this.commit();
		return element;
	}
}
