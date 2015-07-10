package teetime.stage;

import java.nio.charset.Charset;

import teetime.stage.basic.AbstractFilter;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class MD5Stage extends AbstractFilter<String> {

	@Override
	protected void execute(final String element) {
		Hasher hasher = Hashing.md5().newHasher();
		hasher.putString(element, Charset.forName("UTF-8"));
		outputPort.send(hasher.hash().toString());
	}

}
