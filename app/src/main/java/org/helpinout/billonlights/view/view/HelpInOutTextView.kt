package org.helpinout.billonlights.view.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import org.helpinout.billonlights.utils.fromHtml

class HelpInOutTextView(context: Context?, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text?.fromHtml(), type)
    }

}