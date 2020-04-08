package com.triline.billionlights.view.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.triline.billionlights.utils.fromHtml

class HelpInOutTextView(context: Context?, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text?.fromHtml(), type)
    }

}