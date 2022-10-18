package com.intermediateandroid.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.intermediateandroid.storyapp.R

class EmailEditText : AppCompatEditText {
    private lateinit var emailIcon: Drawable

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
        emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_email_gray) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing.
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    error = context.getString(R.string.empty_email)
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    error = context.getString(R.string.invalid_email)
                }
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = ContextCompat.getDrawable(context, R.drawable.edit_text_background)
        hint = context.getString(R.string.hint_email)
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        setCompoundDrawablesWithIntrinsicBounds(emailIcon, null, null, null)
    }
}