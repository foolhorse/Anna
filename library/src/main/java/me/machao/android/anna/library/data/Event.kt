package me.machao.android.anna.library.data

/**
 * Date  2019/2/21
 * @author charliema
 */
class Event(

    var eventType: EventType,

    var appName: String,
    var pageName: String?
) {
    val timestamp: Long = System.currentTimeMillis()

}