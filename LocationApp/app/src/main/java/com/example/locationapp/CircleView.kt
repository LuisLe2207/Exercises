package com.example.locationapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation

class CircleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paintBigCircle = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val paintInnerCircle = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = 10f
    }

    private val paintArrow = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 10f
    }

    private var horizontalAccuracy = 0f
    private var horizontalDirection = 0f
    private val INNER_CIRCLE_RADIUS = 20f
    private var centerX = 0f
    private var centerY = 0f

    init {
        context?.let {
            it.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircleView,
                0, 0
            ).apply {
                try {
                    horizontalAccuracy = getFloat(R.styleable.CircleView_horizontalAccuracy, 0f)
                    horizontalDirection = getFloat(R.styleable.CircleView_horizontalDirection, 0f)
                } finally {
                    recycle()
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        centerX = (measuredWidth / 2).toFloat()
        centerY = (measuredHeight/ 2).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCircles(canvas)
        drawArrow(canvas)
    }

    private fun drawCircles(canvas: Canvas?) {
        canvas?.run {
            drawCircle(
                centerX,
                centerY,
                horizontalAccuracy,
                paintBigCircle
            )
            drawCircle(
                centerX,
                centerY,
                INNER_CIRCLE_RADIUS,
                paintInnerCircle
            )
        }
    }

    private fun drawArrow(canvas: Canvas?) {
        Path().apply {
            moveTo(
                centerX + INNER_CIRCLE_RADIUS,
                centerY - (INNER_CIRCLE_RADIUS * 2)
            )
            lineTo(
                centerX + INNER_CIRCLE_RADIUS,
                centerY + (INNER_CIRCLE_RADIUS * 2)
            )
            lineTo(
                centerX + (INNER_CIRCLE_RADIUS * 4),
                centerY
            )
            lineTo(
                centerX + INNER_CIRCLE_RADIUS,
                centerY - (INNER_CIRCLE_RADIUS * 2)

            )
        }.also {
            canvas?.rotate(horizontalDirection - 90, centerX, centerY)
            canvas?.drawPath(it, paintArrow)
        }
    }

    fun setHorizontalAccuracy(value: Float) {
        // Sometime the horizontalAccuracy value is too small to draw the circle, that's why it multiply it for 10
        horizontalAccuracy = value * 10
        invalidate()
        requestLayout()
    }

    fun setHorizontalDirection(value: Float) {
        horizontalDirection = value
        invalidate()
        requestLayout()
    }
}