package org.helpinout.billonlights.view.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.IdRes
import com.avneesh.crashreporter.CrashReporter
import org.helpinout.billonlights.R


class CustomStringAdapter<String>(context: Context, @IdRes private val textViewResourceId: Int = 0, values: Array<String>) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values) {

    init {
        setDropDownViewResource(R.layout.item_drop_down)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = createViewFromResource(convertView, parent, android.R.layout.simple_spinner_item)
        return bindData(getItem(position)!!, view)
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = createViewFromResource(convertView, parent, R.layout.item_drop_down)
        if (position == 0) {
            view.setTextColor(Color.GRAY)
        } else {
            view.setTextColor(Color.BLACK)
        }

        view.post { view.setSingleLine(false) }
        return bindData(getItem(position)!!, view)
    }

    private fun createViewFromResource(convertView: View?, parent: ViewGroup, layoutResource: Int): TextView {
        val context = parent.context
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
        return try {
            if (textViewResourceId == 0) {
                view as TextView
            } else {
                view.findViewById(textViewResourceId) ?: throw RuntimeException("Failed to find view with ID " + "${context.resources.getResourceName(textViewResourceId)} in item layout")
            }
        } catch (ex: ClassCastException) {
            CrashReporter.logException(ex)
            throw IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", ex)
        }
    }

    private fun bindData(value: String, view: TextView): TextView {
        view.text = value.toString()
        return view
    }
}