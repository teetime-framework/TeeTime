package teetime.variant.methodcallWithPorts.examples.kiekerdays;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.kieker.Dir2RecordsFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;

import kieker.common.record.IMonitoringRecord;
import kieker.common.util.registry.IMonitoringRecordReceiver;
import kieker.common.util.registry.Registry;

public class KiekerLoadDriver {

	private final List<IMonitoringRecord> elementCollection = new LinkedList<IMonitoringRecord>();
	private final RunnableStage runnableStage;
	private long[] timings;

	public KiekerLoadDriver(final File directory) {
		StageWithPort producerPipeline = this.buildProducerPipeline(directory);
		this.runnableStage = new RunnableStage(producerPipeline);
	}

	private StageWithPort buildProducerPipeline(final File directory) {
		ClassNameRegistryRepository classNameRegistryRepository = new ClassNameRegistryRepository();
		// create stages
		Dir2RecordsFilter dir2RecordsFilter = new Dir2RecordsFilter(classNameRegistryRepository);
		CollectorSink<IMonitoringRecord> collector = new CollectorSink<IMonitoringRecord>(this.elementCollection);

		final Pipeline<Dir2RecordsFilter, CollectorSink<IMonitoringRecord>> pipeline = new Pipeline<Dir2RecordsFilter, CollectorSink<IMonitoringRecord>>();
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
		kiekerLoadDriver.timings = new long[runs];
		Collection<IMonitoringRecord> records = kiekerLoadDriver.load();

		final Registry<String> stringRegistry = new Registry<String>();
		ByteBuffer recordBuffer = ByteBuffer.allocateDirect(Short.MAX_VALUE);

		RecordReceiver recordReceiver = new RecordReceiver(stringRegistry);
		stringRegistry.setRecordReceiver(recordReceiver);
		try {
			recordReceiver.connect();

			String hostname = "localhost";
			int port = 10133;
			System.out.println("Connecting to " + hostname + ":" + port);

			SocketChannel socketChannel = SocketChannel.open();
			try {
				socketChannel.connect(new InetSocketAddress(hostname, port));
				for (int i = 0; i < runs; i++) {
					for (IMonitoringRecord record : records) {
						// TODO increase trace id
						int clazzId = stringRegistry.get(record.getClass().getName());
						recordBuffer.putInt(clazzId);
						recordBuffer.putLong(record.getLoggingTimestamp());
						record.writeBytes(recordBuffer, stringRegistry);
					}
					recordBuffer.flip();
					// System.out.println("position: " + recordBuffer.position());
					// System.out.println("limit: " + recordBuffer.limit());
					long start_ns = System.nanoTime();
					int writtenBytes = socketChannel.write(recordBuffer);
					long stop_ns = System.nanoTime();
					kiekerLoadDriver.timings[i] = stop_ns - start_ns;
					if ((i % 100000) == 0) {
						System.out.println(i); // NOPMD (System.out)
					}
					// System.out.println("writtenBytes (record): " + writtenBytes);
					recordBuffer.clear();
				}
			} finally {
				socketChannel.close();
			}

		} finally {
			recordReceiver.close();
		}

		PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile, true), 8192 * 8), false, "UTF-8");
		try {
			for (long timing : kiekerLoadDriver.timings) {
				ps.println("0;" + timing);
			}
		} finally {
			ps.close();
		}
	}
}
