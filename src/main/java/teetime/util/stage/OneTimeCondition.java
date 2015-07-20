package teetime.util.stage;

public class OneTimeCondition {

	private final Object syncObj = new Object();
	private volatile boolean conditionMet;

	public void signalAll() {
		synchronized (syncObj) {
			conditionMet = true;
			syncObj.notifyAll();
		}
	}

	public void await() throws InterruptedException {
		if (!conditionMet) {
			synchronized (syncObj) {
				while (!conditionMet) {
					syncObj.wait();
				}
			}
		}
	}
}
