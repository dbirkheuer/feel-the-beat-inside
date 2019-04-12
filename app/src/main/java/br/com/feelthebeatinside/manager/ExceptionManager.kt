package br.com.feelthebeatinside.manager

import android.content.Context
import br.com.feelthebeatinside.util.DialogUtil

class ExceptionManager {
    fun showSimpleAtention(context: Context, message: String) {
        DialogUtil().createSimpleOkDialog(context, message)
    }
}