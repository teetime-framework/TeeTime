/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.stage.basic.distributor.strategy;

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

	@SuppressWarnings("unchecked")
	@Override
	public <T> boolean distribute(final List<OutputPort<?>> outputPorts, final T element) {
		for (final OutputPort<?> outputPort : outputPorts) {
			T clonedElement = clone(element);
			((OutputPort<T>) outputPort).send(clonedElement);
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
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	private static Collection<Method> findSetters(final Class<?> clazz) {
		final List<Method> methods = new ArrayList<Method>();

		for (Method method : clazz.getMethods()) {
			if (method.getReturnType() == Void.TYPE) {
				if (method.getParameterTypes().length == 1) {
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
				if (method.getParameterTypes().length == 0) {
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

	@Override
	public void onPortRemoved(final OutputPort<?> removedOutputPort) {
		// do nothing
	}

}
