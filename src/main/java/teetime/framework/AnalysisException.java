package teetime.framework;

import java.util.Collection;

import teetime.util.Pair;

/**
 * Represents a exception, which is thrown by an analysis, if any problems occured within its execution.
 * A collection of thrown exceptions within the analysis can be retrieved with {@link #getThrownExceptions()}.
 *
 */
public class AnalysisException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 7486086437171884298L;

	private final Collection<Pair<Thread, Throwable>> exceptions;

	public AnalysisException(final Collection<Pair<Thread, Throwable>> exceptions) {
		super("Error(s) while running analysis. Check thrown exceptions.");
		this.exceptions = exceptions;
	}

	/**
	 * Returns all exceptions thrown within the analysis.
	 * These are passed on as pairs of threads and throwables, to indicate a exception's context.
	 *
	 * @return a collection of pairs
	 */
	public Collection<Pair<Thread, Throwable>> getThrownExceptions() {
		return exceptions;
	}

}
