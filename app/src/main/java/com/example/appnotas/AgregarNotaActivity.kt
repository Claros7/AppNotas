package com.example.appnotas

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_agregar_nota.*
import java.lang.Exception

class AgregarNotaActivity : AppCompatActivity() {

    val dbTabla = "Notas"
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_nota)

        try {
            val bundle:Bundle = intent.extras!!
            id = bundle.getInt("ID", 0)
            if (id!=0){
                titleEt.setText(bundle.getString("name"))
                descEt.setText(bundle.getString("des"))
            }
        }catch (ex:Exception){}
    }

    fun addFunc(view: View) {
        var dbManager = DbManager(this)

        var values = ContentValues()
        values.put("Titulo", titleEt.text.toString())

        if (id ==0){
            val ID = dbManager.insert(values)
            if (ID>0){
            Toast.makeText(this, "Nota agregada", Toast.LENGTH_SHORT).show()
            finish()
        }
        else{
                Toast.makeText(this, "Error al agregar la nota...", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            var selectionArgs = arrayOf(id.toString())
            val ID = dbManager.update(values, "ID=?", selectionArgs)
            if (ID>0){
                Toast.makeText(this, "Nota agregada", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "Error al agregar la nota", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
