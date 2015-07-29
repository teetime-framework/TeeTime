package teetime.util.divideAndConquer;

/**
 * @since 2.x
 *
 * @author Robin Mohr
 *
 */
public abstract class Identifiable {

	private final int id;

	protected Identifiable(final int id) {
		this.id = id;
	}

	public int getID() {
		return this.id;
	}

}
