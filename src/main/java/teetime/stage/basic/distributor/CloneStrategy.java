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
package teetime.stage.basic.distributor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import teetime.framework.OutputPort;

/**
 * @author Nils Christian Ehmke
 *
 * @since 1.0
 */
public final class CloneStrategy implements IDistributorStrategy {

	@Override
	public <T> boolean distribute(final OutputPort<T>[] outputPorts, final T element) {
		for (final OutputPort<T> outputPort : outputPorts) {
			outputPort.send(clone(element));
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private static <T> T clone(final T element) {
		try {
			final T newInstance = (T) element.getClass().newInstance();

			final Collection<Method> setters = findSetters(element.getClass());
			final Collection<Method> getters = findGetters(element.getClass());

			for (Method setter : setters) {
				final Method getter = findCorrespondingGetter(setter, getters);
				if (getter != null) {
					setter.invoke(newInstance, getter.invoke(element, new Object[0]));
				}
			}

			return newInstance;
		} catch (InstantiationException e) {
			throw new UnsupportedOperationException();
		} catch (IllegalAccessException e) {
			throw new UnsupportedOperationException();
		} catch (IllegalArgumentException e) {
			throw new UnsupportedOperationException();
		} catch (InvocationTargetException e) {
			throw new UnsupportedOperationException();
		}
	}

	private static Collection<Method> findSetters(final Class<?> clazz) {
		final List<Method> methods = new ArrayList<Method>();

		for (Method method : clazz.getMethods()) {
			if (method.getReturnType() == Void.TYPE) {
				if (method.getParameterCount() == 1) {
					if (method.getName().matches("set[A-Z].*")) {
						methods.add(method);
					}
				}
			}
		}

		return methods;
	}

	private static Collection<Method> findGetters(final Class<?> clazz) {
		final List<Method> methods = new ArrayList<Method>();

		for (Method method : clazz.getMethods()) {
			if (method.getReturnType() != Void.TYPE) {
				if (method.getParameterCount() == 0) {
					if (method.getName().matches("get[A-Z].*") || method.getName().matches("is[A-Z].*")) {
						methods.add(method);
					}
				}
			}
		}

		return methods;
	}

	private static Method findCorrespondingGetter(final Method setter, final Collection<Method> getters) {
		final String attributeName = setter.getName().substring(3);
		for (Method getter : getters) {
			if (getter.getReturnType() == setter.getParameterTypes()[0]) {
				if (getter.getName().matches("get" + attributeName) || getter.getName().matches("is" + attributeName)) {
					return getter;
				}
			}
		}

		return null;
	}

}
