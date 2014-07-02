
set cp=.;MooBench.jar;META-INF/kieker.monitoring.properties;META-INF/kieker.logging.properties
set jvmParams=-javaagent:lib/kieker-1.9_aspectj.jar -Dorg.aspectj.weaver.loadtime.configuration=META-INF/kieker.aop.xml -Dorg.aspectj.weaver.showWeaveInfo=true -Daj.weaving.verbose=true -Dkieker.monitoring.writer=kieker.monitoring.writer.tcp.TCPWriter
set params=-d 10 -h 1 -m 0 -t 3000000 -o tmp/test.txt -q
set runs=%1

for %%i in (%runs%) do (
	java -cp %cp% %jvmParams% mooBench.benchmark.Benchmark %params%
)