/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
 */
package teetime.framework;

/**
 *
 * @author Christian Wulf (chw)
 *
 * @deprecated since 3.0.
 *             We will completely remove framework-backed support for infinite producers since it has never worked correctly in all (corner) cases.
 *             Instead, please use finite producers and implement an appropriate termination condition by your own.
 */
@Deprecated
public enum TerminationStrategy {

	BY_SIGNAL, BY_SELF_DECISION, BY_INTERRUPT

}
