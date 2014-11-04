package teetime.stage.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import teetime.framework.ProducerStage;

public abstract class AbstractTcpReader<T> extends ProducerStage<T> {

	private final int port;
	private final int bufferCapacity;

	public AbstractTcpReader(final int port, final int bufferCapacity) {
		super();
		this.port = port;
		this.bufferCapacity = bufferCapacity;
	}

	@Override
	protected void execute() {
		ServerSocketChannel serversocket = null;
		try {
			serversocket = ServerSocketChannel.open();
			serversocket.socket().bind(new InetSocketAddress(this.port));
			logger.debug("Listening on port " + this.port);

			final SocketChannel socketChannel = serversocket.accept();
			try {
				final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferCapacity);
				while (socketChannel.read(buffer) != -1) {
					process(buffer);
				}
			} finally {
				socketChannel.close();
			}
		} catch (final IOException ex) {
			logger.error("Error while reading", ex);
		} finally {
			if (null != serversocket) {
				try {
					serversocket.close();
				} catch (final IOException e) {
					logger.debug("Failed to close TCP connection!", e);
				}
			}

			this.terminate();
		}
	}

	private void process(final ByteBuffer buffer) {
		buffer.flip();
		try {
			while (buffer.hasRemaining()) {
				buffer.mark();
				this.read(buffer);
			}
			buffer.clear();
		} catch (final BufferUnderflowException ex) {
			buffer.reset();
			buffer.compact();
		}
	}

	protected abstract void read(final ByteBuffer buffer);

}
