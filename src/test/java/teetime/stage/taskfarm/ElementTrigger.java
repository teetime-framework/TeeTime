package teetime.stage.taskfarm;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import teetime.framework.AbstractProducerStage;

public class ElementTrigger<E> extends AbstractProducerStage<E> {

	private static final Object TRIGGER = new Object();
	private static final Object TERMINATE_TRIGGER = null;

	private final BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>();
	private final Collection<E> elements;
	private Iterator<E> iterator;

	public ElementTrigger(final E... elements) {
		this(Arrays.asList(elements));
	}

	public ElementTrigger(final Collection<E> elements) {
		this.elements = elements;
		this.iterator = elements.iterator();
	}

	@Override
	protected void execute() {
		try {
			Object trigger = queue.take(); // blocking
			if (trigger == TERMINATE_TRIGGER) {
				terminateStage();
			} else {
				if (iterator.hasNext()) {
					E element = iterator.next();
					outputPort.send(element);
				} else {
					// infinite iteration: instantiate a new iterator
					iterator = elements.iterator();
				}
			}
		} catch (InterruptedException e) {
			terminateStage();
		}
	}

	public void trigger() {
		queue.add(TRIGGER);
	}

	@Override
	public void terminateStage() {
		queue.add(TERMINATE_TRIGGER);
	}

}
