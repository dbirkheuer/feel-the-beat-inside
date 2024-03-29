package br.com.feelthebeatinside.util

import android.content.Context
import android.widget.Toast

object ToastUtil {

    fun createShortToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}