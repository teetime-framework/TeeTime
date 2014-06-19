package teetime.variant.explicitScheduling.stage;

import java.util.Collection;

import teetime.variant.explicitScheduling.stage.predicate.IsSuperTypePredicate;
import teetime.variant.explicitScheduling.stage.predicate.PredicateFilter;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class SuperTypeFilter<T> extends PredicateFilter<T> {

	/**
	 * @param acceptedClasses
	 * @since 1.10
	 */
	public SuperTypeFilter(final Collection<Class<?>> acceptedClasses) {
		super(new IsSuperTypePredicate<T>(acceptedClasses));
	}

}
