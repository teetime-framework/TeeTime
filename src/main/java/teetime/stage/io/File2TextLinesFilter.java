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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import teetime.stage.basic.AbstractTransformation;
import teetime.stage.util.TextLineContainer;

/**
 * @author Christian Wulf
 *
 * @since 1.0
 *
 */
public final class File2TextLinesFilter extends AbstractTransformation<File, TextLineContainer> {

	private final String charset;

	/**
	 * <ol>
	 * <li>charset = UTF-8
	 * </ol>
	 *
	 * @since 1.1
	 */
	public File2TextLinesFilter() {
		this("UTF-8");
	}

	/**
	 *
	 * @param charset
	 *            to be used when interpreting text files
	 *
	 * @since 1.1
	 */
	public File2TextLinesFilter(final String charset) {
		super();
		this.charset = charset;
	}

	@Override
	protected void execute(final File textFile) {
		int lineNumber = 1;
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(textFile.toPath(), Charset.forName(this.charset));
			String textLine;
			while ((textLine = reader.readLine()) != null) {
				textLine = textLine.trim();
				if (textLine.length() != 0) {
					this.outputPort.send(new TextLineContainer(textFile, textLine, lineNumber));
				} // else: ignore empty line
				lineNumber++;
			}
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
