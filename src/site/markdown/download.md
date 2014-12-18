# Download

There are several ways to get TeeTime. Choose the best one for you.

## Latest stable

There are two simple ways to add TeeTime to your own project.

>Please note, that TeeTime's first official release isn't published yet. You can switch to the snapshot release or wait a couple of days. We are looking forward, to release 1.0 still this month.

#### As dependency

TeeTime is available through Maven's central repository.

If you use Maven, simply add following lines to your ``pom.xml``:

```xml
<dependency>
	<groupId>net.sourceforge.teetime</groupId>
	<artifactId>teetime</artifactId>
	<version>1.0</version>
</dependency>
```

<!--- Dependency informations for other project management tools can be found [here](dependency-info.html). -->
Please make sure, your project management tool uses Maven's central repository. Help on how to add it, can be found [here](http://central.sonatype.org/pages/consumers.html).

#### Binaries and sources

The latest TeeTime release can be downloaded directly from [Sourceforge](https://sourceforge.net/projects/teetime/files/latest/download). If you also want to download the source and/or JavaDoc, you can also get a summary of available files on [Sourceforge](https://sourceforge.net/projects/teetime/files/). Alternatively, you can also download all files from the [CI Server](https://build.se.informatik.uni-kiel.de/jenkins/view/TeeTime/job/teetime-release/lastSuccessfulBuild/artifact/target/).

## Snapshot

TeeTime is built upon a daily basis. All these nightly builds are accessible to the public.

Please do not forget, that this releases are not intended for daily use. These builds may be unstable.

#### As dependency 

All snapshot builds will be deployed to Sonatype's snapshot repository.

To download these builds, add following lines to your project's ``pom.xml``: 

```xml
<dependency>
	<groupId>net.sourceforge.teetime</groupId>
  	<artifactId>teetime</artifactId>
  	<version>1.0-SNAPSHOT</version>
</dependency>
```

If you did not add the Sonatype snapshot repository yet to your ``pom.xml``, add also following lines to it, as otherwise Maven will not be able to find the needed artifacts:

```xml
<repositories>
	<repository>
		<id>sonatype.oss.snapshots</id>
		<url>https://oss.sonatype.org/content/repositories/snapshots/</url>
	</repository>
</repositories>
```


#### Binaries and sources

All nightly builds can be found on [Sonatype's repository](https://oss.sonatype.org/content/repositories/snapshots/net/sourceforge/teetime/teetime/) or our [CI Server](https://build.se.informatik.uni-kiel.de/jenkins/view/TeeTime/job/teetime-nighly-release/lastSuccessfulBuild/artifact/target/).

## Release notes

All changes can be found on our [release notes](changes-report.html) page.

