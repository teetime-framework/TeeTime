package teetime.framework.scheduling;

public class CountDownAndUpLatch {

	// private final Phaser phaser = new Phaser();

	private final Object lock = new Object();
	private int counter;

	/**
	 * Increases the counter by 1 in a thread-safe manner.
	 */
	public void countUp() {
		// phaser.register();
		synchronized (lock) {
			counter++;
		}
	}

	/**
	 * Decreases the counter by 1 in a thread-safe manner.
	 * <p>
	 * Notifies all waiting threads if the counter becomes zero.
	 */
	public void countDown() {
		// phaser.arriveAndDeregister();
		synchronized (lock) {
			counter--;
			if (counter == 0) {
				lock.notifyAll();
			}
		}
	}

	/**
	 * Waits for the counter to become non-positive.
	 *
	 * @throws InterruptedException
	 */
	public void await() throws InterruptedException {
		// phaser.arriveAndAwaitAdvance();
		synchronized (lock) {
			while (counter > 0) {
				lock.wait();
			}
		}
	}

	public int getCurrentCount() {
		// return phaser.getUnarrivedParties();
		synchronized (lock) {
			return counter;
		}
	}

}
