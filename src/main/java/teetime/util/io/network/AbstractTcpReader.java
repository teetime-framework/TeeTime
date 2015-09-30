/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.util.io.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;

public abstract class AbstractTcpReader implements Runnable {

	private final int port;
	private final int bufferCapacity;
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	private volatile boolean terminated;

	public AbstractTcpReader(final int port, final int bufferCapacity, final Logger logger) {
		super();
		this.port = port;
		this.bufferCapacity = bufferCapacity;
		this.logger = logger;
	}

	@Override
	public final void run() {
		ServerSocketChannel serversocket = null;
		try {
			serversocket = ServerSocketChannel.open();
			serversocket.socket().bind(new InetSocketAddress(this.port));
			if (logger.isDebugEnabled()) {
				logger.debug("Listening on port " + this.port);
			}

			final SocketChannel socketChannel = serversocket.accept();
			try {
				final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferCapacity);
				while (socketChannel.read(buffer) != -1 && !terminated) {
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
		}
	}

	private void process(final ByteBuffer buffer) {
		buffer.flip();
		try {
			while (buffer.hasRemaining()) {
				buffer.mark();
				boolean success = this.onBufferReceived(buffer);
				if (!success) {
					buffer.reset();
					buffer.compact();
					return;
				}
			}
			buffer.clear();
		} catch (final BufferUnderflowException ex) {
			logger.warn("Unexpected buffer underflow. Resetting and compacting buffer.", ex);
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
	 *         </ul>
	 */
	protected abstract boolean onBufferReceived(final ByteBuffer buffer);

	public void terminate() {
		this.terminated = true;
	}

	public int getPort() {
		return port;
	}
}
