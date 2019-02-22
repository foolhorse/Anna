package me.machao.android.anna.library

import android.app.Activity
import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView

/**
 * Date  2019/2/22
 * @author charliema
 */
class FloatView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    fun attachActivity(activity: Activity) {
        val decorView = activity.window.decorView as ViewGroup

        decorView.addView(
            this,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        // top in z
        ViewCompat.setElevation(this, Float.MAX_VALUE)

//        val targetList = findClickListener(decorView)
//        for (target in targetList) {
//            hookClick(target)
//        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val action = event.action and MotionEvent.ACTION_MASK
            if (action == MotionEvent.ACTION_DOWN) {
                val decorView = this.rootView

                val targetList = findTarget(decorView, event.x.toInt(), event.y.toInt())
                for (target in targetList) {
                    hookClick(target)
                }
                log("dispatchTouchEvent ACTION_DOWN")
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun findTarget(view: View, x: Int, y: Int): List<View> {
        val resultList = mutableListOf<View>()
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val childView = view.getChildAt(i)
                val childResultList = findTarget(childView, x, y)
                resultList.addAll(childResultList)
            }
        }
        if (view.isEnabled && view.isClickable && view.hasOnClickListeners()) {
            resultList.add(view)
        }
        return resultList
    }

    private fun hookClick(view: View) {
        val mListenerInfoField = View::class.java.getDeclaredField("mListenerInfo")
        mListenerInfoField.isAccessible = true
        val mListenerInfo = mListenerInfoField.get(view)

        val mOnClickListenerField = mListenerInfo?.javaClass?.getDeclaredField("mOnClickListener")
        mOnClickListenerField?.isAccessible = true
        val mOnClickListener = mOnClickListenerField?.get(mListenerInfo) as OnClickListener?

        val eventClickListener = object : EventClickListener {
            override fun onClick(v: View?) {
                mOnClickListener?.onClick(v)
                Anna.getInstance().newClickEvent(v)
            }
        }
        if (mOnClickListener is EventClickListener) {

        } else {
            mOnClickListenerField?.set(mListenerInfo, eventClickListener)
        }

    }

    interface EventClickListener : View.OnClickListener

}
