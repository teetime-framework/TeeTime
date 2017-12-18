package teetime.framework.scheduling.globaltaskpool;

public class CountDownAndUpLatch {

	// private final Phaser phaser = new Phaser();

	private final Object lock = new Object();
	private int counter;

	public void countUp() {
		// phaser.register();
		synchronized (lock) {
			counter++;
		}
	}

	public void countDown() {
		// phaser.arriveAndDeregister();
		synchronized (lock) {
			counter--;
			if (counter == 0) {
				lock.notifyAll();
			}
		}
	}

	public void await() {
		// phaser.arriveAndAwaitAdvance();
		synchronized (lock) {
			while (counter > 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
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
