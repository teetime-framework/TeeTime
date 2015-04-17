/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import teetime.framework.AbstractProducerStage;

public abstract class AbstractTcpReader<T> extends AbstractProducerStage<T> {

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
			logger.error("Error while reading.", ex);
		} finally {
			if (null != serversocket) {
				try {
					serversocket.close();
				} catch (final IOException e) {
					logger.debug("Failed to close TCP connection.", e);
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
				boolean success = this.read(buffer);
				if (!success) {
					buffer.reset();
					buffer.compact();
					return;
				}
			}
			buffer.clear();
		} catch (final BufferUnderflowException ex) {
			logger.warn("Unexpected exception. Resetting and compacting buffer.", ex);
			buffer.reset();
			buffer.compact();
		}
	}

	/**
	 * @param buffer
	 *            to be read from
	 * @return <ul>
	 *         <li><code>true</code> when there were enough bytes to perform the read operation
	 *         <li><code>false</code> otherwise. In this case, the buffer is reset, compacted, and filled with new content.
	 */
	protected abstract boolean read(final ByteBuffer buffer);

}
