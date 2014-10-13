#!/bin/sh

java=~/jdk1.7.0_60/bin/java
cp=.:teetime.jar:lib/kieker-1.9_aspectj.jar
jvmParams=""
params=

JAVAARGS="-server"
JAVAARGS="${JAVAARGS} -d64"
JAVAARGS="${JAVAARGS} -Xms1G -Xmx1G"
JAVAARGS="${JAVAARGS} -verbose:gc -XX:+PrintCompilation"

${java} ${JAVAARGS} ${jvmParams} -cp ${cp} teetime.variant.methodcallWithPorts.examples.traceReconstructionWithThreads.ChwWorkTcpTraceReconstructionAnalysisWithThreadsTest ${params}