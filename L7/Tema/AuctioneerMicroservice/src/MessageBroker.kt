import io.reactivex.rxjava3.core.Observable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

class MessageBroker {
    val bidderConnections=mutableListOf<Socket>()
    var auctioneerSocket:Socket
    val messageBrokerSocket:ServerSocket
    val receiveBidsObservable: Observable<String>

    companion object Constants {
        const val MESSAGE_BROKER_PORT = 1400
        const val AUCTIONEER_HOST = "localhost"
        const val AUCTIONEER_PORT = 1500
    }

    init {
        auctioneerSocket=Socket(AUCTIONEER_HOST, AUCTIONEER_PORT)
        messageBrokerSocket=ServerSocket(MESSAGE_BROKER_PORT)
        // se creeaza obiectul Observable cu care se genereaza evenimente cand se primesc oferte de la bidderi
        receiveBidsObservable = Observable.create<String> { emitter ->
            // se asteapta conexiuni din partea bidderilor
            while (true) {
                try {
                    val bidderConnection = messageBrokerSocket.accept()
                    bidderConnections.add(bidderConnection)

                    // se citeste mesajul de la bidder de pe socketul TCP
                    val bufferReader = BufferedReader(InputStreamReader(bidderConnection.inputStream))
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        bufferReader.close()
                        bidderConnection.close()

                        emitter.onError(Exception("Eroare: Bidder-ul ${bidderConnection.port} a fost deconectat."))
                    }

                    // se emite ce s-a citit ca si element in fluxul de mesaje
                    emitter.onNext(receivedMessage)
                } catch (e: SocketTimeoutException) {
                    // daca au trecut cele 15 secunde de la pornirea licitatiei, inseamna ca licitatia s-a incheiat
                    // se emite semnalul Complete pentru a incheia fluxul de oferte
                    emitter.onComplete()
                    break
                }
            }
        }
    }
}

fun main(){
    MessageBroker()
}