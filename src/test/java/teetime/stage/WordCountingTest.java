package teetime.stage;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import teetime.framework.Analysis;
import teetime.stage.util.CountingMap;

public class WordCountingTest {

	@Test
	public void test1() {
		WordCountingConfiguration wcc = new WordCountingConfiguration(3, new File("src/test/resources/data/output.txt"), new File(
				"src/test/resources/data/output.txt"));
		Analysis analysis = new Analysis(wcc);
		analysis.start();
		CountingMap<String> map = wcc.getResult();
		for (Map.Entry<String, Integer> entry : map.entrySet())
		{
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
}

// 56 et
// 31 dolor
// 27 sed
// 27 diam
// 26 sit
// 26 lorem
// 26 ipsum
// 26 amet
// 25 dolore
// 24 ut
// 16 vero
// 16 magna
// 16 erat
// 16 ea
// 16 at
// 14 tempor
// 12 takimata
// 12 stet
// 12 sea
// 12 sanctus
// 12 sadipscing
// 12 rebum
// 12 nonumy
// 12 no
// 12 labore
// 12 kasd
// 12 justo
// 12 invidunt
// 12 gubergren
// 12 eos
// 12 elitr
// 12 eirmod
// 12 duo
// 12 dolores
// 12 consetetur
// 12 clita
// 12 aliquyam
// 12 accusam
// 11 voluptua
// 11 est
// 10 vel
// 10 in
// 9 nulla
// 9 duis
// 8 consequat
// 5 vulputate
// 5 velit
// 5 molestie
// 5 iriure
// 5 illum
// 5 hendrerit
// 5 feugiat
// 5 facilisis
// 5 eum
// 5 eu
// 5 esse
// 5 autem
// 4 zzril
// 4 wisi
// 4 volutpat
// 4 veniam
// 4 ullamcorper
// 4 tincidunt
// 4 te
// 4 tation
// 4 suscipit
// 4 quis
// 4 qui
// 4 praesent
// 4 odio
// 4 nostrud
// 4 nonummy
// 4 nisl
// 4 nibh
// 4 minim
// 4 luptatum
// 4 lobortis
// 4 laoreet
// 4 iusto
// 4 feugait
// 4 facilisi
// 4 exerci
// 4 ex
// 4 euismod
// 4 eros
// 4 enim
// 4 elit
// 4 dignissim
// 4 delenit
// 4 consectetuer
// 4 commodo
// 4 blandit
// 4 augue
// 4 aliquip
// 4 aliquam
// 4 adipiscing
// 4 ad
// 4 accumsan
// 2 soluta
// 2 quod
// 2 possim
// 2 placerat
// 2 option
// 2 nobis
// 2 nihil
// 2 nam
// 2 mazim
// 2 liber
// 2 imperdiet
// 2 id
// 2 facer
// 2 eleifend
// 2 doming
// 2 cum
// 2 congue
// 2 assum
