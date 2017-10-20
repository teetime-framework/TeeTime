$content = Get-Content lorem_ipsum.txt

for ($i=1; $i -le 1000; $i++) {
	Add-Content huge_textfile.txt $content
}
