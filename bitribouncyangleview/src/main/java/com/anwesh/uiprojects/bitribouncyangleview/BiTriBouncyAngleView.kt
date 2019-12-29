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
import android.util.Log

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

class BiTriBouncyAngleView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false, var t : Long = System.currentTimeMillis()) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }

        }

        fun start() {
            if (!animated) {
                t = System.currentTimeMillis()
                Log.d("starting animation at", "$t")
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                var currT : Long = System.currentTimeMillis()
                animated = false
                Log.d("stopping animation at:", "${currT}")
                Log.d("animated for", "${currT - t} milliseconds")
            }
        }
    }

    data class BTBANode(var i : Int, val state : State = State()) {

        private var next : BTBANode? = null
        private var prev : BTBANode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BTBANode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBTBANode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BTBANode {
            var curr : BTBANode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }


    data class BiTriBouncyAngle(var i : Int) {

        private val root : BTBANode = BTBANode(0)
        private var curr : BTBANode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }
}