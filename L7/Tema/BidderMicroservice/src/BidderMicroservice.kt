import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.net.Socket
import kotlin.Exception
import kotlin.random.Random
import kotlin.system.exitProcess

open class BidderMicroservice {
    private var messageBrokerSocket: Socket
    private var auctionResultObservable: Observable<String>
    private var myIdentity: String = "[BIDDER_NECONECTAT]"

    companion object Constants {
        const val MESSAGE_BROKER_HOST = "localhost"
        const val MESSAGE_BROKER_PORT = 1400
        const val MAX_BID = 10_000
        const val MIN_BID = 1_000
    }

    init {
        try {
            messageBrokerSocket = Socket(MESSAGE_BROKER_HOST, MESSAGE_BROKER_PORT)
            println("M-am conectat la MessageBroker!")

            myIdentity = "[${messageBrokerSocket.localPort}]"

            // se creeaza un obiect Observable ce va emite mesaje primite printr-un TCP
            // fiecare mesaj primit reprezinta un element al fluxului de date reactiv
            auctionResultObservable = Observable.create<String> { emitter ->
                // se citeste raspunsul de pe socketul TCP
                val bufferReader = BufferedReader(InputStreamReader(messageBrokerSocket.inputStream))
                val receivedMessage = bufferReader.readLine()

                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    bufferReader.close()
                    messageBrokerSocket.close()

                    emitter.onError(Exception("MessageBroker-ul s-a deconectat."))
                    return@create
                }

                // mesajul primit este emis in flux
                emitter.onNext(receivedMessage)

                // deoarece se asteapta un singur mesaj, in continuare se emite semnalul de incheiere al fluxului
                emitter.onComplete()

                bufferReader.close()
                messageBrokerSocket.close()
            }
        } catch (e: Exception) {
            println("$myIdentity Nu ma pot conecta la MessageBroker!")
            exitProcess(1)
        }
    }

    private fun bid() {
        // se genereaza o oferta aleatorie din partea bidderului curent
        //val pret = Random.nextInt(MIN_BID, MAX_BID)
        val pret = 5000

        // se creeaza mesajul care incapsuleaza oferta
        val biddingMessage = Message.create(createSender(),
            "licitez $pret")

        // bidder-ul trimite pretul pentru care doreste sa liciteze
        val serializedMessage = biddingMessage.serialize()
        sleep(Random.nextLong(3000))
        messageBrokerSocket.getOutputStream().write(serializedMessage)

        // exista o sansa din 2 ca bidder-ul sa-si trimita oferta de 2 ori, eronat
        //if (Random.nextBoolean()) {
        if(true){
            println("Oops, am licitat de 2 ori")
            messageBrokerSocket.getOutputStream().write(serializedMessage)
        }
    }

    protected open fun createSender(): String = "${messageBrokerSocket.localAddress}:${messageBrokerSocket.localPort}"

    protected fun waitForResult() {
        println("$myIdentity Astept rezultatul licitatiei...")
        // bidder-ul se inscrie pentru primirea unui raspuns la oferta trimisa de acesta
        val auctionResultSubscription = auctionResultObservable.subscribeBy(
            // cand se primeste un mesaj in flux, inseamna ca a sosit rezultatul licitatiei
            onNext = {
                val resultMessage: Message = Message.deserialize(it.toByteArray())
                println("$myIdentity Rezultat licitatie: ${resultMessage.body}")
            },
            onError = {
                println("$myIdentity Eroare: $it")
            }
        )

        // se elibereaza memoria obiectului Subscription
        auctionResultSubscription.dispose()
    }

    open fun run() {
        bid()
        waitForResult()
    }
}

open class SpecificBidderMicroservice:BidderMicroservice() {
    private val name: String
    private val telephone: String
    private val email: String

    init {
        val letters = ('a'..'z')+('A'..'Z')
        val numbers = '0'..'9'
        var aux = ""
        (1..Random.nextInt(5,10)).forEach { aux+= letters.random() }
        name=aux
        aux="07"//87xxx876
        (1..8).forEach { aux+=numbers.random() }
        telephone=aux
        aux="@gmail.com"
        (1..Random.nextInt(3,10)).forEach { aux=(letters+numbers).random()+aux }
        email=aux
    }

    override fun createSender():String {
        return super.createSender()+":$name:$email:$telephone"
    }
}

class RestartableBidderMicroservice(private val state: Int = 0):SpecificBidderMicroservice(){

    override fun run(){
        if(state==0)
            super.run()
        else
            super.waitForResult()
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice: BidderMicroservice
    if(args.size==1 && args[0] in listOf("0","1"))
        bidderMicroservice = RestartableBidderMicroservice(args[0].toInt())
    else
        bidderMicroservice = RestartableBidderMicroservice()
    bidderMicroservice.run()
}