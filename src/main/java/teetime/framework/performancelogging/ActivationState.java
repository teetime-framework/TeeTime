package teetime.framework.performancelogging;

/**
 * This class stores an active/inactive flag at a given time.
 *
 * @author Adrian
 *
 */
public class ActivationState {
	// TODO vielleicht überflüssig
	public final static int SENDING_FAILED = -1;
	public final static int PULLING_FAILED = -2;
	public final static int GENERAL_EXCEPTION = -3;
	public final static int NOTHING_FAILED = 0;

	public final static int ACTIV = 1;
	public final static int ACTIV_WAITING = 0;
	public final static int BLOCKED = -1;
	public final static int TERMINATED = -2;
	private final int state;
	private final long timeStamp;
	private final int cause;

	public ActivationState(final int state) {
		this(state, System.nanoTime(), 0);
	}

	public ActivationState(final int state, final int cause) {
		this(state, System.nanoTime(), cause);
	}

	public ActivationState(final int state, final long timeStamp, final int cause) {
		this.state = state;
		this.timeStamp = timeStamp;
		this.cause = cause;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public int getCause() {
		return cause;
	}

	public int getState() {
		return state;
	}

}
