package teetime.util.framework.concurrent;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

public class SignalingCounter {

	private final IntObjectMap<Object> conditions = new IntObjectHashMap<Object>();
	private int counter;

	// synchronized methods synchronize the map and the counter
	// synchronized(cond) synchronizes the individual numbers for which are being waited for

	public synchronized void inc() {
		counter++;
		conditionalNotifyAll(counter);
	}

	public synchronized void dec() {
		counter--;
		conditionalNotifyAll(counter);
	}

	private synchronized void conditionalNotifyAll(final int number) {
		if (conditions.containsKey(number)) {
			Object cond = conditions.get(number);
			synchronized (cond) {
				cond.notifyAll();
			}
		}
	}

	public synchronized void waitFor(final int number) throws InterruptedException {
		if (!conditions.containsKey(number)) {
			conditions.put(number, new Object());
		}

		final Object cond = conditions.get(number);
		synchronized (cond) {
			while (counter != number) {
				cond.wait();
			}
		}
	}
}
