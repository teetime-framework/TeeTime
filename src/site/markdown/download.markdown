# Download

In order to work with TeeTime, you simply need to add the framework to your own project.

TeeTime is distributed through a variety of channels to serve most needs.

## Latest stable

There are two simple ways to add TeeTime to your own project.

#### As dependency

TeeTime is available through Maven's central repository.

If you use Maven, simply add following lines to your ``pom.xml``:

```xml
<dependency>
	<groupId>net.sourceforge.teetime</groupId>
	<artifactId>teetime</artifactId>
	<version>${teetime.stableversion}</version>
</dependency>
```

For other project management tools, please check [here](nomaven.html). 

#### Binaries and sources

The latest TeeTime release can be downloaded directly from [Sourceforge](https://sourceforge.net/projects/teetime/files/latest/download). If you also want to download the source and/or JavaDoc, you can also get a summary of available files on [Sourceforge](https://sourceforge.net/projects/teetime/files/) or [the Central Repository](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22net.sourceforge.teetime%22%20AND%20a%3A%22teetime%22).

## Snapshot

TeeTime is built upon a daily basis. All these nightly builds are accessible to the public. A documentation is provided [here](apidocs/index.html).

Please do not forget, that this releases are not intended for daily use. These builds may be unstable.

#### As dependency 

All snapshot builds will be deployed to Sonatype's snapshot repository.

To download these builds, add following lines to your project's ``pom.xml``: 

```xml
<dependency>
	<groupId>net.sourceforge.teetime</groupId>
  	<artifactId>teetime</artifactId>
  	<version>${teetime.snapshotversion}</version>
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

## Logo

The TeeTime Logo is licensed under a [Creative Commons Attribution 4.0 International](http://creativecommons.org/licenses/by/4.0/) License. Feel free to use it, as long as you provide a reference to this site.

Available formats:

* PNG, transparent: [641 x 150px](images/teetime-transparent-150.png), [427 x 100px](images/teetime-transparent-100.png), [214 x 50px](images/teetime-transparent-50.png)
* PNG, white background: [641 x 150px](images/teetime-white-150.png), [427 x 100px](images/teetime-white-100.png), [214 x 50px](images/teetime-white-50.png)
* SVG: [transparent](images/teetime-path.svg)
* PDF:

