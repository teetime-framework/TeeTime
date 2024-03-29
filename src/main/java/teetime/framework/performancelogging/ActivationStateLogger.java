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
package teetime.framework.performancelogging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import teetime.framework.AbstractStage;
import teetime.framework.performancelogging.formatstrategy.CumulativeActivePassivTime;

/**
 * This class serves as storage for information about active and inactive times
 * of objects.
 *
 * @author Adrian
 *
 */
public class ActivationStateLogger {

	/**
	 * Singleton Instance Holder
	 */
	private static final ActivationStateLogger INSTANCE = new ActivationStateLogger();
	/**
	 * Set of registered Stages
	 */
	private final Set<AbstractStage> stages = new LinkedHashSet<>();
	/**
	 * An Integer that holds the longest of all registered simple Stage names.
	 */
	private int longestName = 0;

	private IFormatingStrategy formatingStrategy = new CumulativeActivePassivTime(stages);

	private ActivationStateLogger() {
		// singleton
	}

	public static ActivationStateLogger getInstance() {
		return ActivationStateLogger.INSTANCE;
	}

	/**
	 * Any stage can register itself here. It will be stored with an empty List of
	 * States.
	 *
	 * @param stage Stage to be registered.
	 */
	public void register(final AbstractStage stage) {
		this.setLongestName(stage.getClass().getSimpleName().length());
		stages.add(stage);
	}

	public void logToFile() throws UnsupportedEncodingException, FileNotFoundException {
		this.logToFile("StateLogs/");
	}

	public void logToFile(final String path) throws UnsupportedEncodingException, FileNotFoundException {
		Calendar now = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat();
		String date = formatter.format(now.getTime()).replace('.', '-').replace(':', '.').replace(' ', '_');

		String filename = "StateLog_" + date + ".txt";
		this.logToFile(path, filename);
	}

	public void logToFile(final String path, final String filename)
			throws UnsupportedEncodingException, FileNotFoundException {
		this.logToFile(new File(path + filename));
	}

	public void logToFile(final File file) throws UnsupportedEncodingException, FileNotFoundException {
		try {
			this.printToFile(file);
		} catch (IOException e) {
			try {
				if (file.createNewFile()) {
					this.printToFile(file);
				} else {
					System.err.println("File wasn't created!"); // NOPMD
				}
			} catch (IOException ioe) {
				System.err.println("Error creating " + file.toString()); // NOPMD
			}

		}
	}

	private void printToFile(final File file) throws IOException {
//		PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(file, true), 8192 * 8), false, "UTF-8");
		PrintStream ps = new PrintStream(Files.newOutputStream(file.toPath(), StandardOpenOption.APPEND), false,
				"UTF-8");
		ps.print(this);
		ps.close();
		System.out.println("Log saved to File: " + file.getAbsolutePath()); // NOPMD
	}
	// getter and setter:

	public Set<AbstractStage> getStages() {
		return stages;
	}

	public int getLongestName() {
		return longestName;
	}

	public void setLongestName(final int longestName) {
		if (longestName > this.longestName) {
			this.longestName = longestName;
		}
	}

	public IFormatingStrategy getFormatingStrategy() {
		return formatingStrategy;
	}

	public void setFormatingStrategy(final IFormatingStrategy formatingStrategy) {
		this.formatingStrategy = formatingStrategy;
	}

	// toString and inner classes
	@Override
	public String toString() {
		String result = "";

		result += this.formatingStrategy.formatData();
		return result;
	}

	public interface IFormatingStrategy {
		public String formatData();
	}

}
