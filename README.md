[![GitHub license](https://img.shields.io/github/license/teetime-framework/TeeTime.svg)](https://github.com/teetime-framework/TeeTime/blob/master/LICENSE.txt)
[![Javadocs](https://javadoc.io/badge/net.sourceforge.teetime/teetime.svg?color=blue)](https://javadoc.io/doc/net.sourceforge.teetime/teetime)

# TeeTime

This project contains the Java reference implementation of the Pipe-and-Filter framework TeeTime.

Informations on how to use it can be found on http://teetime-framework.github.io.

## Getting Started

- [Wiki](https://teetime-framework.github.io/wiki/home.html)
- [Source Code Examples](https://github.com/teetime-framework/TeeTime/tree/master/src/test/java/teetime/examples)

## How to Add TeeTime as Dependency to Your Project?

Please visit the download section on our website: http://teetime-framework.github.io/download.html

## Build TeeTime by Yourself

TeeTime requires an existing Maven and JDK installation.

Simply execute "./gradlew build"

## TeeTime Release Train

- Create a release branch based on the version number with the prefix -RC, e.g., 3.1.2-RC
- Set the `teeTimeVersion` in `gradle.properties` to the release version, e.g., 3.1.2  (without RC)
- Build TeeTime locally, `./gradlew build`

- Ensure that the version/revision is set
- Run this script, if no errors occur, continue
- login to nexus with the credentials below https://oss.sonatype.org/
- Click on "Staging Repositories"
- There should be a new deposit, if not wait a little and reload the page
- If nothing shows up something went horribly wrong or you have your build.gradle
  file still set to publish locally.
- Select the deposit, it should be open if the step went fine you get an email.
- Now click on "close" (button bar above the list of deposits)
- Wait, after a while reload the page
- If closing failed check the issues and fix them, rerun the ./publish.sh
- If it worked, click on "release"

