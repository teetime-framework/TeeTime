import ch.qos.logback.classic.filter.ThresholdFilter

statusListener(OnConsoleStatusListener)

appender("FILE", FileAppender) {
  file = "src/test/data/load-logs/timings-results.txt"
  append = false
  filter(ThresholdFilter) {
	level = INFO
  }
  encoder(PatternLayoutEncoder) {
    pattern = "%msg%n"
  }
}

appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} %level %logger - %msg%n"
  }
}

//root WARN, ["CONSOLE"]
root WARN

//logger "teetime.variant.methodcallWithPorts.stage", DEBUG, ["CONSOLE"]
logger "teetime.variant.methodcallWithPorts.stage", INFO

logger "teetime.variant.methodcallWithPorts.examples.kiekerdays.TimingsReader", TRACE, ["CONSOLE", "FILE"]