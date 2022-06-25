import java.text.SimpleDateFormat
import java.util.*

class Message private constructor(val sender: String, val body: String, val timestamp: Date) {
    companion object {
        fun create(sender: String, body: String): Message {
            return Message(sender, body, Date())
        }

        fun deserialize(msg: ByteArray): Message {
            val msgString = String(msg)
            val (timestamp, sender, body) = msgString.split(' ', limit = 3)

            return Message(sender, body, Date(timestamp.toLong()))
        }
    }

    fun serialize(): ByteArray {
        return "${timestamp.time} $sender $body\n".toByteArray()
    }

    override fun toString(): String {
        val dateString = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp)
        return "[$dateString] $sender >>> $body"
    }
}

fun main(args: Array<String>) {
    val msg = Message.create("localhost:4848", "test mesaj")
    println(msg)
    val serialized = msg.serialize()
    val deserialized = Message.deserialize(serialized)
    println(deserialized)
}