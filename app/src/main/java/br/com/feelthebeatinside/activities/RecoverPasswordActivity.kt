package br.com.feelthebeatinside.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import br.com.feelthebeatinside.R

class RecoverPasswordActivity : AppCompatActivity() {

    lateinit var editEmail: EditText
    lateinit var buttonSend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)
        mapFields()
    }

    private fun mapFields() {
        editEmail = findViewById(R.id.editEmail)
        buttonSend = findViewById(R.id.buttonSend)
        buttonSend.setOnClickListener {

        }
    }
}
