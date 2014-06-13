package teetime.util.list;

public interface ObjectPool<T> {

	T acquire();

	void release(T element);

}
