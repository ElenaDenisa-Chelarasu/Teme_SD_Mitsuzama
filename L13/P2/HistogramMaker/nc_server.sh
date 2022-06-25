while read x; do echo "$x" | nc localhost 9999; done < lines.txt
