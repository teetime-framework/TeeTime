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
package teetime.variant.methodcallWithPorts.stage.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import teetime.variant.methodcallWithPorts.framework.core.ProducerStage;

import kieker.common.exception.MonitoringRecordException;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.flow.trace.operation.CallOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
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
public class TCPReader extends ProducerStage<IMonitoringRecord> {

	private static final int MESSAGE_BUFFER_SIZE = 65535;

	// BETTER use a non thread-safe implementation to increase performance. A thread-safe version is not necessary.
	private final ILookup<String> stringRegistry = new Lookup<String>();
	private int port1 = 10133;
	private int port2 = 10134;

	private TCPStringReader tcpStringReader;

	private RecordFactory recordFactory;

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
	public void onStart() {
		this.recordFactory = new RecordFactory();
		this.register();

		this.tcpStringReader = new TCPStringReader(this.port2, this.stringRegistry);
		this.tcpStringReader.start();
		super.onStart();
	}

	private void register() {
		this.recordFactory.register(TraceMetadata.class.getCanonicalName(), new IRecordFactoryMethod() {
			@Override
			public IMonitoringRecord create(final ByteBuffer buffer, final ILookup<String> stringRegistry) {
				return new TraceMetadata(buffer, stringRegistry);
			}
		});

		this.recordFactory.register(KiekerMetadataRecord.class.getCanonicalName(), new IRecordFactoryMethod() {
			@Override
			public IMonitoringRecord create(final ByteBuffer buffer, final ILookup<String> stringRegistry) {
				return new KiekerMetadataRecord(buffer, stringRegistry);
			}
		});

		this.recordFactory.register(BeforeOperationEvent.class.getCanonicalName(), new IRecordFactoryMethod() {
			@Override
			public IMonitoringRecord create(final ByteBuffer buffer, final ILookup<String> stringRegistry) {
				return new BeforeOperationEvent(buffer, stringRegistry);
			}
		});

		this.recordFactory.register(AfterOperationEvent.class.getCanonicalName(), new IRecordFactoryMethod() {
			@Override
			public IMonitoringRecord create(final ByteBuffer buffer, final ILookup<String> stringRegistry) {
				return new AfterOperationEvent(buffer, stringRegistry);
			}
		});

		this.recordFactory.register(CallOperationEvent.class.getCanonicalName(), new IRecordFactoryMethod() {
			@Override
			public IMonitoringRecord create(final ByteBuffer buffer, final ILookup<String> stringRegistry) {
				return new CallOperationEvent(buffer, stringRegistry);
			}
		});
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
						this.createAndSendRecord(buffer);
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

			this.setReschedulable(false);
			this.tcpStringReader.terminate();
		}
	}

	private void createAndSendRecord(final ByteBuffer buffer) {
		final int clazzid = buffer.getInt();
		final long loggingTimestamp = buffer.getLong();
		final IMonitoringRecord record;
		try { // NOCS (Nested try-catch)
			// record = this.recordFactory.create(clazzid, buffer, this.stringRegistry);
			record = AbstractMonitoringRecord.createFromByteBuffer(clazzid, buffer, this.stringRegistry);
			record.setLoggingTimestamp(loggingTimestamp);
			this.send(this.outputPort, record);
		} catch (final MonitoringRecordException ex) {
			super.logger.error("Failed to create record.", ex);
		}
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
		private volatile boolean terminated = false; // NOPMD
		private volatile Thread readerThread;

		public TCPStringReader(final int port, final ILookup<String> stringRegistry) {
			this.port = port;
			this.stringRegistry = stringRegistry;
		}

		public void terminate() {
			this.terminated = true;
			this.readerThread.interrupt();
		}

		@Override
		public void run() {
			this.readerThread = Thread.currentThread();
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
				while ((socketChannel.read(buffer) != -1) && (!this.terminated)) {
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
		}
	}
}
