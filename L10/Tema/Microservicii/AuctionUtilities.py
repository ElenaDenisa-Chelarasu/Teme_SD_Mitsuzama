from functional import seq
from kafka import KafkaAdminClient
from kafka import KafkaConsumer
from kafka.admin import NewTopic
import time


def create_topics(*used_topics):
    admin = KafkaAdminClient()

    kafka_topics = KafkaConsumer().topics()
    # initial nu exista topicuri care sa inceapa cu used_topic deci se adauga used_topic0 pentru ca urmatoare
    # instructiuni sa functioneze
    for used_topic in used_topics:
        kafka_topics.add(f'{used_topic}0')
    kafka_topics = seq(kafka_topics)

    # used_topics
    # -> [(used_topic, used_topicn)]
    # -> [(used_topic, used_topicN)], N asa incat oricare ar fi n, n<=N
    # -> [(used_topic, N)]
    # -> {used_topic: N}

    used_topics_to_max_index = seq(used_topics) \
        .map(lambda used_topic: (used_topic, kafka_topics.filter(lambda topic: topic.startswith(used_topic)))) \
        .map(lambda used_topic: (used_topic[0], seq(used_topic[1])
                                 .max_by(lambda it: int(it.replace(used_topic[0], ''))))) \
        .map(lambda it: (it[0], int(it[1].replace(it[0], '')))) \
        .to_dict()

    # se creeaza topic-urile necesare aplicatiei
    print("Se creeaza topic-urile necesare:")
    new_topics_names = [f'{topic}{index + 1}' for (topic, index) in used_topics_to_max_index.items()]
    lista_topicuri = [NewTopic(name=name, num_partitions=1, replication_factor=1) for name in new_topics_names]

    for topic in lista_topicuri:
        print(f'\t{topic.name}')
    admin.create_topics(lista_topicuri, timeout_ms=3000)

    print("Gata! Microserviciile participante la licitatie pot fi pornite.")
    return new_topics_names


def reset_topics():
    admin = KafkaAdminClient()

    used_topics = (
        "start_bid_topic",
        "bid_topic",
        "result_topic",
    )

    # se sterg topic-urile de forma used_topicN
    print("Se sterg topic-urile existente...")

    kafka_topics = KafkaConsumer().topics()
    for topic in kafka_topics:
        for used_topic in used_topics:
            if topic.startswith(used_topic):
                print(f'\tSe sterge {topic}...')
                admin.delete_topics(topics=[topic], timeout_ms=2000)
                # se asteapta putin ca stergerea sa aiba loc
                time.sleep(2)


if __name__ == '__main__':
    reset_topics()
