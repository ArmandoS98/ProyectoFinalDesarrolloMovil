package com.aesc.proyectofinaldesarrollomovil.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentContainerView
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.ui.activities.LoginActivity

object Utils {
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

    fun getTimeAgo(time: Long): String? {
        val now: Long = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "just now"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "a minute ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS).toString() + " minutes ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "an hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            "yesterday"
        } else {
            (diff / DAY_MILLIS).toString() + " days ago"
        }
    }

    fun logsUtils(msg: String) = println("DEBUG --> $msg")

    fun statusProgress(status: Boolean, progressBar: FragmentContainerView) {
        progressBar.visibility = if (status) View.VISIBLE else View.GONE
    }

    fun dialogInfo(context: Context, msg: String) {
        var alertDialog1: AlertDialog? = null
        val dialogBuilder = AlertDialog.Builder(context)
        val layoutView: View =
            LayoutInflater.from(context).inflate(R.layout.custom_dialog_info, null)
        val mButtonSi = layoutView.findViewById<ImageView>(R.id.btnClose)
        val mTextViewMsg = layoutView.findViewById<TextView>(R.id.textView8)
        mTextViewMsg.text = msg
        mButtonSi.setOnClickListener {
            alertDialog1!!.dismiss()
        }
        dialogBuilder.setView(layoutView)
        alertDialog1 = dialogBuilder.create()
        alertDialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog1.show()
    }

    fun dialogError(context: Context, msg: String) {
        var alertDialog1: AlertDialog? = null
        val dialogBuilder = AlertDialog.Builder(context)
        val layoutView: View =
            LayoutInflater.from(context).inflate(R.layout.custom_dialog_error, null)
        val mButtonSi = layoutView.findViewById<ImageView>(R.id.btnClose)
        val mTextViewMsg = layoutView.findViewById<TextView>(R.id.textView8)
        mTextViewMsg.text = msg
        mButtonSi.setOnClickListener {
            alertDialog1!!.dismiss()
        }
        dialogBuilder.setView(layoutView)
        alertDialog1 = dialogBuilder.create()
        alertDialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog1.show()
    }

    fun dialogDeleteAccount(context: Context, msg: String) {
        var alertDialog1: AlertDialog? = null
        val dialogBuilder = AlertDialog.Builder(context)
        val layoutView: View =
            LayoutInflater.from(context).inflate(R.layout.custom_dialog_delete, null)
        val mButtonSi = layoutView.findViewById<ImageView>(R.id.btnClose)
        val mTextViewMsg = layoutView.findViewById<TextView>(R.id.textView8)
        mTextViewMsg.text = msg
        mButtonSi.setOnClickListener {
            alertDialog1!!.dismiss()
        }
        dialogBuilder.setView(layoutView)
        alertDialog1 = dialogBuilder.create()
        alertDialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog1.show()
    }
}