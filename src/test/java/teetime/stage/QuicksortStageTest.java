///**
// * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *         http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package teetime.stage;
//
//import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
//import static org.junit.Assert.assertEquals;
//import static teetime.framework.test.StageTester.test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import org.junit.Test;
//
//import teetime.framework.ConfigurationContext;
//
///**
// * @author Robin Mohr
// */
//public class QuicksortStageTest {
//
//	private final List<Integer> input = generateRandomNumbers(150);
//	private final List<Integer> result = input;
//	private final ConfigurationContext context = null; // FIXME need to provide context for testing environment
//
//	@Test
//	public void quicksortStageShouldSortInputArray() {
//
//		final QuicksortStage qs = new QuicksortStage(context);
//
//		test(qs).and().send(input).to(qs.getInputPort()).and().receive(result)).from(qs.getOutputPort()).start(); //FIXME maybe setupt QS stage not using arrays or modify testing methods
//
//		assertEquals(input, result);
//	}
//
//	private List<Integer> generateRandomNumbers(final int n) {
//
//		List<Integer> list = new ArrayList<Integer>(n);
//		Random random = new Random();
//
//		for (int i = 0; i < n; i++) {
//			list.add(random.nextInt(n * 10));
//		}
//
//		return list;
//	}
// }
