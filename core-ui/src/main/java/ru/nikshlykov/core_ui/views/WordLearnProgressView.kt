package ru.nikshlykov.core_ui.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
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
          getInteger(R.styleable.WordLearnProgressView_learnProgress, DEFAULT_WORD_PROGRESS)
      } finally {
        recycle()
      }
    }
  }

  override fun onDraw(canvas: Canvas?) {
    when (learnProgress) {
      -1 -> setBackgroundResource(R.drawable.shape_progress)
      0 -> setBackgroundResource(R.drawable.shape_progress_0)
      1 -> setBackgroundResource(R.drawable.shape_progress_1)
      2 -> setBackgroundResource(R.drawable.shape_progress_2)
      3 -> setBackgroundResource(R.drawable.shape_progress_3)
      4 -> setBackgroundResource(R.drawable.shape_progress_4)
      5 -> setBackgroundResource(R.drawable.shape_progress_5)
      6 -> setBackgroundResource(R.drawable.shape_progress_6)
      7, 8 -> setBackgroundResource(R.drawable.shape_progress_7)
    }
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