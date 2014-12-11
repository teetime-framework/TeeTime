package teetime.framework;

import org.junit.Assert;
import org.junit.Test;

import teetime.stage.Cache;
import teetime.stage.Counter;

public class StageTest {

	@Test
	public void testId() {
		Counter<Object> counter0 = new Counter<Object>();
		Counter<Object> counter1 = new Counter<Object>();
		Assert.assertEquals("Counter-0", counter0.getId());
		Assert.assertEquals("Counter-1", counter1.getId());

		for (int i = 0; i < 100; i++) {
			Cache<Object> cache = new Cache<Object>();
			Assert.assertEquals("Cache-" + i, cache.getId());
		}
	}

}
