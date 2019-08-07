package com.egenesio.utils.ui.extensions

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.egenesio.utils.extensions.isDateValid
import com.egenesio.utils.extensions.isEmailValid

/**
 * Created by egenesio on 11/04/2018.
 */

/**
 * EDIT TEXT
 */

var EditText.inputText: String
    get() = text.toString()
    set(value) = setText(value)

val EditText.isEmpty: Boolean get() = inputText.trim().isEmpty()
val EditText.isNotEmpty: Boolean get() = inputText.trim().isNotEmpty()

fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

val EditText.isEmailValid: Boolean get() = inputText.isEmailValid
val EditText.isDateValid: Boolean get() = inputText.isDateValid
fun EditText.isDateValid(customFormat: String): Boolean = inputText.isDateValid(customFormat)
val EditText.isIntValid: Boolean get() = inputText.toIntOrNull() != null

var EditText.stringResource: String
    set(value) {
        context.resIdFrom(value)?.let {
            setText(it)
        } ?: run {
            setText(value)
        }
    }

    get() = text.toString() //TODO fix

inline fun EditText.setDoneButton(button: Button) {
    setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
        return@OnEditorActionListener if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
            hideKeyboard()
            button.callOnClick()
            true
        } else false
    })
}

inline fun EditText.disableInput() {
    //isEnabled = false
    isFocusable = false
    isFocusableInTouchMode = false
    //isClickable = false
    //isSelectable = false
}