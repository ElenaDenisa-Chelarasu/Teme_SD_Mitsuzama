java -jar ./AuctioneerMicroservice_jar/AuctioneerMicroservice.jar&
auctioneer=$!

wait $auctioneer
