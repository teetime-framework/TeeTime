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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * A stage to compress and decompress byte arrays
 *
 * @author Nelson Tavares de Sousa
 *
 */
public final class ZipByteArray extends AbstractConsumerStage<byte[]> {

	private final OutputPort<byte[]> outputPort = this.createOutputPort();
	private final ZipMode mode;

	public enum ZipMode {
		COMP, DECOMP
	}

	public ZipByteArray(final ZipMode mode) {
		this.mode = mode;
	}

	@Override
	protected void execute(final byte[] element) {
		byte[] streamBytes = null;
		try {
			if (mode == ZipMode.COMP) {
				streamBytes = compress(element);
			} else {
				streamBytes = decompress(element);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputPort.send(streamBytes);
	}

	private byte[] compress(final byte[] data) throws IOException {
		Deflater deflater = new Deflater();
		deflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

		deflater.finish();
		byte[] compressedBytes = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(compressedBytes); // returns the generated code... index
			outputStream.write(compressedBytes, 0, count);
		}
		outputStream.close();
		byte[] outputBytes = outputStream.toByteArray();

		deflater.end();

		return outputBytes;
	}

	private byte[] decompress(final byte[] data) throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] uncompressedBytes = new byte[1024];
		while (!inflater.finished()) {
			int count = inflater.inflate(uncompressedBytes);
			outputStream.write(uncompressedBytes, 0, count);
		}
		outputStream.close();
		byte[] outputBytes = outputStream.toByteArray();

		inflater.end();

		return outputBytes;
	}

	public OutputPort<byte[]> getOutputPort() {
		return this.outputPort;
	}

}
