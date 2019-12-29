package com.anwesh.uiprojects.bitribouncyangleview

/**
 * Created by anweshmishra on 29/12/19.
 */

import android.content.Context
import android.app.Activity
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val nodes : Int = 5
val lines : Int = 2
val scGap : Float = 0.02f / lines
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")
val rotDeg : Float = 45f
val delay : Long = 30

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
fun Int.si() : Float = 1f - 2 * this
fun Float.xRad(deg : Float) : Float = this * Math.cos(deg * Math.PI / 180f).toFloat()
fun Float.yRad(deg : Float) : Float = this * Math.sin(deg * Math.PI / 180f).toFloat()

fun Canvas.drawBiTriAngle(i : Int, scale : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify().divideScale(i, lines)
    val si : Float = i.si()
    val x : Float = size.xRad(rotDeg)
    val y : Float = size.yRad(rotDeg)
    save()
    translate(x, y)
    rotate(si * -rotDeg * sf)
    drawLine(0f, 0f, 0f, -size, paint)
    restore()
}

fun Canvas.drawBiTriAngles(scale : Float, size : Float, paint : Paint) {
    for (j in 0..(lines - 1)) {
        drawBiTriAngle(j, scale, size, paint)
    }
}

fun Canvas.drawBTBANode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(gap * (i + 1), h / 2)
    drawBiTriAngles(scale, size, paint)
    restore()
}
