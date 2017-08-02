package teetime.framework.termination;

/**
 * Represents a termination condition which is used by those producers which were formerly infinite producers.
 *
 * @author Christian Wulf
 *
 * @since 3.0
 */
public abstract class TerminationCondition {

	/**
	 * @return <code>true</code> if the condition is met, <code>false</code> otherwise.
	 */
	public abstract boolean isMet();
}
