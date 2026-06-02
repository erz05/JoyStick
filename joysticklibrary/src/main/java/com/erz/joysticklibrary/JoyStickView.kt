package com.erz.joysticklibrary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

@Deprecated(
    message = "Use the Compose-based Joystick composable instead.",
    replaceWith = ReplaceWith("Joystick", "com.erz.joysticklibrary.Joystick")
)
class JoyStick @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    companion object {
        const val DIRECTION_CENTER = -1
        const val DIRECTION_LEFT = 0
        const val DIRECTION_LEFT_UP = 1
        const val DIRECTION_UP = 2
        const val DIRECTION_UP_RIGHT = 3
        const val DIRECTION_RIGHT = 4
        const val DIRECTION_RIGHT_DOWN = 5
        const val DIRECTION_DOWN = 6
        const val DIRECTION_DOWN_LEFT = 7

        const val TYPE_8_AXIS = 11
        const val TYPE_4_AXIS = 22
        const val TYPE_2_AXIS_LEFT_RIGHT = 33
        const val TYPE_2_AXIS_UP_DOWN = 44

        private fun calculateDirection(degrees: Double): Int {
            return when {
                (degrees >= 0 && degrees < 22.5) || (degrees < 0 && degrees > -22.5) -> DIRECTION_LEFT
                degrees >= 22.5 && degrees < 67.5 -> DIRECTION_LEFT_UP
                degrees >= 67.5 && degrees < 112.5 -> DIRECTION_UP
                degrees >= 112.5 && degrees < 157.5 -> DIRECTION_UP_RIGHT
                (degrees >= 157.5 && degrees <= 180) || (degrees >= -180 && degrees < -157.5) -> DIRECTION_RIGHT
                degrees >= -157.5 && degrees < -112.5 -> DIRECTION_RIGHT_DOWN
                degrees >= -112.5 && degrees < -67.5 -> DIRECTION_DOWN
                degrees >= -67.5 && degrees < -22.5 -> DIRECTION_DOWN_LEFT
                else -> DIRECTION_CENTER
            }
        }
    }

    interface JoyStickListener {
        fun onMove(joyStick: JoyStick?, angle: Double, power: Double, direction: Int)
        fun onTap()
        fun onDoubleTap()
    }

    private var listener: JoyStickListener? = null
    private val paint: Paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        isFilterBitmap = true
    }
    private val temp: RectF = RectF()
    private val gestureDetector: GestureDetector
    private var direction = DIRECTION_CENTER
    private var type = TYPE_8_AXIS
    private var centerX = 0f
    private var centerY = 0f
    private var posX = 0f
    private var posY = 0f
    private var radius = 0f
    private var buttonRadius = 0f
    private var power = 0.0
    private var angle = 0.0

    // Background Color
    private var padColor: Int = Color.WHITE

    // Stick Color
    private var buttonColor: Int = Color.RED

    // Keeps joystick in last position
    private var stayPut = false

    // Button Size percentage of the minimum(width, height)
    private var percentage = 25

    // Background Bitmap
    private var padBGBitmap: Bitmap? = null

    // Button Bitmap
    private var buttonBitmap: Bitmap? = null

    init {
        gestureDetector = GestureDetector(context, this).apply {
            setIsLongpressEnabled(false)
            setOnDoubleTapListener(this@JoyStick)
        }

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.JoyStick)
            padColor = typedArray.getColor(R.styleable.JoyStick_padColor, Color.WHITE)
            buttonColor = typedArray.getColor(R.styleable.JoyStick_buttonColor, Color.RED)
            stayPut = typedArray.getBoolean(R.styleable.JoyStick_stayPut, false)
            percentage = typedArray.getInt(R.styleable.JoyStick_percentage, 25)
            if (percentage > 50) percentage = 50
            if (percentage < 25) percentage = 25

            val padResId = typedArray.getResourceId(R.styleable.JoyStick_backgroundDrawable, -1)
            val buttonResId = typedArray.getResourceId(R.styleable.JoyStick_buttonDrawable, -1)

            if (padResId > 0) {
                padBGBitmap = BitmapFactory.decodeResource(resources, padResId)
            }
            if (buttonResId > 0) {
                buttonBitmap = BitmapFactory.decodeResource(resources, buttonResId)
            }
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        centerX = width / 2
        centerY = height / 2
        val minDim = minOf(width, height)
        posX = centerX
        posY = centerY
        buttonRadius = minDim / 2f * (percentage / 100f)
        radius = minDim / 2f * ((100f - percentage) / 100f)
    }

    override fun onDraw(canvas: Canvas) {
        val pad = padBGBitmap
        if (pad == null) {
            paint.color = padColor
            canvas.drawCircle(centerX, centerY, radius, paint)
        } else {
            temp.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            canvas.drawBitmap(pad, null, temp, paint)
        }

        val button = buttonBitmap
        if (button == null) {
            paint.color = buttonColor
            canvas.drawCircle(posX, posY, buttonRadius, paint)
        } else {
            temp.set(posX - buttonRadius, posY - buttonRadius, posX + buttonRadius, posY + buttonRadius)
            canvas.drawBitmap(button, null, temp, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                posX = event.x
                posY = event.y

                when (type) {
                    TYPE_2_AXIS_LEFT_RIGHT -> {
                        posY = centerY
                    }
                    TYPE_2_AXIS_UP_DOWN -> {
                        posX = centerX
                    }
                    TYPE_4_AXIS -> {
                        if (abs(posX - centerX) > abs(posY - centerY)) {
                            posY = centerY
                        } else {
                            posX = centerX
                        }
                    }
                }

                val absVal = sqrt(((posX - centerX) * (posX - centerX) + (posY - centerY) * (posY - centerY)).toDouble()).toFloat()
                if (absVal > radius) {
                    posX = (posX - centerX) * radius / absVal + centerX
                    posY = (posY - centerY) * radius / absVal + centerY
                }

                angle = atan2((centerY - posY).toDouble(), (centerX - posX).toDouble())
                power = 100 * sqrt(((posX - centerX) * (posX - centerX) + (posY - centerY) * (posY - centerY)).toDouble()) / radius
                direction = calculateDirection(Math.toDegrees(angle))
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!stayPut) {
                    posX = centerX
                    posY = centerY
                    direction = DIRECTION_CENTER
                    angle = 0.0
                    power = 0.0
                    invalidate()
                }
            }
        }

        listener?.onMove(this, angle, power, direction)
        return true
    }

    override fun onDown(motionEvent: MotionEvent): Boolean = true
    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean = false
    override fun onScroll(motionEvent: MotionEvent?, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean = false
    override fun onLongPress(motionEvent: MotionEvent) {}
    override fun onFling(motionEvent: MotionEvent?, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean = false

    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        listener?.onTap()
        return false
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        listener?.onDoubleTap()
        return false
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean = false

    fun setListener(listener: JoyStickListener?) {
        this.listener = listener
    }

    fun getPower(): Double = power
    fun getAngle(): Double = angle
    fun getAngleDegrees(): Double = Math.toDegrees(angle)
    fun getDirection(): Int = direction
    fun getType(): Int = type

    fun setPadColor(padColor: Int) {
        this.padColor = padColor
    }

    fun setButtonColor(buttonColor: Int) {
        this.buttonColor = buttonColor
    }

    fun setButtonRadiusScale(scale: Int) {
        percentage = scale
        if (percentage > 50) percentage = 50
        if (percentage < 25) percentage = 25
    }

    fun enableStayPut(enable: Boolean) {
        this.stayPut = enable
    }

    fun setPadBackground(resId: Int) {
        this.padBGBitmap = BitmapFactory.decodeResource(resources, resId)
    }

    fun setPadBackground(bitmap: Bitmap?) {
        this.padBGBitmap = bitmap
    }

    fun setButtonDrawable(resId: Int) {
        this.buttonBitmap = BitmapFactory.decodeResource(resources, resId)
    }

    fun setButtonDrawable(bitmap: Bitmap?) {
        this.buttonBitmap = bitmap
    }

    fun setType(type: Int) {
        this.type = type
    }
}
