package teetime.util.list;

public interface CommittableQueue<T> {

	// basic methods
	T get(int index);

	void addToTailUncommitted(T element);

	T removeFromHeadUncommitted();

	void commit();

	void rollback();

	int size();

	boolean isEmpty();

	void clear();

	// convenient methods
	// T removeFromHeadUncommitted(int count);

	T getTail();

}
