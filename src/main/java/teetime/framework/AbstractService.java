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

	abstract void merge(T target, T source);

}
