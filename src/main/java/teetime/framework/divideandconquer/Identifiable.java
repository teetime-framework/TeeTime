package teetime.framework.divideandconquer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since 2.x
 *
 * @author Robin Mohr
 *
 */
public class Identifiable {

	private static AtomicInteger nextId = new AtomicInteger();
	private final int identifier;

	protected Identifiable() {
		this.identifier = nextId.incrementAndGet();
	}

	protected Identifiable(final int newID) {
		this.identifier = newID;
	}

	public int getID() {
		return this.identifier;
	}
}
