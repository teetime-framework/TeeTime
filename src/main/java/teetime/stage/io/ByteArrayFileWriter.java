/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
package teetime.stage.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import teetime.framework.AbstractConsumerStage;

import com.google.common.io.Files;

public class ByteArrayFileWriter extends AbstractConsumerStage<byte[]> {

	private final File file;
	private FileOutputStream fo;

	public ByteArrayFileWriter(final File file) {
		this.file = file;
		try {
			Files.touch(file);
			fo = new FileOutputStream(this.file);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void execute(final byte[] element) {

		try {
			fo.write(element);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void onTerminating() {
		try {
			fo.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
