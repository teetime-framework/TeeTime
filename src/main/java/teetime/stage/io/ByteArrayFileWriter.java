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
