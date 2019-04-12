package br.com.feelthebeatinside.util

import android.app.AlertDialog
import android.content.Context
import br.com.feelthebeatinside.R

class DialogUtil {
    fun createSimpleOkDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.atention))
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.ok)) { myDialog_, which ->

        }
        val dialog: AlertDialog = builder.create()

        dialog.show()
    }
}