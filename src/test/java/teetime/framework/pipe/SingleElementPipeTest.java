package teetime.framework.pipe;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class SingleElementPipeTest {

	@Test(expected = IllegalArgumentException.class)
	public void testAdd() throws Exception {
		SingleElementPipe pipe = new SingleElementPipe(null, null);
		assertFalse(pipe.add(null));
	}

}
