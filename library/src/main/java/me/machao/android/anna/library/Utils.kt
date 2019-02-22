package me.machao.android.anna.library

import android.graphics.Rect
import android.util.Log
import android.view.View
import com.google.gson.Gson

/**
 * Date  2019/2/22
 * @author charliema
 */
fun Any.log(msg: String) {
    Log.d(this.javaClass.simpleName, msg)
}

val GSON: Gson by lazy {
    Gson()
}

fun View.containsPoint(x: Int, y: Int): Boolean {
    val rect = Rect()
    this.getGlobalVisibleRect(rect)
    return rect.contains(x, y)
}