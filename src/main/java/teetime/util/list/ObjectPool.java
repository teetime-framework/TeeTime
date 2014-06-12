package teetime.util.list;

public interface ObjectPool<T> {

	T get();

	void release(T obj);

}
