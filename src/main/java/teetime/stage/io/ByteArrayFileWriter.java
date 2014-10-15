package teetime.stage.io;

import java.io.File;
import java.io.FileOutputStream;

import teetime.framework.ConsumerStage;

public class ByteArrayFileWriter extends ConsumerStage<byte[]> {

	private final File file;

	public ByteArrayFileWriter(final File file) {
		this.file = file;
	}

	@Override
	protected void execute(final byte[] element) {
		FileOutputStream fo;
		// TODO check if file exists, otherwise create file
		try {
			fo = new FileOutputStream(this.file);
			fo.write(element);
			fo.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}