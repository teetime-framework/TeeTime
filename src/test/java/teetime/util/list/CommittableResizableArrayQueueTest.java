package teetime.util.list;

import org.junit.Assert;
import org.junit.Test;

public class CommittableResizableArrayQueueTest {

	@Test
	public void testCommit() throws Exception {
		CommittableResizableArrayQueue<Object> reservableArrayList = new CommittableResizableArrayQueue<Object>(null, 10);
		Object element = new Object();
		reservableArrayList.addToTailUncommitted(element);

		Assert.assertTrue(reservableArrayList.isEmpty());

		reservableArrayList.commit();

		Assert.assertFalse(reservableArrayList.isEmpty());
		Assert.assertEquals(element, reservableArrayList.getTail());
	}

	@Test
	public void testRollback() throws Exception {
		CommittableResizableArrayQueue<Object> reservableArrayList = new CommittableResizableArrayQueue<Object>(null, 10);
		Object element = new Object();
		reservableArrayList.addToTailUncommitted(element);

		Assert.assertTrue(reservableArrayList.isEmpty());

		reservableArrayList.rollback();

		Assert.assertTrue(reservableArrayList.isEmpty());
		// Assert.assertEquals(element, reservableArrayList.getLast());
	}

	@Test
	public void testRemove() throws Exception {
		CommittableResizableArrayQueue<Object> reservableArrayList = new CommittableResizableArrayQueue<Object>(null, 10);
		Object element = new Object();
		reservableArrayList.addToTailUncommitted(element);
		reservableArrayList.commit();

		Assert.assertEquals(element, reservableArrayList.removeFromHeadUncommitted());
		Assert.assertFalse(reservableArrayList.isEmpty());

		reservableArrayList.commit();

		Assert.assertTrue(reservableArrayList.isEmpty());
	}
}
