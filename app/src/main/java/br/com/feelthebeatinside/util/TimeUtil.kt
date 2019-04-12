package br.com.feelthebeatinside.util

object TimeUtil {

    fun getStringTimeByMilis(milis: Long): String {
        val duration = milis

        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60) % 60)
        val hours = (duration / (1000 * 60 * 60) % 24)

        var duration_string: String? = null
        if (hours > 0)
            duration_string =
                addZero(hours.toString()) + ":" + addZero(minutes.toString()) + ":" + addZero(seconds.toString())
        else
            duration_string = addZero(minutes.toString()) + ":" + addZero(seconds.toString())

        return duration_string
    }

    private fun addZero(value: String): String {
        if (value.length == 1)
            return "0" + value
        return value
    }
}