package br.com.feelthebeatinside.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import br.com.feelthebeatinside.R

class CreateAccountActivity : AppCompatActivity() {

    lateinit var imageProfile: ImageView
    lateinit var editUserName: EditText
    lateinit var editUserEmail: EditText
    lateinit var editUserPassword: EditText
    lateinit var buttonCreateAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        mapFields()
    }

    private fun mapFields() {
        imageProfile = findViewById(R.id.imageProfile)
        editUserName = findViewById(R.id.editUserName)
        editUserEmail = findViewById(R.id.editUserEmail)
        editUserPassword = findViewById(R.id.editUserPassword)
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener {

        }
    }
}
