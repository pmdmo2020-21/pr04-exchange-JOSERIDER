package es.iessaladillo.pedrojoya.exchange.utils

import android.content.Context
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import es.iessaladillo.pedrojoya.exchange.R


fun Editable?.isInvalid(): Boolean {
    val text = toString()
    return text.isEmpty() || text.startsWith(".")
}

fun Editable.isValid(): Boolean = !isInvalid() && !endsWith(".")


fun View.hideSoftKeyboard(): Boolean {
    val imm = context.getSystemService(
            Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return imm?.hideSoftInputFromWindow(windowToken, 0) ?: false
}


fun TextView.setColorWhenFocused(
        isFocused: Boolean,
        resIdFocusedColor: Int,
        resIdDefaultColor: Int = R.color.grey,
) {
    setTextColor(ContextCompat.getColor(context, if (isFocused) resIdFocusedColor else resIdDefaultColor))
}