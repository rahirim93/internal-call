package com.example.internalcall

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment

import com.parse.ParseObject
import com.parse.ParseQuery

class EditActivity : AppCompatActivity() {

    private var phoneObject: ParseObject? = null
    private lateinit var editTextName: EditText
    private lateinit var editTextSecond: EditText
    private lateinit var editTextSurname: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextName = findViewById(R.id.editTextName)
        editTextSecond = findViewById(R.id.editTextSecond)
        editTextSurname = findViewById(R.id.editTextSurname)
        editTextNumber = findViewById(R.id.editTextNumber)

        buttonSave = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            if (phoneObject == null) {
                phoneObject = ParseObject("Phone")
            }
            phoneObject!!.put("name", editTextName.getText().toString())
            phoneObject!!.put("second", editTextSecond.getText().toString())
            phoneObject!!.put("surname", editTextSurname.getText().toString())
            phoneObject!!.put("number", editTextNumber.getText().toString())
            phoneObject!!.saveInBackground {
                if (it == null) {
                    finish()
                }
            }

        }

        buttonDelete = findViewById(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            if (phoneObject == null) {
                finish()
            } else {
                phoneObject!!.deleteInBackground {
                    if (it == null) {
                        finish()
                    } else {
                    }
                }
            }
        }

        val name = intent.getStringExtra("name")
        val second = intent.getStringExtra("second")
        val surname = intent.getStringExtra("surname")
        val number = intent.getStringExtra("number")
        val query = ParseQuery<ParseObject>("Phone")
        query.whereContains("name", "${name.toString()}")
        query.whereContains("second", "${second.toString()}")
        query.whereContains("surname", "${surname.toString()}")
        query.whereContains("number", "${number.toString()}")
        query.findInBackground { objects, e ->
            if (e == null) {
                objects.forEach {
                    phoneObject = it
                    editTextName.setText(it.getString("name"))
                    editTextSecond.setText(it.getString("second"))
                    editTextSurname.setText(it.getString("surname"))
                    editTextNumber.setText(it.getString("number"))
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()

            }

        }
    }
}