package teetime.variant.methodcallWithPorts.runtime.typeCheck;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.TypeVariable;

import org.junit.Test;

import teetime.util.ConstructorClosure;
import teetime.variant.explicitScheduling.examples.throughput.TimestampObject;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.pipe.IPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.ThreadCommunication;
import teetime.variant.methodcallWithPorts.stage.ObjectProducer;
import teetime.variant.methodcallWithPorts.stage.PortTypeConfiguration;
import teetime.variant.methodcallWithPorts.stage.StartTimestampFilter;
import teetime.variant.methodcallWithPorts.stage.StopTimestampFilter;
import teetime.variant.methodcallWithPorts.stage.basic.Sink;

public class ConnectionTypeTest {

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
		StartTimestampFilter startTimestampFilter = StartTimestampFilter.class.newInstance();
		StopTimestampFilter stopTimestampFilter = StopTimestampFilter.class.newInstance();
		Sink sink = Sink.class.newInstance();

		PipeFactory pipeFactory = new PipeFactory();

		IPipe pipe = pipeFactory.create(ThreadCommunication.INTRA);
		pipe.connectPorts(objectProducer.getOutputPort(), startTimestampFilter.getInputPort());
		pipe = pipeFactory.create(ThreadCommunication.INTRA);
		pipe.connectPorts(startTimestampFilter.getOutputPort(), stopTimestampFilter.getInputPort());
		pipe = pipeFactory.create(ThreadCommunication.INTRA);
		pipe.connectPorts(stopTimestampFilter.getOutputPort(), sink.getInputPort());

		/*
		 * requirements:
		 * <ul>
		 * <li>when selecting a stage class to create one instance, the user is prompted to declare each type argument
		 * <li>
		 * </ul>
		 */

		TypeVariable<Class<ObjectProducer>>[] objectProducerTypeParameters = ObjectProducer.class.getTypeParameters();
		for (TypeVariable<Class<ObjectProducer>> typeVariable : objectProducerTypeParameters) {
			System.out.println(typeVariable.getBounds()); // ->[Ljava.lang.reflect.Type;@13a65d1f
			System.out.println(typeVariable.getBounds().length); // ->1
			System.out.println(typeVariable.getBounds()[0]); // ->class java.lang.Object
			System.out.println(typeVariable.getName()); // ->T
			System.out.println(typeVariable.getClass()); // ->class sun.reflect.generics.reflectiveObjects.TypeVariableImpl
			System.out.println(typeVariable.getGenericDeclaration()); // ->class teetime.variant.methodcallWithPorts.stage.ObjectProducer
		}

		// TypeVariable<?>[] objectProducerOutputPortTypeParameters = objectProducer.getOutputPort().getClass().getTypeParameters();
		// for (TypeVariable<?> typeVariable : objectProducerOutputPortTypeParameters) {
		// System.out.println(typeVariable.getBounds()); // ->[Ljava.lang.reflect.Type;@20a12d8f
		// System.out.println(typeVariable.getBounds().length); // ->1
		// System.out.println(typeVariable.getBounds()[0]); // ->class java.lang.Object
		// System.out.println(typeVariable.getName()); // ->T
		// System.out.println(typeVariable.getClass()); // ->class sun.reflect.generics.reflectiveObjects.TypeVariableImpl
		// System.out.println(typeVariable.getGenericDeclaration()); // ->class teetime.variant.methodcallWithPorts.framework.core.OutputPort
		// }
		//
		// TypeVariable<?>[] startTimestampFilterOutputPortTypeParameters = startTimestampFilter.getOutputPort().getClass().getTypeParameters();
		// for (TypeVariable<?> typeVariable : startTimestampFilterOutputPortTypeParameters) {
		// System.out.println(typeVariable.getBounds()); // ->[Ljava.lang.reflect.Type;@7b365f02
		// System.out.println(typeVariable.getBounds().length); // ->1
		// System.out.println(typeVariable.getBounds()[0]); // ->class java.lang.Object
		// System.out.println(typeVariable.getName()); // ->T
		// System.out.println(typeVariable.getClass()); // ->class sun.reflect.generics.reflectiveObjects.TypeVariableImpl
		// System.out.println(typeVariable.getGenericDeclaration()); // ->class teetime.variant.methodcallWithPorts.framework.core.OutputPort
		// }

		Class<?> currentClass = objectProducer.getClass();
		while (currentClass.getSuperclass() != null) { // we don't want to process Object.class
			Field[] fields = currentClass.getDeclaredFields();
			for (Field field : fields) {
				// System.out.println("Field: " + field.getType());
				if (OutputPort.class.equals(field.getType())) {
					System.out.println("Field.name: " + field.getName());
					System.out.println("Field.type: " + field.getType());
					// field.getType()
				}
			}

			currentClass = currentClass.getSuperclass();
		}

		System.out.println(objectProducer.getOutputPort().getType());
		PortTypeConfiguration.setPortTypes(objectProducer, Class.forName("teetime.variant.explicitScheduling.examples.throughput.TimestampObject"));
		System.out.println(objectProducer.getOutputPort().getType());
	}
}
