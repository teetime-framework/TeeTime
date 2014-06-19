package teetime.variant.explicitScheduling.framework.core;

public interface IReservablePipe<T> extends IPipe<T> {

	// void reserve(T element);

	void commit();

	void rollback();
}
