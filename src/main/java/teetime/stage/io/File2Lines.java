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
package teetime.stage.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * @author Christian Wulf
 *
 * @since 1.1
 *
 */
public final class File2Lines extends AbstractConsumerStage<File> {

	private final OutputPort<String> outputPort = this.createOutputPort();

	private final String charset;

	/**
	 * <ol>
	 * <li>charset = UTF-8
	 * </ol>
	 */
	public File2Lines() {
		this("UTF-8");
	}

	/**
	 *
	 * @param charset
	 *            to be used when interpreting text files
	 */
	public File2Lines(final String charset) {
		super();
		this.charset = charset;
	}

	@Override
	protected void execute(final File textFile) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), this.charset));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() != 0) {
					outputPort.send(line);
				} // else: ignore empty line
			}
		} catch (final FileNotFoundException e) {
			this.logger.error("", e);
		} catch (final IOException e) {
			this.logger.error("", e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (final IOException e) {
				this.logger.warn("", e);
			}
		}
	}

	public String getCharset() {
		return this.charset;
	}

	public OutputPort<String> getOutputPort() {
		return outputPort;
	}

}
