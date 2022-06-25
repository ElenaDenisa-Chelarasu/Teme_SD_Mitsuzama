from kafka import KafkaAdminClient
from kafka import KafkaConsumer
from kafka.admin import NewTopic
import time

if __name__ == '__main__':
    admin = KafkaAdminClient()

    used_topics = (
        "topic_oferte",
        "topic_rezultat",
        "topic_oferte_procesate",
        "topic_notificare_procesor_mesaje",
    )

    # se sterg topic-urile, daca exista deja
    print("Se sterg topic-urile existente...")

    kafka_topics = KafkaConsumer().topics()
    for topic in kafka_topics:
        if topic in used_topics:
            print("\tSe sterge {}...".format(topic))
            admin.delete_topics(topics=[topic], timeout_ms=2000)

            # se asteapta putin ca stergerea sa aiba loc
            time.sleep(2)

    # se creeaza topic-urile necesare aplicatiei
    print("Se creeaza topic-urile necesare:")
    lista_topicuri = [
        NewTopic(name=used_topics[0], num_partitions=4, replication_factor=1),
        NewTopic(name=used_topics[1], num_partitions=1, replication_factor=1),
        NewTopic(name=used_topics[2], num_partitions=1, replication_factor=1),
        NewTopic(name=used_topics[3], num_partitions=1, replication_factor=1)
    ]
    for topic in lista_topicuri:
        print("\t{}".format(topic.name))
    admin.create_topics(lista_topicuri, timeout_ms=3000)

    print("Gata! Microserviciile participante la licitatie pot fi pornite.")
