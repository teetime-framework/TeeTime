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

import java.io.*;
import java.nio.CharBuffer;

import teetime.stage.basic.AbstractTransformation;

/**
 * @author Christian Wulf
 *
 */
public final class File2SeqOfWords extends AbstractTransformation<File, String> {

	private final String charset;
	private final int bufferCapacity;

	/**
	 * <ol>
	 * <li>charset = UTF-8
	 * <li>bufferCapacity = 1024
	 * </ol>
	 */
	public File2SeqOfWords() {
		this("UTF-8", 1024);
	}

	public File2SeqOfWords(final int bufferCapacity) {
		this("UTF-8", bufferCapacity);
	}

	public File2SeqOfWords(final String charset, final int bufferCapacity) {
		super();
		this.charset = charset;
		this.bufferCapacity = bufferCapacity;
	}

	@Override
	protected void execute(final File textFile) {
		BufferedReader reader = null; // NOPMD
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), this.charset));
			CharBuffer charBuffer = CharBuffer.allocate(bufferCapacity);
			int iterations = 0;
			while (reader.read(charBuffer) != -1) {
				final int position = getPreviousWhitespacePosition(charBuffer);
				if (-1 == position && logger.isErrorEnabled()) {
					logger.error("A word in the following text file is bigger than the buffer's capacity: " + textFile.getAbsolutePath());
					return;
				}
				final int limit = charBuffer.limit();

				charBuffer.limit(position);
				charBuffer.rewind();
				this.outputPort.send(charBuffer.toString()); // from position to limit-1

				if (logger.isDebugEnabled()) {
					logger.debug("Sent {} bytes", bufferCapacity * iterations++);
				}

				charBuffer.limit(limit);
				charBuffer.position(position);
				charBuffer.compact();
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

	private int getPreviousWhitespacePosition(final CharBuffer charBuffer) {
		char[] characters = charBuffer.array(); // NOPMD Array issue
		int index = charBuffer.arrayOffset() + charBuffer.position() - 1;

		while (index >= 0) {
			switch (characters[index]) { // NOPMD break not needed
			case ' ':
			case '\n':
			case '\r':
			case '\t':
				return index - charBuffer.arrayOffset(); // NOPMD
			default:
				index--;
			}
		}
		return -1;
	}

	public String getCharset() {
		return this.charset;
	}

	public int getBufferCapacity() {
		return bufferCapacity;
	}

}
