package com.aesc.proyectofinaldesarrollomovil.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aesc.proyectofinaldesarrollomovil.R
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

fun Activity.toast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun View.snack(
    color: String,
    message: String,
    length: Int = Snackbar.LENGTH_LONG/*, f: Snackbar.() -> Unit*/
) {
    val snack = Snackbar.make(this, message, length)
    val mView = snack.view
    mView.setBackgroundColor(Color.parseColor(color))
    val mTextView = mView.findViewById<View>(R.id.snackbar_text) as TextView
    mTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
    snack.show()
}

fun ImageView.loadByURL(url: String) = Glide.with(this)
    .load(url)
    .placeholder(R.drawable.ice_person_24)
    .error(R.drawable.ice_person_24)
    .fallback(R.drawable.ice_person_24)
    .into(this)


fun Snackbar.action(actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
    action(view.resources.getString(actionRes), color, listener)
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(ContextCompat.getColor(context, color)) }
}

inline fun <reified T : Activity> Activity.goToActivityF(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init()
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
    finish()
}

inline fun <reified T : Activity> Activity.goToActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

