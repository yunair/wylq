package com.air.wuyunliuqi.view

import android.animation.Animator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.IntDef
import androidx.core.graphics.ColorUtils
import com.air.wuyunliuqi.Utils
import com.air.wuyunliuqi.model.PielItem
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PielView : View {
    private var mRange = RectF()
    private var mRadius = 0
    private lateinit var mArcPaint: Paint
    private lateinit var mTextPaint: TextPaint
    private val mStartAngle = 0f
    private var mCenter = 0
    private var mPadding = 0
    private var mTopTextPadding = 0
    private var mTopTextSize = 0
    private var mSecondaryTextSize = 0
    private var mRoundOfNumber = 4
    private var mEdgeWidth = -1
    private var borderColor = 0
    private var defaultBackgroundColor = 0
    private var drawableCenterImage: Drawable? = null
    private var textColor = 0
    private var predeterminedNumber = -1
    private var viewRotation = 0f
    private var fingerRotation = 0.0
    private var newRotationStore = DoubleArray(3)
    private var mPielItems: List<PielItem>? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private fun init() {
        mArcPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        mTextPaint = TextPaint().apply {
            isAntiAlias = true
            if (textColor != 0) {
                color = textColor
            }
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14f,
                resources.displayMetrics
            )
        }

        mRange = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (mPadding + mRadius).toFloat(),
            (mPadding + mRadius).toFloat()
        )
    }

    fun getPielItems(): Int {
        return mPielItems!!.size
    }

    fun setData(pielItems: List<PielItem>?) {
        mPielItems = pielItems
        invalidate()
    }

    fun setPieBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setBorderColor(color: Int) {
        borderColor = color
        invalidate()
    }

    fun setTopTextPadding(padding: Int) {
        mTopTextPadding = padding
        invalidate()
    }

    fun setPieCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setTopTextSize(size: Int) {
        mTopTextSize = size
        invalidate()
    }

    fun setSecondaryTextSizeSize(size: Int) {
        mSecondaryTextSize = size
        invalidate()
    }

    fun setBorderWidth(width: Int) {
        mEdgeWidth = width
        invalidate()
    }

    fun setPieTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    private fun drawPieBackgroundWithBitmap(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawBitmap(
            bitmap, null, Rect(
                mPadding / 2, mPadding / 2,
                measuredWidth - mPadding / 2,
                measuredHeight - mPadding / 2
            ), null
        )
    }

    /**
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mPielItems == null) {
            return
        }
        drawBackgroundColor(canvas, defaultBackgroundColor)
        init()
        var tmpAngle = mStartAngle
        val sweepAngle = 360f / mPielItems!!.size
        for (i in mPielItems!!.indices) {
            if (mPielItems!![i].color != 0) {
                mArcPaint.style = Paint.Style.FILL
                mArcPaint.color = mPielItems!![i].color
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint)
            }
            if (borderColor != 0 && mEdgeWidth > 0) {
                mArcPaint.style = Paint.Style.STROKE
                mArcPaint.color = borderColor
                mArcPaint.strokeWidth = mEdgeWidth.toFloat()
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint)
            }
            val sliceColor =
                if (mPielItems!![i].color != 0) mPielItems!![i].color else defaultBackgroundColor
            if (!TextUtils.isEmpty(mPielItems!![i].topText)) drawTopText(
                canvas,
                tmpAngle,
                sweepAngle,
                mPielItems!![i].topText,
                sliceColor
            )
            if (!TextUtils.isEmpty(mPielItems!![i].secondaryText)) drawSecondaryText(
                canvas,
                tmpAngle,
                mPielItems!![i].secondaryText!!,
                sliceColor
            )
            if (mPielItems!![i].icon != 0) drawImage(
                canvas, tmpAngle, BitmapFactory.decodeResource(
                    resources,
                    mPielItems!![i].icon
                )
            )
            tmpAngle += sweepAngle
        }
        drawableCenterImage?.let {
            drawCenterImage(canvas, it)
        }
    }

    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == 0) return
        val mBackgroundPaint = Paint()
        mBackgroundPaint.color = color
        canvas.drawCircle(
            mCenter.toFloat(),
            mCenter.toFloat(),
            (mCenter - 5).toFloat(),
            mBackgroundPaint
        )
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = min(measuredWidth, measuredHeight)
        mPadding = if (paddingLeft == 0) 10 else paddingLeft
        mRadius = width - mPadding * 2
        mCenter = width / 2
        setMeasuredDimension(width, width)
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param bitmap
     */
    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = mRadius / mPielItems!!.size
        val angle = ((tmpAngle + 360f / mPielItems!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * sin(angle.toDouble())).toInt()
        val rect = Rect(
            x - imgWidth / 2, y - imgWidth / 2,
            x + imgWidth / 2, y + imgWidth / 2
        )
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawCenterImage(canvas: Canvas, drawable: Drawable) {
        var bitmap = Utils.drawableToBitmap(drawable);
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            false
        )

        canvas.drawBitmap(
            bitmap, (measuredWidth / 2 - bitmap.width / 2).toFloat(), (
                    measuredHeight / 2 - bitmap.height / 2).toFloat(), null
        )
    }

    private fun isColorDark(color: Int): Boolean {
        val colorValue = ColorUtils.calculateLuminance(color)
        val compareValue = 0.30
        return colorValue <= compareValue
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private fun drawTopText(
        canvas: Canvas,
        tmpAngle: Float,
        sweepAngle: Float,
        mStr: String,
        backgroundColor: Int
    ) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        if (textColor == 0) {
            mTextPaint.color =
                if (isColorDark(backgroundColor)) {
                    -0x1
                } else {
                    -0x1000000
                }
        }
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        mTextPaint.typeface = typeface
        mTextPaint.textAlign = Paint.Align.LEFT
        mTextPaint.textSize = mTopTextSize.toFloat()
        val textWidth = mTextPaint.measureText(mStr)
        val hOffset = (mRadius * Math.PI / mPielItems!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mTopTextPadding
        canvas.drawTextOnPath(mStr, path, hOffset.toFloat(), vOffset.toFloat(), mTextPaint)
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param mStr
     * @param backgroundColor
     */
    private fun drawSecondaryText(
        canvas: Canvas,
        tmpAngle: Float,
        mStr: String,
        backgroundColor: Int
    ) {
        canvas.save()
        val arraySize = mPielItems!!.size
        if (textColor == 0) {
            mTextPaint.color =
                if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        }
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        mTextPaint.typeface = typeface
        mTextPaint.textSize = mSecondaryTextSize.toFloat()
        mTextPaint.textAlign = Paint.Align.LEFT
        val textWidth = mTextPaint.measureText(mStr)
        val initFloat = tmpAngle + 360f / arraySize / 2
        val angle = (initFloat * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * sin(angle.toDouble())).toInt()
        val rect = RectF(
            x + textWidth, y.toFloat(),
            x - textWidth, y.toFloat()
        )
        val path = Path()
        path.addRect(rect, Path.Direction.CW)
        path.close()
        canvas.rotate(initFloat + arraySize / 18f, x.toFloat(), y.toFloat())
        canvas.drawTextOnPath(
            mStr,
            path,
            mTopTextPadding / 7f,
            mTextPaint.textSize / 2.75f,
            mTextPaint
        )
        canvas.restore()
    }

    /**
     * @return
     */
    private fun getAngleOfIndexTarget(index: Int): Float {
        return 360f / mPielItems!!.size * index
    }

    /**
     * @param numberOfRound
     */
    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }

    fun setPredeterminedNumber(predeterminedNumber: Int) {
        this.predeterminedNumber = predeterminedNumber
    }

    private var touchEnabled = true
    fun isTouchEnabled(): Boolean {
        return touchEnabled
    }

    fun setTouchEnabled(touchEnabled: Boolean) {
        this.touchEnabled = touchEnabled
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!touchEnabled) {
            return false
        }
        val x = event.x
        val y = event.y
        val xc = width / 2.0f
        val yc = height / 2.0f
        val newFingerRotation: Double
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                viewRotation = (rotation + 360f) % 360f
                fingerRotation =
                    Math.toDegrees(atan2((x - xc).toDouble(), (yc - y).toDouble()))
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                newFingerRotation =
                    Math.toDegrees(atan2((x - xc).toDouble(), (yc - y).toDouble()))
                if (isRotationConsistent(newFingerRotation)) {
                    rotation = newRotationValue(viewRotation, fingerRotation, newFingerRotation)
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                newFingerRotation =
                    Math.toDegrees(atan2((x - xc).toDouble(), (yc - y).toDouble()))
                fingerRotation = newFingerRotation

                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun newRotationValue(
        originalWheelRotation: Float,
        originalFingerRotation: Double,
        newFingerRotation: Double
    ): Float {
        val computationalRotation = newFingerRotation - originalFingerRotation
        return (originalWheelRotation + computationalRotation.toFloat() + 360f) % 360f
    }

    /**
     * This detects if your finger movement is a result of an actual raw touch event of if it's from a view jitter.
     * This uses 3 events of rotation temporary storage so that differentiation between swapping touch events can be determined.
     *
     * @param newRotValue
     */
    private fun isRotationConsistent(newRotValue: Double): Boolean {
        if (newRotationStore[2].compareTo(newRotationStore[1]) != 0) {
            newRotationStore[2] = newRotationStore[1]
        }
        if (newRotationStore[1].compareTo(newRotationStore[0]) != 0) {
            newRotationStore[1] = newRotationStore[0]
        }
        newRotationStore[0] = newRotValue
        return !(newRotationStore[2].compareTo(newRotationStore[0]) == 0 || newRotationStore[1].compareTo(
            newRotationStore[0]
        ) == 0 || newRotationStore[2].compareTo(newRotationStore[1]) == 0 //Is the middle event the odd one out
                || newRotationStore[0] > newRotationStore[1] && newRotationStore[1] < newRotationStore[2]
                || newRotationStore[0] < newRotationStore[1] && newRotationStore[1] > newRotationStore[2])
    }
}