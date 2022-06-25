import java.io.File

abstract class InFileJournalizer {
    protected val fileName = "journal.txt"

    fun start() {
        val text=File(fileName).readText()
        if(text=="finished")
            File(fileName).writeText("")
        else
            restoreState()
    }

    fun journalize(action: String) {
        File(fileName).appendText(action)
    }

    fun finish() {
        File(fileName).writeText("finished")
    }

    protected fun getStates(): List<String> = File("fileName").readLines()

    abstract fun restoreState()
}

fun main(args: Array<String>) {
    println("Hi")
}