package pk.taylor_darzi.interfaces

interface GenericEvents {
    fun onGenericEvent(eventType: String, vararg args: Any?)
}