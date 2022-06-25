java -jar ./BiddingProcessorMicroservice_jar/BiddingProcessorMicroservice.jar&
biddingProcessor=$!
wait $biddingProcessor
