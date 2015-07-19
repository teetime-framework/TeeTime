/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.stage;

import java.nio.charset.Charset;

import teetime.stage.basic.AbstractFilter;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class MD5Stage extends AbstractFilter<String> {

	private String encoding = "UTF-8";

	@Override
	protected void execute(final String element) {
		Hasher hasher = Hashing.md5().newHasher();
		hasher.putString(element, Charset.forName(encoding));
		outputPort.send(hasher.hash().toString());
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}
}
