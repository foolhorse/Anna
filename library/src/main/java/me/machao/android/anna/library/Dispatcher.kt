package me.machao.android.anna.library

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import me.machao.android.anna.library.data.Event
import org.jetbrains.anko.db.*


/**
 * Date  2019/2/21
 * @author charliema
 */
class Dispatcher(
    private val context: Context,
    private val uploader: Uploader,
    private val mainThreadHandler: Handler
) {
    private val db: AnnaDatabaseOpenHelper = context.database

    private val dispatcherThread = DispatcherThread()
    private val handler = DispatcherHandler(dispatcherThread.looper, this)

    init {
        dispatcherThread.start()
    }

    companion object {
        const val DISPATCHER_THREAD_NAME = "Anna-Dispatcher"

        const val EVENT_SUBMIT = 1

    }

    private class DispatcherThread :
        HandlerThread(DISPATCHER_THREAD_NAME, android.os.Process.THREAD_PRIORITY_BACKGROUND)

    private class DispatcherHandler(looper: Looper, private val dispatcher: Dispatcher) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                EVENT_SUBMIT -> {
                    val event = msg.obj as Event
                    dispatcher.performSubmit(event)
                }
                else -> throw AssertionError("Unknown handler message received: " + msg.what)
            }
        }
    }

    fun dispatchSubmit(event: Event) {
        handler.sendMessage(handler.obtainMessage(EVENT_SUBMIT, event))
    }

    private val eventList = mutableListOf<Event>()

    private fun performSubmit(event: Event) {
        when (Anna.getInstance().strategy) {
            Strategy.REALTIME -> {
                uploader.upload()
            }
            Strategy.THRESHOLD -> {
                eventList.add(event)
                db.use {
                    select(AnnaDatabaseOpenHelper.TABLE_NAME, "name")
                        .orderBy("id", SqlOrderDirection.ASC)
                        .exec {
                            val dbEventList = this.parseList(object : RowParser<Event> {
                                override fun parseRow(columns: Array<Any?>): Event {
                                    val data = columns[0] as String
                                    return gson().fromJson(data, Event::class)
                                }
                            })
                            eventList.addAll(dbEventList)
                        }
                    delete(AnnaDatabaseOpenHelper.TABLE_NAME)
                }
                if (eventList.size >= 50) {

                }
            }
        }

    }
}