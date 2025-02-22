package ru.nikshlykov.core_ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ru.nikshlykov.core_ui.R
import ru.nikshlykov.core_ui.dpToPx
import java.lang.Integer.max

class WordLearnProgressView(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
        this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private val paint: Paint = Paint()
    private var rect: Rect = Rect()

    private val MIN_VIEW_WIDTH_IN_PX = context.dpToPx(40)
    private val MIN_VIEW_HEIGHT_IN_PX = context.dpToPx(10)

    var learnProgress: Int = DEFAULT_WORD_PROGRESS
        set(progress) {
            field = progress
            invalidate()
        }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WordLearnProgressView,
            0, 0
        ).apply {

            try {
                learnProgress =
                    getInteger(
                        R.styleable.WordLearnProgressView_learnProgress,
                        DEFAULT_WORD_PROGRESS
                    )
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        // Используется FILL, т.к. STROKE как-то криво рисует пискели.
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.progress_frame)
        paint.strokeWidth = context.dpToPx(1).toFloat()
        canvas.getClipBounds(rect)
        canvas.drawRoundRect(
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.right.toFloat(),
            rect.bottom.toFloat(),
            context.dpToPx(6).toFloat(),
            context.dpToPx(6).toFloat(),
            paint
        )

        // Прорисовывается, т.к. нужен пробел между прогрессом и рамкой прогресса, но STROKE криво рисует
        paint.color = Color.WHITE
        rect.set(
            rect.left + context.dpToPx(1),
            rect.top + context.dpToPx(1),
            rect.right - context.dpToPx(1),
            rect.bottom - context.dpToPx(1)
        )
        canvas?.drawRoundRect(
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.right.toFloat(),
            rect.bottom.toFloat(),
            context.dpToPx(6).toFloat(),
            context.dpToPx(6).toFloat(),
            paint
        )

        rect.set(
            rect.left + context.dpToPx(1),
            rect.top + context.dpToPx(1),
            rect.right - context.dpToPx(1),
            rect.bottom - context.dpToPx(1)
        )
        when (learnProgress) {
            -1 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_not_started)
                rect.right = rect.left + (rect.right - rect.left) / 9
            }

            0 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_0)
                rect.right = rect.left + 2 * (rect.right - rect.left) / 9
            }

            1 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_1)
                rect.right = rect.left + 3 * (rect.right - rect.left) / 9
            }

            2 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_2)
                rect.right = rect.left + 4 * (rect.right - rect.left) / 9
            }

            3 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_3)
                rect.right = rect.left + 5 * (rect.right - rect.left) / 9
            }

            4 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_4)
                rect.right = rect.left + 6 * (rect.right - rect.left) / 9
            }

            5 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_5)
                rect.right = rect.left + 7 * (rect.right - rect.left) / 9
            }

            6 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_6)
                rect.right = rect.left + 8 * (rect.right - rect.left) / 9
            }

            7, 8 -> {
                paint.color = ContextCompat.getColor(context, R.color.progress_7)
            }

            else -> {
                paint.color = Color.WHITE
            }
        }
        canvas?.drawRoundRect(
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.right.toFloat(),
            rect.bottom.toFloat(),
            context.dpToPx(6).toFloat(),
            context.dpToPx(6).toFloat(),
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY,
            MeasureSpec.AT_MOST -> max(MIN_VIEW_WIDTH_IN_PX, widthSize)

            else -> MIN_VIEW_WIDTH_IN_PX
        }

        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY,
            MeasureSpec.AT_MOST -> max(MIN_VIEW_HEIGHT_IN_PX, heightSize)

            else -> MIN_VIEW_HEIGHT_IN_PX
        }

        setMeasuredDimension(width, height)
    }

    companion object {
        private const val DEFAULT_WORD_PROGRESS = -1
    }
}