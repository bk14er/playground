package phonebook

import java.time.Duration

data class Timer(private val startTime: Long = 0L) {

    fun elapsed(): Duration = Duration.ofMillis(System.currentTimeMillis() - startTime)

    companion object {
        fun startCounting(): Timer = Timer(System.currentTimeMillis())
    }
}