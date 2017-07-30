/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import teetime.stage.basic.AbstractTransformation;

public class ByteArray2String extends AbstractTransformation<byte[], String> {

	private final Charset charset;

	/**
	 * Creates a new instance with the UTF-8 charset.
	 */
	public ByteArray2String() {
		this(StandardCharsets.UTF_8);
	}

	public ByteArray2String(final Charset charset) {
		super();
		this.charset = charset;
		setStateless(true);
	}

	@Override
	protected void execute(final byte[] element) {
		this.outputPort.send(new String(element, charset));
	}

}
