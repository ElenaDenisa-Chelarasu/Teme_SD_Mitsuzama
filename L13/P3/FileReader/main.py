import kafka

from re import findall

if __name__ == '__main__':
    text = open('economics_to_be_happier.txt', 'r').read()
    word_regex = r'[a-zA-Z]+'
    words = [it.lower() for it in findall(word_regex, text)]
    producer = kafka.KafkaProducer()
    for word in findall(word_regex, text):
        producer.send(
            topic='lab13p3',
            value=word.lower().encode()
        )
        producer.flush()
    producer.close()
