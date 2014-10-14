package teetime.stage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

/**
 * A stage to compress and decompress byte arrays
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class ZipByteArray extends ConsumerStage<byte[]> {

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
		byte[] cache = null;
		try {
			if (mode == ZipMode.COMP) {
				cache = compress(element);
			} else {
				cache = decompress(element);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.send(this.outputPort, cache);
	}

	private byte[] compress(final byte[] data) throws IOException {
		Deflater deflater = new Deflater();
		deflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

		deflater.finish();
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer); // returns the generated code... index
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		byte[] output = outputStream.toByteArray();

		deflater.end();

		return output;
	}

	private byte[] decompress(final byte[] data) throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		byte[] output = outputStream.toByteArray();

		inflater.end();

		return output;
	}

	public OutputPort<? extends byte[]> getOutputPort() {
		return this.outputPort;
	}

}
