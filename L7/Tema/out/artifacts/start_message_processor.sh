java -jar ./MessageProcessorMicroservice_jar/MessageProcessorMicroservice.jar&
messageProcessor=$!
wait $messageProcessor
