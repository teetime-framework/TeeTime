package teetime.util.concurrent.queue.putstrategy;

import java.util.Queue;

public class YieldPutStrategy<E> implements PutStrategy<E>
{
	@Override
	public void backoffOffer(final Queue<E> q, final E e)
	{
		while (!q.offer(e))
		{
			Thread.yield();
		}
	}

	@Override
	public void signal()
	{
		// Nothing
	}
}
