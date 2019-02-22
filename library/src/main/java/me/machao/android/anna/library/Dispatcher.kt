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
    private var handler: Handler

    private val pendingEventList = mutableListOf<Event>()

    companion object {
        const val DISPATCHER_THREAD_NAME = "Anna-Dispatcher"

        const val EVENT_SUBMIT = 1
        const val EVENT_SAVE_PENDING_TO_DB = 2
        const val EVENT_GET_PENDING_FROM_DB = 3
    }

    init {
        dispatcherThread.start()
        handler = DispatcherHandler(dispatcherThread.looper, this)
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
                EVENT_SAVE_PENDING_TO_DB -> {
                    dispatcher.performSavePendingToDb()
                }
                EVENT_GET_PENDING_FROM_DB -> {
                    dispatcher.performGetPendingFromDb()
                }
                else -> throw AssertionError("Unknown handler message received: " + msg.what)
            }
        }
    }

    fun dispatchSubmit(event: Event) {
        handler.sendMessage(handler.obtainMessage(EVENT_SUBMIT, event))
    }

    fun dispatchSavePendingToDb() {
        handler.sendMessage(handler.obtainMessage(EVENT_SAVE_PENDING_TO_DB))
    }

    fun dispatchGetPendingFromDb() {
        handler.sendMessage(handler.obtainMessage(EVENT_GET_PENDING_FROM_DB))
    }


    private fun performSubmit(event: Event) {
        when (Anna.getInstance().strategy) {
            Strategy.DEBUG -> {
                uploader.upload()
            }
            Strategy.RELEASE -> {
                pendingEventList.add(event)
                if (pendingEventList.size > Anna.getInstance().uploadThreshold) {
                    uploader.upload()
                }

            }
            else -> {

            }
        }
    }

    private fun performSavePendingToDb() {
        db.use {
            transaction {
                pendingEventList.forEach {
                    insert(
                        AnnaDatabaseOpenHelper.TABLE_NAME,
                        AnnaDatabaseOpenHelper.COLUMNS_DATA to GSON.toJson(it),
                        AnnaDatabaseOpenHelper.COLUMNS_TIMESTAMP to it.timestamp
                    )
                }
            }
        }
    }

    private fun performGetPendingFromDb() {
        db.use {
            select(AnnaDatabaseOpenHelper.TABLE_NAME, AnnaDatabaseOpenHelper.COLUMNS_DATA)
                .orderBy(AnnaDatabaseOpenHelper.COLUMNS_TIMESTAMP, SqlOrderDirection.ASC)
                .exec {
                    val dbEventList = this.parseList(object : RowParser<Event> {
                        override fun parseRow(columns: Array<Any?>): Event {
                            val data = columns[0] as String
                            return GSON.fromJson(data, Event::class.java)
                        }
                    })
                    pendingEventList.addAll(dbEventList)
                }
            delete(AnnaDatabaseOpenHelper.TABLE_NAME)
        }
    }


}