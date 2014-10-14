package teetime.runtime.typeCheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.ObjectProducer;
import teetime.stage.PortTypeConfiguration;
import teetime.stage.StartTimestampFilter;
import teetime.stage.StopTimestampFilter;
import teetime.stage.basic.Sink;
import teetime.util.ConstructorClosure;
import teetime.util.TimestampObject;

public class ConnectionTypeTest {

	private final PipeFactoryRegistry pipeFactory = PipeFactoryRegistry.INSTANCE;

	// tests for load-time validation

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testDynamicPortConnection() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		ConstructorClosure<TimestampObject> constructorClosure = new ConstructorClosure<TimestampObject>() {
			@Override
			public TimestampObject create() {
				return new TimestampObject();
			}

		};

		Constructor<ObjectProducer> constructor = ObjectProducer.class.getConstructor(long.class, ConstructorClosure.class);

		ObjectProducer objectProducer = constructor.newInstance(1, constructorClosure);
		// PortTypeConfiguration.setPortTypes(objectProducer, Class.forName("teetime.variant.explicitScheduling.examples.throughput.TimestampObject"));

		StartTimestampFilter startTimestampFilter = StartTimestampFilter.class.newInstance();
		StopTimestampFilter stopTimestampFilter = StopTimestampFilter.class.newInstance();
		Sink sink = Sink.class.newInstance();

		IPipeFactory factory = this.pipeFactory.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.QUEUE_BASED, true);
		factory.create(objectProducer.getOutputPort(), startTimestampFilter.getInputPort());
		factory.create(startTimestampFilter.getOutputPort(), stopTimestampFilter.getInputPort());
		factory.create(stopTimestampFilter.getOutputPort(), sink.getInputPort());

		// TypeVariable<Class<ObjectProducer>>[] objectProducerTypeParameters = ObjectProducer.class.getTypeParameters();
		// for (TypeVariable<Class<ObjectProducer>> typeVariable : objectProducerTypeParameters) {
		// System.out.println(typeVariable.getBounds()); // ->[Ljava.lang.reflect.Type;@13a65d1f
		// System.out.println(typeVariable.getBounds().length); // ->1
		// System.out.println(typeVariable.getBounds()[0]); // ->class java.lang.Object
		// System.out.println(typeVariable.getName()); // ->T
		// System.out.println(typeVariable.getClass()); // ->class sun.reflect.generics.reflectiveObjects.TypeVariableImpl
		// System.out.println(typeVariable.getGenericDeclaration()); // ->class teetime.stage.ObjectProducer
		// }
		//
		// Class<?> currentClass = objectProducer.getClass();
		// while (currentClass.getSuperclass() != null) { // we don't want to process Object.class
		// Field[] fields = currentClass.getDeclaredFields();
		// for (Field field : fields) {
		// // System.out.println("Field: " + field.getType());
		// if (OutputPort.class.equals(field.getType())) {
		// System.out.println("Field.name: " + field.getName());
		// System.out.println("Field.type: " + field.getType());
		// // field.getType()
		// }
		// }
		//
		// currentClass = currentClass.getSuperclass();
		// }

		assertNull(objectProducer.getOutputPort().getType());
		PortTypeConfiguration.setPortTypes(objectProducer, Class.forName(TimestampObject.class.getName()));
		assertEquals(TimestampObject.class, objectProducer.getOutputPort().getType());

		assertNull(startTimestampFilter.getOutputPort().getType());
		PortTypeConfiguration.setPortTypes(startTimestampFilter);
		assertEquals(TimestampObject.class, startTimestampFilter.getInputPort().getType());
		assertEquals(TimestampObject.class, startTimestampFilter.getOutputPort().getType());
	}
}
