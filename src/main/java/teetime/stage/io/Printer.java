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

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import teetime.framework.AbstractConsumerStage;

/**
 * A filter to print objects to a configured stream
 *
 * @author Matthias Rohr, Jan Waller, Nils Christian Ehmke
 *
 * @since 1.10
 */
public final class Printer<T> extends AbstractConsumerStage<T> { // NOPMD not a data class

	public static final String STREAM_STDOUT = "STDOUT";
	public static final String STREAM_STDERR = "STDERR";
	public static final String STREAM_STDLOG = "STDlog";
	public static final String STREAM_NULL = "NULL";

	public static final String ENCODING_UTF8 = "UTF-8";

	private PrintStream printStream;
	private String streamName = STREAM_STDOUT;
	private String encoding = ENCODING_UTF8;
	private boolean active = true;
	private boolean append = true;

	@Override
	protected void execute(final T object) {
		if (this.active) {
			final StringBuilder sb = new StringBuilder(128);

			sb.append(super.getId()).append('(').append(object.getClass().getSimpleName()).append(") ")
					.append(object.toString());

			final String msg = sb.toString();
			if (this.printStream != null) {
				this.printStream.println(msg);
			} else {
				super.logger.info(msg);
			}
		}
	}

	public String getStreamName() {
		return this.streamName;
	}

	public void setStreamName(final String streamName) {
		this.streamName = streamName;
	}

	public String getEncoding() {
		return this.encoding;
	}

	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	public boolean isAppend() {
		return this.append;
	}

	public void setAppend(final boolean append) {
		this.append = append;
	}

	@Override
	public void onStarting() {
		super.onStarting();
		this.initializeStream();
	}

	@Override
	public void onTerminating() {
		this.closeStream();
		super.onTerminating();
	}

	private void initializeStream() {
		if (STREAM_STDOUT.equals(this.streamName)) {
			this.printStream = System.out;
			this.active = true;
		} else if (STREAM_STDERR.equals(this.streamName)) {
			this.printStream = System.err;
			this.active = true;
		} else if (STREAM_STDLOG.equals(this.streamName)) {
			this.printStream = null; // NOPMD
			this.active = true;
		} else if (STREAM_NULL.equals(this.streamName)) {
			this.printStream = null; // NOPMD
			this.active = false;
		} else {
			try {
				this.printStream = new PrintStream(
						Files.newOutputStream(Paths.get(this.streamName), StandardOpenOption.APPEND), false,
						this.encoding);
				this.active = true;
			} catch (final UnsupportedEncodingException ex) {
				this.active = false;
				throw new IllegalStateException("Encoding not supported", ex);
			} catch (final IOException ex) {
				this.active = false;
				throw new IllegalStateException("Stream could not be created", ex);
			}
		}
	}

	private void closeStream() {
		if ((this.printStream != null) && (this.printStream != System.out) && (this.printStream != System.err)) {
			this.printStream.close();
		}
	}

}
