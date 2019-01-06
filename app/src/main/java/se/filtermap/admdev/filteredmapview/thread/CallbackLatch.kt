package se.filtermap.admdev.filteredmapview.thread

//Triggers a callback when all supplied callbacks have been called at least once
class CallbackLatch(
    private val numberOfCallbacks: Int,
    private val callback: () -> Unit
) {
    private var autoIdCounter = 0
    private val progress = mutableMapOf<Int, Int>()

    fun <T> track(function: (input: T) -> Unit): (input: T) -> Unit {
        val callbackId = autoIdCounter++
        progress.putIfAbsent(callbackId, 0)
        return {
            function(it)
            updateProgress(callbackId)
        }
    }

    private fun updateProgress(id: Int) {
        progress.apply {
            this[id] = 1 + this.getOrElse(id) {0}
            if (values.all { it > 0 } && values.size >= numberOfCallbacks) {
                callback()
            }
        }
    }
}