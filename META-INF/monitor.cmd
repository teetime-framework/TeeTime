java ^
	-javaagent:kieker-1.13-SNAPSHOT-aspectj.jar ^
	-cp ".;target\*;target\dependency\*;" ^
	teetime.examples.cipher.CipherTest ^
	4 1 target\classes\hugetext.txt

rem teetime.examples.wordcounter.WordCounterTest