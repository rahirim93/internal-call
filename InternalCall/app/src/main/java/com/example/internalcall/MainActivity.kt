package com.example.internalcall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internalcall.databinding.ActivityMainBinding
import com.parse.ParseObject
import com.parse.ParseQuery

class MainActivity : AppCompatActivity() {

    private var adapter: PhoneAdapter? = PhoneAdapter(emptyList())
    private lateinit var searchView: SearchView
    private lateinit var binding: ActivityMainBinding


    override fun onResume() {
        super.onResume()
        getItems()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        getItems()

        binding.buttonAdd.setOnClickListener {
            val intent = Intent(applicationContext, EditActivity::class.java)
            startActivity(intent)
        }

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val query = ParseQuery<ParseObject>("Phone")
                query.whereContains("surname", p0)
                query.findInBackground { parseObjects, parseException ->
                    if (parseException == null) {
                        binding.recyclerView.adapter = PhoneAdapter(parseObjects)
                    } else {
                        Log.d("tag", "error")
                    }
                }
                return true
            }

        })

    }

    private fun getItems() {
        val query = ParseQuery.getQuery<ParseObject>("Phone")
        query.findInBackground { parseObjects, parseException ->
            if (parseException == null) {
                //We are initializing odo object list to our adapter
                parseObjects.forEach {
                    Log.d("tag", it.getString("name")!!)
                    Log.d("tag", it.getString("second")!!)
                    Log.d("tag", it.getString("surname")!!)
                    Log.d("tag", it.getString("number")!!)
                }
                binding.recyclerView.adapter = PhoneAdapter(parseObjects)
            } else {
                Log.d("tag", "error")
            }
        }
    }

    private inner class PhoneHolder(view: View):
        RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private var name: TextView = itemView.findViewById(R.id.textViewName)
        private var secondName: TextView = itemView.findViewById(R.id.textViewSecondName)
        private var surname: TextView = itemView.findViewById(R.id.textViewSurname)
        private var number: TextView = itemView.findViewById(R.id.textView4)
        private var image: ImageView = itemView.findViewById(R.id.imageView)
        private var imageEdit: ImageView = itemView.findViewById(R.id.imageView2)

        private lateinit var phoneO: ParseObject

        init {
            itemView.setOnClickListener(this)
            image.setOnClickListener {

                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+74952866000," + phoneO.getString("number")))
                startActivity(intent)
                Toast.makeText(applicationContext, "test", Toast.LENGTH_SHORT).show()
            }
            imageEdit.setOnClickListener{
                val intent = Intent(applicationContext, EditActivity::class.java)
                intent.putExtra("name", phoneO.getString("name"))
                intent.putExtra("second", phoneO.getString("second"))
                intent.putExtra("surname", phoneO.getString("surname"))
                intent.putExtra("number", phoneO.getString("number"))
                startActivity(intent)
            }
        }

        override fun onClick(p0: View?) {
//            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+74952866000," + phoneO.getString("number")))
//            startActivity(intent)
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+74952866000," + phoneO.getString("number")))
            startActivity(intent)
        }

        fun bind(phone: ParseObject) {
            phoneO = phone
            name.text = phone.getString("name")
            secondName.text = phone.getString("second")
            surname.text = phone.getString("surname")
            number.text = phone.getString("number")
        }


    }

    private inner class PhoneAdapter(var phones: List<ParseObject>) : RecyclerView.Adapter<PhoneHolder> (){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneHolder {
            val view = layoutInflater.inflate(R.layout.list_item, parent, false)
            return PhoneHolder(view)
        }

        override fun getItemCount() = phones.size

        override fun onBindViewHolder(holder: PhoneHolder, position: Int) {
            var phone = phones[position]
            holder.bind(phone)
        }

    }
}