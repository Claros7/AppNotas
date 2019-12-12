package com.example.appnotas

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Nota>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }


    private fun LoadQuery(title: String){
        val dbManager = DbManager(this)
        val projections = arrayOf("ID", "Titulo", "Descripcion")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Titulo como?", selectionArgs, "Titulo")
        listNotes.clear()
        if (cursor.moveToFirst()){

            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Titulo = cursor.getString(cursor.getColumnIndex("Titulo"))
                val Descripcion = cursor.getString(cursor.getColumnIndex("Descripcion"))

                listNotes.add(Nota(ID, Titulo, Descripcion))

            }while (cursor.moveToNext())
        }
        
        val myNotesAdapter = MyNotesAdapter(this, listNotes)

        notesLv.adapter = myNotesAdapter

        val total = notesLv.count
        val mActionBar = supportActionBar
        if (mActionBar != null){
            mActionBar.subtitle = "Tienes $total nota(s) en lista..."
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val sv: SearchView = menu!!.findItem(R.id.app_bar_buscar).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%"+query+"%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%"+newText+"%")
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addNote->{
                startActivity(Intent(this, AgregarNotaActivity::class.java))
            }
            R.id.action_opciones->{
                Toast.makeText(this, "Opciones", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter(context: Context, var listNotesAdapter: ArrayList<Nota>) :
        BaseAdapter() {
        var context:Context?= context


        @SuppressLint("ViewHolder", "InflateParams")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.row, null)
            val myNota = listNotesAdapter[position]
            myView.titleTv.text = myNota.nodeName
            myView.descTV.text = myNota.nodeDes
            myView.borrarBtn.setOnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNota.nodeID.toString())
                dbManager.delete("ID=?", selectionArgs)
                LoadQuery("%")
            }
            myView.editarBtn.setOnClickListener {
                GoToUpdateFun(myNota)
            }
            myView.copiarBtn.setOnClickListener {

                val title = myView.titleTv.text.toString()
                val desc = myView.descTV.text.toString()
                val s = title +"\n"+ desc
                val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cb.text = s
                Toast.makeText(this@MainActivity, "Copiado...", Toast.LENGTH_SHORT).show()
            }
            myView.compartirBtn.setOnClickListener {

                val title = myView.titleTv.text.toString()
                val desc = myView.descTV.text.toString()
                val s = title +"\n"+ desc
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }

    }

    private fun GoToUpdateFun(myNota: Nota) {
        val intent = Intent(this, AgregarNotaActivity::class.java)
        intent.putExtra("ID", myNota.nodeID)
        intent.putExtra("name",myNota.nodeName)
        intent.putExtra("des", myNota.nodeDes)
        startActivity(intent)

    }
}


