package ai.bump_dump.shared

interface AppEvent

interface AppEventListener {
    fun onEvent(appEvent: AppEvent) {

    }
}

object AppEventsStorage {
    private val list = mutableListOf<AppEventListener>()

    fun subscribe(listener: AppEventListener) {
        list.add(listener)
    }

    fun emit(event: AppEvent) {
        list.forEach {
            it.onEvent(event)
        }
    }
}


