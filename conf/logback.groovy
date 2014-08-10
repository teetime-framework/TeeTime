statusListener(OnConsoleStatusListener)

root(WARN)

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

//logger "teetime.variant.methodcallWithPorts.stage", DEBUG, ["CONSOLE"]