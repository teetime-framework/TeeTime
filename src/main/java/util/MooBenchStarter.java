package util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MooBenchStarter {

	private final File execDir;

	public MooBenchStarter() {
		this.execDir = new File("scripts/MooBench-cmd");
		System.out.println("execDir: " + this.execDir.getAbsolutePath());
	}

	public void start(final int runs, final long calls) throws IOException {
		List<String> command = new LinkedList<String>();
		command.add("cmd");
		command.add("/c");
		command.add("start");
		command.add("/D");
		command.add(this.execDir.getAbsolutePath());
		command.add("Load Driver");
		command.add("startMooBench.cmd");
		command.add(String.valueOf(runs));
		command.add(String.valueOf(calls));

		new ProcessBuilder(command).start();
	}
}
