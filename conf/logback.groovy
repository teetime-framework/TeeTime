statusListener(OnConsoleStatusListener)

/*appender("FILE", FileAppender) {
  file = "testFile.log"
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = "%level %logger - %msg%n"
  }
}
*/

appender("CONSOLE", ConsoleAppender) {
  append = true
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} %level %logger - %msg%n"
  }
}

root WARN, ["CONSOLE"]

//logger "teetime.variant.methodcallWithPorts.stage", DEBUG, ["CONSOLE"]
logger "teetime.variant.methodcallWithPorts.stage", INFO