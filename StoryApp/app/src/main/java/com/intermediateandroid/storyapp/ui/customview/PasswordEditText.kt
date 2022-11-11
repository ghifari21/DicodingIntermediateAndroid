package com.intermediateandroid.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.intermediateandroid.storyapp.R

class PasswordEditText : AppCompatEditText {
    private lateinit var lockIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        lockIcon = ContextCompat.getDrawable(context, R.drawable.ic_lock_gray) as Drawable

        addTextChangedListener(doAfterTextChanged {
            if (it.toString().length < 6) {
                error = context.getString(R.string.invalid_password)
            }
        })
    }

    private fun setIcon() {
        setButtonDrawables(startOfTheText = lockIcon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        setIcon()
        background = ContextCompat.getDrawable(context, R.drawable.edit_text_background)
        hint = context.getString(R.string.hint_password)
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        maxLines = 1
    }
}