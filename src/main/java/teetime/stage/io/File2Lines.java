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
package teetime.stage.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import teetime.stage.basic.AbstractTransformation;

/**
 * @author Christian Wulf
 *
 * @since 1.1
 *
 */
public final class File2Lines extends AbstractTransformation<File, String> {

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

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	@Override
	protected void execute(final File textFile) {
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(textFile.toPath(), Charset.forName(this.charset));
			String line;
			while ((line = reader.readLine()) != null) { // NOPMD
				line = line.trim();
				if (line.length() != 0) {
					this.outputPort.send(line);
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

}
