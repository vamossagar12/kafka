#!/bin/bash
while :
do
	echo "Writing to topic 1"
	echo "matrix":{\"title\":\"The Matrix\",\"year\":1999,\"cast\":[\"Keanu Reeves\",\"Laurence Fishburne\",\"Carrie-Anne Moss\",\"Hugo Weaving\",\"Joe Pantoliano\"],\"genres\":[\"Science Fiction\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic blogpost-1 --property "parse.key=true" --property "key.separator=:"
	echo "die_hard":{\"title\":\"Die Hard\",\"year\":1988,\"cast\":[\"Bruce Willis\",\"Alan Rickman\",\"Bonnie Bedelia\",\"William Atherton\",\"Paul Gleason\",\"Reginald VelJohnson\",\"Alexander Godunov\"],\"genres\":[\"Action\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic blogpost-1 --property "parse.key=true" --property "key.separator=:"
	echo "toy_story":{\"title\":\"Toy Story\",\"year\":1995,\"cast\":[\"Tim Allen\",\"Tom Hanks\"],\"genres\":[\"Animated\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic blogpost-1 --property "parse.key=true" --property "key.separator=:"
	echo "jp"{\"title\":\"Jurassic Park\",\"year\":1993,\"cast\":[\"Sam Neill\",\"Laura Dern\",\"Jeff Goldblum\",\"Richard Attenborough\"],\"genres\":[\"Adventure\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic blogpost-1 --property "parse.key=true" --property "key.separator=:"
	echo "lotr":{\"title\":\"The Lord of the Rings: The Fellowship of the Ring\",\"year\":2001,\"cast\":[\"Elijah Wood\",\"Ian McKellen\",\"Liv Tyler\",\"Sean Astin\",\"Viggo Mortensen\",\"Orlando Bloom\",\"Sean Bean\",\"Hugo Weaving\",\"Ian Holm\"],\"genres\":[\"Fantasy\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic blogpost-1 --property "parse.key=true" --property "key.separator=:"

	echo "Writing to topic 2"
	echo "matrix":{\"title\":\"The Matrix\",\"year\":1999,\"cast\":[\"Keanu Reeves\",\"Laurence Fishburne\",\"Carrie-Anne Moss\",\"Hugo Weaving\",\"Joe Pantoliano\"],\"genres\":[\"Science Fiction\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic movies-1 --property "parse.key=true" --property "key.separator=:"
	echo "dh":{\"title\":\"Die Hard\",\"year\":1988,\"cast\":[\"Bruce Willis\",\"Alan Rickman\",\"Bonnie Bedelia\",\"William Atherton\",\"Paul Gleason\",\"Reginald VelJohnson\",\"Alexander Godunov\"],\"genres\":[\"Action\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic movies-1 --property "parse.key=true" --property "key.separator=:"
	echo "ts":{\"title\":\"Toy Story\",\"year\":1995,\"cast\":[\"Tim Allen\",\"Tom Hanks\"],\"genres\":[\"Animated\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic movies-1 --property "parse.key=true" --property "key.separator=:"
	echo "jp":{\"title\":\"Jurassic Park\",\"year\":1993,\"cast\":[\"Sam Neill\",\"Laura Dern\",\"Jeff Goldblum\",\"Richard Attenborough\"],\"genres\":[\"Adventure\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic movies-1 --property "parse.key=true" --property "key.separator=:"
	echo "lotr":{\"title\":\"The Lord of the Rings: The Fellowship of the Ring\",\"year\":2001,\"cast\":[\"Elijah Wood\",\"Ian McKellen\",\"Liv Tyler\",\"Sean Astin\",\"Viggo Mortensen\",\"Orlando Bloom\",\"Sean Bean\",\"Hugo Weaving\",\"Ian Holm\"],\"genres\":[\"Fantasy\"]} | ./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic movies-1 --property "parse.key=true" --property "key.separator=:"

	echo "sleeping for 1 second"
	sleep 1
done
