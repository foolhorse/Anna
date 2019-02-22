package me.machao.android.anna.library

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import me.machao.android.anna.library.data.Event
import me.machao.android.anna.library.data.EventType
import java.lang.ref.WeakReference

/**
 * Date  2019/2/19
 * @author charliema
 */
class Anna(
    private val application: Application,
    private val host: String?,
    private val path: String?,
    internal val strategy: Strategy,
    internal val uploadThreshold: Int,

    private val dispatcher: Dispatcher
) {

    companion object {

        @Volatile
        private var singleton: Anna? = null

        fun init(builder: Anna.Builder) {
            if (singleton == null) {
                synchronized(Anna) {
                    if (singleton == null) {
                        singleton = builder.build()
                    }
                }
            }
        }

        fun getInstance(): Anna {
            if (singleton == null) {
                throw IllegalStateException("Anna need call init first")
            }
            return singleton!!
        }

        const val REQUEST_COMPLETE = 1

        val HANDLER: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    REQUEST_COMPLETE -> {
                        val event = msg.obj as Event
                        log("ok")
                    }
                    else -> throw AssertionError("Unknown handler message received: " + msg.what)
                }
            }
        }

        fun newEvent(msg: String) {
            getInstance().newEvent(msg)
        }

    }

    fun newAppCreateEvent(application: Application) {
        val event = Event(EventType.APP_CREATE, application.packageName, null)
        dispatcher.dispatchSubmit(event)
    }

    fun newAppDestroyEvent(application: Application) {
        val event = Event(EventType.APP_DESTROY, application.packageName, null)
        dispatcher.dispatchSubmit(event)
    }

    private fun newAppStartEvent() {
        dispatcher.dispatchGetPendingFromDb()
    }

    private fun newAppStopEvent() {
        dispatcher.dispatchSavePendingToDb()
    }

    private fun newPageStartEvent(activity: Activity) {
        val event = Event(EventType.PAGE_START,"" , activity.localClassName)
        dispatcher.dispatchSubmit(event)
    }

    private fun newPageStopEvent(activity: Activity) {
        val event = Event(EventType.PAGE_STOP,"" , activity.localClassName)
        dispatcher.dispatchSubmit(event)
    }

    private fun newPageStartEvent(fragment: Fragment) {

    }

    private fun newPageStopEvent(fragment: Fragment) {

    }

    private fun newEvent(msg: String) {

    }


    private var activityRefList = mutableListOf<WeakReference<Activity>>()


    init {
//        getPackageInfo()
//        getPhoneInfo()

        val fragmentLifecycleCallbacks = object : android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
                super.onFragmentPreAttached(fm, f, context)
            }

            override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
                super.onFragmentAttached(fm, f, context)
            }

            override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                super.onFragmentPreCreated(fm, f, savedInstanceState)
            }

            override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                super.onFragmentCreated(fm, f, savedInstanceState)
            }

            override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            }

            override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                super.onFragmentActivityCreated(fm, f, savedInstanceState)
            }

            override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
                super.onFragmentSaveInstanceState(fm, f, outState)
            }

            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentStarted(fm, f)
                if (f.isVisible) {
                    newPageStartEvent(f)
                }
            }

            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
            }

            override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
                super.onFragmentPaused(fm, f)
            }

            override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                super.onFragmentStopped(fm, f)
                if (f.isVisible) {
                    newPageStopEvent(f)
                }
            }

            override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentViewDestroyed(fm, f)
            }


            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentDestroyed(fm, f)
            }

            override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
                super.onFragmentDetached(fm, f)
            }
        }

        this@Anna.application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                if (activity == null) {
                    return
                }
//                attachFloatView(activity)
                if (activity is android.support.v4.app.FragmentActivity) {
                    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
                }
            }

            override fun onActivityStarted(activity: Activity?) {
                if (activity == null) {
                    return
                }
                if (activityRefList.isEmpty()) {
                    newAppStartEvent()
                }
                activityRefList.add(WeakReference(activity))
                newPageStartEvent(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityStopped(activity: Activity?) {
                if (activity == null) {
                    return
                }
                newPageStopEvent(activity)

                val needRemoveRefList = mutableListOf<WeakReference<Activity>>()
                for (ref in activityRefList) {
                    if (ref.get() == activity) {
                        needRemoveRefList.add(ref)
                    }
                }
                activityRefList.removeAll(needRemoveRefList)

                if (activityRefList.isEmpty()) {
                    newAppStopEvent()
                }

            }

            override fun onActivityDestroyed(activity: Activity?) {
                if (activity is android.support.v4.app.FragmentActivity) {
                    activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
                }
            }

        })
    }

    class Builder(application: Application) {
        private val application = application

        private var host: String? = null
        private var path: String? = null
        private var strategy: Strategy? = null
        private var uploader: Uploader? = null
        private var uploadThreshold: Int = 0

        fun server(host: String, path: String): Builder {
            this@Builder.host = host
            this@Builder.path = path
            return this
        }

        fun strategy(strategy: Strategy): Builder {
            this@Builder.strategy = strategy
            return this
        }

        fun uploader(uploader: Uploader) {
            this@Builder.uploader = uploader
        }

        /**
         * only work when STRATEGY_THRESHOLD
         */
        fun uploadThreshold(threshold: Int) {
            this@Builder.uploadThreshold = threshold
        }

        fun build(): Anna {
            val dispatcher =
                Dispatcher(application.applicationContext, uploader ?: UrlConnectionUploader(), HANDLER)

            return Anna(application, host, path, strategy ?: Strategy.DEBUG, uploadThreshold, dispatcher)
        }

    }


}