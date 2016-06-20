## Dependency Information

Please make sure you use Maven’s central repository. Help on how to add it, can be found (http://central.sonatype.org/pages/consumers.html)[here].

### Apache Buildr

```
'net.sourceforge.teetime:teetime:jar:${teetime.stableversion}'
```

### Apache Ivy

```
<dependency org="net.sourceforge.teetime" name="teetime" rev="${teetime.stableversion}">
  <artifact name="teetime" type="jar" />
</dependency>
```

### Groovy Grape

```
@Grapes(
@Grab(group='net.sourceforge.teetime', module='teetime', version='${teetime.stableversion}')
)
```

### Grails

```
compile 'net.sourceforge.teetime:teetime:${teetime.stableversion}'
```

### Leiningen

```
[net.sourceforge.teetime/teetime "${teetime.stableversion}"]
```

### SBT

```
libraryDependencies += "net.sourceforge.teetime" % "teetime" % "${teetime.stableversion}"
```