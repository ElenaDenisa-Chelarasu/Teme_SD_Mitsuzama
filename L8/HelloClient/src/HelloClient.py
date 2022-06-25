import socket

if __name__ == "__main__":
    HOST, PORT = "localhost", 1234

    # creare socket TCP
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # conectare la microserviciu (acesta are rol de server si este disponibil pe portul 1234)
    try:
        sock.connect((HOST, PORT))
    except ConnectionError:
        print("Eroare de conectare la microserviciu!")
        exit(1)

    # transmitere mesaj
    print("Trimit mesaj catre microserviciu...")
    sock.send(bytes("Hello from client!", "utf-8"))

    # primire raspuns
    received = str(sock.recv(1024), "utf-8")
    print("Raspuns de la HelloMicroservice: {}".format(received))
