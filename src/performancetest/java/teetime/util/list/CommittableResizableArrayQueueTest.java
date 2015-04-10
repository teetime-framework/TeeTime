/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
