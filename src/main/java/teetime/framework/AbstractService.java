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
public abstract class AbstractService<T> {

	abstract void initialize();

	abstract void start();

	abstract void terminate();

	abstract void finish();

	abstract void merge(T source);

}
