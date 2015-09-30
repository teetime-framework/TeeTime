package teetime.testutil;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public final class AssertHelper {

	private AssertHelper() {
		// utility class
	}

	@SuppressWarnings("unchecked")
	public static <S, T extends S> T assertInstanceOf(final Class<T> expectedClazz, final S object) {
		assertThat(object, is(instanceOf(expectedClazz)));
		return (T) object;
	}
}
