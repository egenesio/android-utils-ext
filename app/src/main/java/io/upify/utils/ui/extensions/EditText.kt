package io.upify.utils.ui.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import org.jetbrains.anko.isSelectable

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

var EditText.stringResource: String
    set(value) {
        context.resIdFrom(value)?.let {
            setText(it)
        } ?: run {
            setText(value)
        }
    }

    get() = text.toString() //TODO fix

fun EditText.disableInput() {
    //isEnabled = false
    isFocusable = false
    isFocusableInTouchMode = false
    //isClickable = false
    //isSelectable = false
}