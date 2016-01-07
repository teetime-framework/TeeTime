java ^
	-javaagent:kieker-1.11-aspectj.jar ^
	-cp .;target\*;target\dependency\*; ^
	teetime.examples.wordcounter.WordCounterTest ^
	4 1 target\classes\hugetext.txt