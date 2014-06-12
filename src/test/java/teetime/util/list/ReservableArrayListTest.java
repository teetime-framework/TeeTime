package teetime.util.list;

import org.junit.Assert;
import org.junit.Test;

public class ReservableArrayListTest {

	@Test
	public void testCommit() throws Exception {
		ReservableArrayList<Object> reservableArrayList = new ReservableArrayList<Object>(10);
		Object element = new Object();
		reservableArrayList.reservedAdd(element);

		Assert.assertTrue(reservableArrayList.isEmpty());

		reservableArrayList.commit();

		Assert.assertFalse(reservableArrayList.isEmpty());
		Assert.assertEquals(element, reservableArrayList.getLast());
	}

	@Test
	public void testRollback() throws Exception {
		ReservableArrayList<Object> reservableArrayList = new ReservableArrayList<Object>(10);
		Object element = new Object();
		reservableArrayList.reservedAdd(element);

		Assert.assertTrue(reservableArrayList.isEmpty());

		reservableArrayList.rollback();

		Assert.assertTrue(reservableArrayList.isEmpty());
		// Assert.assertEquals(element, reservableArrayList.getLast());
	}

	@Test
	public void testRemove() throws Exception {
		ReservableArrayList<Object> reservableArrayList = new ReservableArrayList<Object>(10);
		Object element = new Object();
		reservableArrayList.reservedAdd(element);
		reservableArrayList.commit();

		Assert.assertEquals(element, reservableArrayList.reservedRemoveLast());
		Assert.assertFalse(reservableArrayList.isEmpty());

		reservableArrayList.commit();

		Assert.assertTrue(reservableArrayList.isEmpty());
	}
}
