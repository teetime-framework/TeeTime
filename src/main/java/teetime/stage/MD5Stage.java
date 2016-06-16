/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import teetime.stage.basic.AbstractFilter;
import teetime.stage.taskfarm.ITaskFarmDuplicable;

public class MD5Stage extends AbstractFilter<String>implements ITaskFarmDuplicable<String, String> {

	private final Charset charset;

	/**
	 * encoding = UTF-8
	 */
	public MD5Stage() {
		this(Charset.forName("UTF-8"));
	}

	/**
	 * @param encoding
	 *            of the input strings for hashing algorithm
	 * @deprecated As of 3.0. Use {{@link #MD5Stage(Charset)} instead.
	 */
	@Deprecated
	public MD5Stage(final String encoding) {
		this.charset = Charset.forName(encoding);
	}

	/**
	 * @param charset
	 *            of the input strings for hashing algorithm
	 * @see java.nio.charset.StandardCharsets Available charsets
	 */
	public MD5Stage(final Charset charset) {
		this.charset = charset;
	}

	@Override
	protected void execute(final String element) {
		Hasher hasher = Hashing.md5().newHasher();
		hasher.putString(element, charset);
		outputPort.send(hasher.hash().toString());
	}

	public String getEncoding() {
		return charset.displayName();
	}

	@Override
	public ITaskFarmDuplicable<String, String> duplicate() {
		return new MD5Stage(charset);
	}
}
