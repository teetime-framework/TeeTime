package util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.HeadStage;
import teetime.variant.methodcallWithPorts.framework.core.HeadPipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.kieker.Dir2RecordsFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AbstractOperationEvent;
import kieker.common.util.registry.IMonitoringRecordReceiver;
import kieker.common.util.registry.Registry;

public class KiekerLoadDriver {

	private final List<IMonitoringRecord> elementCollection = new LinkedList<IMonitoringRecord>();
	private final RunnableStage runnableStage;
	private long[] timings;

	public KiekerLoadDriver(final File directory) {
		HeadStage producerPipeline = this.buildProducerPipeline(directory);
		this.runnableStage = new RunnableStage(producerPipeline);
	}

	private HeadPipeline<Dir2RecordsFilter, CollectorSink<IMonitoringRecord>> buildProducerPipeline(final File directory) {
		ClassNameRegistryRepository classNameRegistryRepository = new ClassNameRegistryRepository();
		// create stages
		Dir2RecordsFilter dir2RecordsFilter = new Dir2RecordsFilter(classNameRegistryRepository);
		CollectorSink<IMonitoringRecord> collector = new CollectorSink<IMonitoringRecord>(this.elementCollection);

		final HeadPipeline<Dir2RecordsFilter, CollectorSink<IMonitoringRecord>> pipeline = new HeadPipeline<Dir2RecordsFilter, CollectorSink<IMonitoringRecord>>();
		pipeline.setFirstStage(dir2RecordsFilter);
		pipeline.setLastStage(collector);

		dir2RecordsFilter.getInputPort().setPipe(new SpScPipe<File>(1));
		SingleElementPipe.connect(dir2RecordsFilter.getOutputPort(), collector.getInputPort());

		dir2RecordsFilter.getInputPort().getPipe().add(directory);

		return pipeline;
	}

	public Collection<IMonitoringRecord> load() {
		this.runnableStage.run();
		return this.elementCollection;
	}

	private static class RecordReceiver implements IMonitoringRecordReceiver {

		private final Registry<String> stringRegistry;
		private final ByteBuffer buffer = ByteBuffer.allocateDirect(Short.MAX_VALUE * 10);
		private SocketChannel socketChannel;

		public RecordReceiver(final Registry<String> stringRegistry) throws IOException {
			this.stringRegistry = stringRegistry;
		}

		@Override
		public boolean newMonitoringRecord(final IMonitoringRecord record) {
			System.out.println("Registering " + record);
			record.writeBytes(this.buffer, this.stringRegistry);
			this.buffer.flip();
			try {
				int writtenBytes = this.socketChannel.write(this.buffer);
				System.out.println("writtenBytes: " + writtenBytes);
				this.buffer.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		public void connect() throws IOException {
			String hostname = "localhost";
			int port = 10134;
			System.out.println("Connecting to " + hostname + ":" + port);

			this.socketChannel = SocketChannel.open();
			this.socketChannel.connect(new InetSocketAddress(hostname, port));
		}

		// public void sendRegistryRecords() throws IOException {
		// String hostname = "localhost";
		// int port = 10134;
		// System.out.println("Connecting to " + hostname + ":" + port);
		//
		// SocketChannel socketChannel = SocketChannel.open();
		// try {
		// socketChannel.connect(new InetSocketAddress(hostname, port));
		// this.buffer.flip();
		// socketChannel.write(this.buffer);
		// } finally {
		// socketChannel.close();
		// }
		// }

		public void close() throws IOException {
			this.socketChannel.close();
		}

	}

	public static void main(final String[] args) throws IOException {
		final File directory = new File(args[0]);
		final File outputFile = new File(args[1]);
		final int runs = Integer.parseInt(args[2]);

		KiekerLoadDriver kiekerLoadDriver = new KiekerLoadDriver(directory);
		kiekerLoadDriver.start(runs);
		kiekerLoadDriver.writeTimingsToFile(outputFile);
	}

	public void start(final int runs) throws IOException {
		this.timings = new long[runs];
		Collection<IMonitoringRecord> records = this.load();

		final Registry<String> stringRegistry = new Registry<String>();
		ByteBuffer recordBuffer = ByteBuffer.allocateDirect(Short.MAX_VALUE);
		ByteBuffer duplicateBuffer = recordBuffer.duplicate();

		RecordReceiver recordReceiver = new RecordReceiver(stringRegistry);
		stringRegistry.setRecordReceiver(recordReceiver);
		try {
			recordReceiver.connect();

			String hostname = "localhost";
			int port = 10133;
			System.out.println("Connecting to " + hostname + ":" + port);
			long traceId = 0;

			SocketChannel socketChannel = SocketChannel.open();
			try {
				socketChannel.connect(new InetSocketAddress(hostname, port));
				for (int i = 0; i < runs; i++) {
					for (IMonitoringRecord record : records) {
						int clazzId = stringRegistry.get(record.getClass().getName());
						recordBuffer.putInt(clazzId);
						recordBuffer.putLong(record.getLoggingTimestamp());
						duplicateBuffer.position(recordBuffer.position());
						// AbstractOperationEvent writes (Long, Long traceId, ...)
						record.writeBytes(recordBuffer, stringRegistry);

						if (record instanceof AbstractOperationEvent) {
							duplicateBuffer.getLong();
							duplicateBuffer.putLong(traceId);
						} else if (record instanceof TraceMetadata) {
							duplicateBuffer.putLong(traceId);
						}
					}
					recordBuffer.flip();
					// System.out.println("position: " + recordBuffer.position());
					// System.out.println("limit: " + recordBuffer.limit());
					long start_ns = System.nanoTime();
					int writtenBytes = socketChannel.write(recordBuffer);
					long stop_ns = System.nanoTime();
					this.timings[i] = stop_ns - start_ns;
					if ((i % 100000) == 0) {
						System.out.println(i); // NOPMD (System.out)
					}
					// System.out.println("writtenBytes (record): " + writtenBytes);
					recordBuffer.clear();
					duplicateBuffer.clear();
					traceId++;
				}
			} finally {
				socketChannel.close();
			}

		} finally {
			recordReceiver.close();
		}
	}

	public void writeTimingsToFile(final File outputFile) throws UnsupportedEncodingException, FileNotFoundException {
		PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile), 8192 * 8), false, "UTF-8");
		try {
			for (long timing : this.timings) {
				ps.println("0;" + timing);
			}
		} finally {
			ps.close();
		}
	}
}
