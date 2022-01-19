package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    View(context, attrs, defStyleAttr) {

    private var elapsed = 0f

    private var widthSize = 0f
    private var heightSize = 0f

    private lateinit var defaultText: String
    private lateinit var downloadingText: String

    private val bgPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.GREEN // default
    }

    private val rectPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLUE // default
    }

    private val txtPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE // default
        textAlign = Paint.Align.CENTER
        textSize = 50f // default
    }

    private val arcPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.GREEN // default
    }

    private val arcSize: Float = 50f
    private var textMaxWidth: Float = 0f
    private val paddingAroundText = 10f
    private var textStartX = 0f
    private var textStartY = 0f
    private var arcPositionX = 0f
    private var arcPositionY = 0f

    private var valueAnimatorLoading = ValueAnimator()
    private var valueAnimatorComplete = ValueAnimator()

    private lateinit var displayTitle: String

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Loading -> animateLoading()
            ButtonState.Completed -> animateCompleted()
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            txtPaint.textSize = getDimension(R.styleable.LoadingButton_textSize, 50f)
            txtPaint.color = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)

            bgPaint.color = getColor(R.styleable.LoadingButton_defaultBackgroundColor, Color.GREEN)
            rectPaint.color = getColor(R.styleable.LoadingButton_downloadingBackgroundColor, Color.BLUE)

            arcPaint.color = getColor(R.styleable.LoadingButton_loadingArcBackgroundColor, Color.GREEN)

            defaultText = getString(R.styleable.LoadingButton_defaultText)?: ""
            downloadingText = getString(R.styleable.LoadingButton_downloadingText)?: ""

            displayTitle = defaultText
        }
    }

    private fun animateLoading() {
        if (valueAnimatorComplete.isRunning)
            valueAnimatorComplete.end()

        displayTitle = downloadingText

        valueAnimatorLoading.apply {
            setFloatValues(0f, 0.9f)
            duration = 2000
            repeatCount = 0

            addUpdateListener { anim ->
                elapsed = anim.animatedValue as Float
                invalidate()
            }

            start()
        }
    }

    private fun animateCompleted() {
        if (valueAnimatorLoading.isRunning)
            valueAnimatorLoading.end()

        valueAnimatorComplete.apply {
            setFloatValues(elapsed, 1f)
            duration = 300
            repeatCount = 0

            addUpdateListener { anim ->
                elapsed = anim.animatedValue as Float
                invalidate()
            }

            doOnEnd {
                elapsed = 0f
                invalidate()

                displayTitle = defaultText
            }

            start()
        }
    }

    fun setCompleted() {
        buttonState = ButtonState.Completed
    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Loading

        super.performClick()

        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            drawColor(bgPaint.color)

            drawRect(0f, 0f, widthSize * elapsed, heightSize, rectPaint)
            drawArc(arcPositionX, arcPositionY, arcPositionX + arcSize, arcPositionY + arcSize, 0f, elapsed*360, true, arcPaint)

            save()

            clipRect(arcSize + paddingAroundText, 0f, textMaxWidth, heightSize)

            drawText(displayTitle, textStartX, textStartY, txtPaint)

            restore()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)

        textMaxWidth = widthSize - (arcSize * 2) - (paddingAroundText * 2)
        textStartX = arcSize + paddingAroundText + textMaxWidth/2
        textStartY = heightSize/2 + txtPaint.textSize/2

        arcPositionX = textStartX + txtPaint.measureText(downloadingText)/2 + paddingAroundText
        arcPositionX = if (arcPositionX > (widthSize - arcSize)) widthSize - arcSize else arcPositionX

        arcPositionY = heightSize/2 - arcSize/2
    }

}