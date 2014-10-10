#!/bin/bash

java=~/jdk1.7.0_60/bin/java
cp=.:MooBench.jar:META-INF/kieker.monitoring.properties:META-INF/kieker.logging.properties
jvmParams="-javaagent:lib/kieker-1.9_aspectj.jar -Dorg.aspectj.weaver.loadtime.configuration=META-INF/kieker.aop.xml -Dorg.aspectj.weaver.showWeaveInfo=true -Daj.weaving.verbose=true -Dkieker.monitoring.writer=kieker.monitoring.writer.tcp.TCPWriter"
params="-d 10 -h 1 -m 0 -t 1000000 -o tmp/test.txt -q"
#runs=$1

for i in {1..3}; do
	${java} -cp ${cp} ${jvmParams} mooBench.benchmark.Benchmark ${params};
done
