#!/bin/bash
i=0

bidders=()
while [ $i -lt $1 ]
do
	java -jar ./BidderMicroservice_jar/BidderMicroservice.jar&
	bidders+=($!)
	i=$(( $i + 1 ))
done

for bidder in ${bidders[@]}
do
	wait $bidder
done
