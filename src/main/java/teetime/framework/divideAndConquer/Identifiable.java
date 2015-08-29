package teetime.framework.divideAndConquer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @since 2.x
 *
 * @author Robin Mohr
 *
 */
public abstract class Identifiable {

	private static AtomicInteger nextId = new AtomicInteger();
	private final int id;

	protected Identifiable() {
		this.id = nextId.incrementAndGet();
	}

	protected Identifiable(final int id) {
		this.id = id;
	}

	public int getID() {
		return this.id;
	}
}
