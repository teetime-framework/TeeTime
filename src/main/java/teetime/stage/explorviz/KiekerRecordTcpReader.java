package teetime.stage.explorviz;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import teetime.framework.ProducerStage;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.util.registry.ILookup;
import kieker.common.util.registry.Lookup;

public class KiekerRecordTcpReader extends ProducerStage<IMonitoringRecord> {

	private static final int MESSAGE_BUFFER_SIZE = 65535;

	private static final byte HostApplicationMetaDataRecord_CLAZZ_ID = 0;
	private static final byte BeforeOperationEventRecord_CLAZZ_ID = 1;
	private static final byte AfterOperationEventRecord_CLAZZ_ID = 3;
	private static final byte StringRegistryRecord_CLAZZ_ID = 4;

	private static final int HostApplicationMetaDataRecord_BYTE_LENGTH = 16;
	private static final int BeforeOperationEventRecord_COMPRESSED_BYTE_LENGTH = 36;
	private static final int AfterOperationEventRecord_COMPRESSED_BYTE_LENGTH = 20;

	private final ILookup<String> stringRegistry = new Lookup<String>();

	private final List<byte[]> waitingForStringMessages = new ArrayList<byte[]>(1024);

	private int port1 = 10133;

	public final int getPort1() {
		return this.port1;
	}

	public final void setPort1(final int port1) {
		this.port1 = port1;
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
						this.messagesfromByteArray(buffer);
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

	private final void messagesfromByteArray(final ByteBuffer buffer) {
		final byte clazzId = buffer.get();
		switch (clazzId) {
		case HostApplicationMetaDataRecord_CLAZZ_ID: {
			if (buffer.remaining() >= HostApplicationMetaDataRecord_BYTE_LENGTH) {
				this.readInHostApplicationMetaData(buffer);
				break;
			}
			buffer.position(buffer.position() - 1);
			buffer.compact();
			return;
		}
		case BeforeOperationEventRecord_CLAZZ_ID: {
			if (buffer.remaining() >= BeforeOperationEventRecord_COMPRESSED_BYTE_LENGTH) {
				this.readInBeforeOperationEvent(buffer);
				break;
			}
			buffer.position(buffer.position() - 1);
			buffer.compact();
			return;
		}
		case AfterOperationEventRecord_CLAZZ_ID: {
			if (buffer.remaining() >= AfterOperationEventRecord_COMPRESSED_BYTE_LENGTH) {
				this.readInAfterOperationEvent(buffer);
				break;
			}
			buffer.position(buffer.position() - 1);
			buffer.compact();
			return;
		}
		case StringRegistryRecord_CLAZZ_ID: {
			int mapId = 0;
			int stringLength = 0;
			if (buffer.remaining() >= 8) {
				mapId = buffer.getInt();
				stringLength = buffer.getInt();
			} else {
				buffer.position(buffer.position() - 1);
				buffer.compact();
				return;
			}

			if (buffer.remaining() >= stringLength) {
				final byte[] stringByteArray = new byte[stringLength];

				buffer.get(stringByteArray);

				this.stringRegistry.set(new String(stringByteArray), mapId);

				this.checkWaitingMessages();
			} else {
				buffer.position(buffer.position() - 9);
				buffer.compact();
				return;
			}
			break;
		}
		default: {
			System.out.println("unknown class id " + clazzId + " at offset "
					+ (buffer.position() - 1));
			return;
		}
		}
	}

	private final void readInHostApplicationMetaData(final ByteBuffer buffer) {
		final int systemnameId = buffer.getInt();
		final int ipaddressId = buffer.getInt();
		final int hostnameId = buffer.getInt();
		final int applicationId = buffer.getInt();
		// just consume; not necessary for kieker
	}

	private final void readInBeforeOperationEvent(final ByteBuffer buffer) {
		final long timestamp = buffer.getLong();
		final long traceId = buffer.getLong();
		final int orderIndex = buffer.getInt();
		final int objectId = buffer.getInt();
		final int operationId = buffer.getInt();
		final int clazzId = buffer.getInt();
		final int interfaceId = buffer.getInt();

		final String operation = this.stringRegistry.get(operationId);
		final String clazz = this.stringRegistry.get(clazzId);
		if (operation == null || clazz == null) {
			this.putInWaitingMessages(buffer, BeforeOperationEventRecord_COMPRESSED_BYTE_LENGTH + 1);
			return;
		}

		final IMonitoringRecord record = new BeforeOperationEvent(timestamp, traceId, orderIndex, operation, clazz);
		this.send(this.outputPort, record);
	}

	private final void readInAfterOperationEvent(final ByteBuffer buffer) {
		final long timestamp = buffer.getLong();
		final long traceId = buffer.getLong();
		final int orderIndex = buffer.getInt();

		final IMonitoringRecord record = new AfterOperationEvent(timestamp, traceId, orderIndex, null, null);
		this.send(this.outputPort, record);
	}

	private final void putInWaitingMessages(final ByteBuffer buffer, final int length) {
		final byte[] message = new byte[length];
		buffer.position(buffer.position() - length);
		buffer.get(message);
		this.waitingForStringMessages.add(message);
	}

	private final void checkWaitingMessages() {
		final List<byte[]> localWaitingList = new ArrayList<byte[]>();
		for (final byte[] waitingMessage : this.waitingForStringMessages) {
			localWaitingList.add(waitingMessage);
		}
		this.waitingForStringMessages.clear();

		for (final byte[] waitingMessage : localWaitingList) {
			final ByteBuffer buffer = ByteBuffer.wrap(waitingMessage);
			final byte waitingMessageClazzId = buffer.get();
			switch (waitingMessageClazzId) {
			case HostApplicationMetaDataRecord_CLAZZ_ID:
				this.readInHostApplicationMetaData(buffer);
				break;
			case BeforeOperationEventRecord_CLAZZ_ID:
				this.readInBeforeOperationEvent(buffer);
				break;
			case AfterOperationEventRecord_CLAZZ_ID:
				this.readInAfterOperationEvent(buffer);
				break;
			default:
				break;
			}
		}
	}

}
