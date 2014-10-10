/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/
package teetime.stage.kieker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import teetime.framework.ProducerStage;

import kieker.common.exception.MonitoringRecordException;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.misc.RegistryRecord;
import kieker.common.util.registry.ILookup;
import kieker.common.util.registry.Lookup;

/**
 * This is a reader which reads the records from a TCP port.
 *
 * @author Jan Waller, Nils Christian Ehmke
 *
 * @since 1.10
 */
public class TCPReaderSink extends ProducerStage<IMonitoringRecord> {

	private static final int MESSAGE_BUFFER_SIZE = 65535;

	private final ILookup<String> stringRegistry = new Lookup<String>();
	private int port1 = 10133;
	private int port2 = 10134;

	private TCPStringReader tcpStringReader;

	private final AtomicInteger counter = new AtomicInteger(0);
	private final ScheduledThreadPoolExecutor executorService;

	public TCPReaderSink() {
		this.executorService = new ScheduledThreadPoolExecutor(1);
	}

	public final int getPort1() {
		return this.port1;
	}

	public final void setPort1(final int port1) {
		this.port1 = port1;
	}

	public final int getPort2() {
		return this.port2;
	}

	public final void setPort2(final int port2) {
		this.port2 = port2;
	}

	@Override
	public void onStarting() {
		this.executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				System.out.println("Records/s: " + TCPReaderSink.this.counter.getAndSet(0));
			}
		}, 0, 1, TimeUnit.SECONDS);

		this.tcpStringReader = new TCPStringReader(this.port2, this.stringRegistry);
		this.tcpStringReader.start();
		super.onStarting();
	}

	@Override
	protected void execute() {
		ServerSocketChannel serversocket = null;
		try {
			serversocket = ServerSocketChannel.open();
			serversocket.socket().bind(new InetSocketAddress(this.port1));
			if (super.logger.isDebugEnabled()) {
				super.logger.debug("Listening on port " + this.port1);
			}
			// BEGIN also loop this one?
			final SocketChannel socketChannel = serversocket.accept();
			final ByteBuffer buffer = ByteBuffer.allocateDirect(MESSAGE_BUFFER_SIZE);
			while (socketChannel.read(buffer) != -1) {
				buffer.flip();
				// System.out.println("Reading, remaining:" + buffer.remaining());
				try {
					while (buffer.hasRemaining()) {
						buffer.mark();
						final int clazzid = buffer.getInt();
						final long loggingTimestamp = buffer.getLong();
						final IMonitoringRecord record;
						try { // NOCS (Nested try-catch)
							record = AbstractMonitoringRecord.createFromByteBuffer(clazzid, buffer, this.stringRegistry);
							record.setLoggingTimestamp(loggingTimestamp);
							// this.send(record);
							this.counter.incrementAndGet();
						} catch (final MonitoringRecordException ex) {
							super.logger.error("Failed to create record.", ex);
						}
					}
					buffer.clear();
				} catch (final BufferUnderflowException ex) {
					buffer.reset();
					// System.out.println("Underflow, remaining:" + buffer.remaining());
					buffer.compact();
				}
			}
			// System.out.println("Channel closing...");
			socketChannel.close();
			// END also loop this one?
		} catch (final IOException ex) {
			super.logger.error("Error while reading", ex);
		} finally {
			if (null != serversocket) {
				try {
					serversocket.close();
				} catch (final IOException e) {
					if (super.logger.isDebugEnabled()) {
						super.logger.debug("Failed to close TCP connection!", e);
					}
				}
			}

			this.terminate();
		}
	}

	@Override
	public void onIsPipelineHead() {
		this.executorService.shutdown();
		this.tcpStringReader.interrupt();
		super.onIsPipelineHead();
	}

	/**
	 *
	 * @author Jan Waller
	 *
	 * @since 1.8
	 */
	private static class TCPStringReader extends Thread {

		private static final int MESSAGE_BUFFER_SIZE = 65535;

		private static final Log LOG = LogFactory.getLog(TCPStringReader.class);

		private final int port;
		private final ILookup<String> stringRegistry;

		public TCPStringReader(final int port, final ILookup<String> stringRegistry) {
			this.port = port;
			this.stringRegistry = stringRegistry;
		}

		@Override
		public void run() {
			ServerSocketChannel serversocket = null;
			try {
				serversocket = ServerSocketChannel.open();
				serversocket.socket().bind(new InetSocketAddress(this.port));
				if (LOG.isDebugEnabled()) {
					LOG.debug("Listening on port " + this.port);
				}
				// BEGIN also loop this one?
				final SocketChannel socketChannel = serversocket.accept();
				final ByteBuffer buffer = ByteBuffer.allocateDirect(MESSAGE_BUFFER_SIZE);
				while ((socketChannel.read(buffer) != -1)) {
					buffer.flip();
					try {
						while (buffer.hasRemaining()) {
							buffer.mark();
							RegistryRecord.registerRecordInRegistry(buffer, this.stringRegistry);
						}
						buffer.clear();
					} catch (final BufferUnderflowException ex) {
						buffer.reset();
						buffer.compact();
					}
				}
				socketChannel.close();
				// END also loop this one?
			} catch (final ClosedByInterruptException ex) {
				LOG.warn("Reader interrupted", ex);
			} catch (final IOException ex) {
				LOG.error("Error while reading", ex);
			} finally {
				if (null != serversocket) {
					try {
						serversocket.close();
					} catch (final IOException e) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Failed to close TCP connection!", e);
						}
					}
				}
			}

			LOG.debug("StringRegistryReader thread has finished.");
		}
	}
}
