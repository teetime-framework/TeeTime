package teetime.framework;

/**
 * All context services must inherit from this abstract class.
 *
 * @author Nelson Tavares de Sousa
 *
 * @param <T>
 *            service type
 *
 * @since 2.0
 */
abstract class AbstractService<T> {

	abstract void onInitialize();

	abstract void onStart();

	abstract void onExecute();

	abstract void onTerminate();

	abstract void onFinish();

	// abstract void merge(T source);

}
