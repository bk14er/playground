package coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlin.random.Random


suspend fun main(): Unit = coroutineScope {
    val flow = flow {
        emit("A")
        emit("B")
        emit("C")
    }

    val collector : FlowCollector<String> = FlowCollector { print(it) }
    flow.collect(collector)
    flow.collect(collector)

}

fun <T> flow(builder: suspend FlowCollector<T>.() -> Unit) = object:Flow<T>{
    override suspend fun collect(collector: FlowCollector<T>) {
        collector.builder()
    }
}

interface Flow<T> {
    suspend fun collect(collector: FlowCollector<T>)
}

fun interface FlowCollector<T> {
    fun emit(value: T)
}

private fun CoroutineScope.produce(random: Random) =
    produce {
        while (true) {
            delay(random.nextLong(1000, 3000))
            val message = random.nextInt()
            println("Sending: $message")
            channel.send(message)
        }
    }