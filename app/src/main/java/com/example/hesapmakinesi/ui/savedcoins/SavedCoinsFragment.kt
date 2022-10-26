package com.example.hesapmakinesi.ui.savedcoins

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.hesapmakinesi.R
import com.example.hesapmakinesi.data.model.Calculate
import com.example.hesapmakinesi.data.model.ListSizeControl
import com.example.hesapmakinesi.data.model.SavedCoins
import com.example.hesapmakinesi.databinding.FragmentSavedCoinsBinding
import com.example.hesapmakinesi.ui.savedcoins.adapter.SavedCoinsDiffutilAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SavedCoinsFragment : Fragment(), SavedCoinsDiffutilAdapter.OnClickListenerSavedCoins {
    private lateinit var binding: FragmentSavedCoinsBinding
    private lateinit var navController: NavController

    private var kayitliCoinlers: ArrayList<SavedCoins> = java.util.ArrayList<SavedCoins>()
    private var stringsID: ArrayList<ListSizeControl> = java.util.ArrayList()

    private lateinit var adapter: SavedCoinsDiffutilAdapter

    private var tiklananPosition = 0

    //resume ilke defa çalışınca istenenleri yapmaması icin kullanıldı
    private var i = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedCoinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        atamalar()
        buildrecyclerview()

        binding.yeniCoinEkleButton.setOnClickListener {
            veriKontrol()
            binding.listeRecyclerview.smoothScrollToPosition(adapter.itemCount)
            saveData()
            binding.toolbar.notSayisi.text = kayitliCoinlers.size.toString() + "/20"
            gorunurlukDegistir()
        }
    }

    private fun coinEkle(isim: String, adet: Int, id: String, idPosition: Int) {
        kayitliCoinlers.add(SavedCoins(isim, id, adet, idPosition))
    }

    private fun veriKontrol() {
        var id = ""
        var i = 0
        try {
            while (i < 21) {
                if (stringsID[i].durum) {
                    id = stringsID[i].id
                    stringsID[i].durum = false
                    break
                }
                i++
            }

            val preferences: SharedPreferences =
                requireActivity().getSharedPreferences(id, Context.MODE_PRIVATE)
            val isim = preferences.getString("coinAdi", "Eklemek için tıkla")
            var hesapArrayList: java.util.ArrayList<Calculate> = java.util.ArrayList()
            val gson = Gson()
            val json = preferences.getString("hesaplar", null)
            // below line is to get the type of our array list.
            val type = object : TypeToken<java.util.ArrayList<Calculate>>() {}.type
            // in below line we are getting data from gson
            // and saving it to our array list
            json?.let {
                hesapArrayList = gson.fromJson(it, type)
            }
            val adet = hesapArrayList.size

            if (id != "") {
                coinEkle(isim.toString(), adet, id, i)
            } else {
                Toast.makeText(requireActivity(), "Liste Dolu", Toast.LENGTH_SHORT).show()
            }
        } catch (_: Exception) {
        }
    }

    private fun atamalar() {
        navController = findNavController()
        kayitliCoinlers = java.util.ArrayList<SavedCoins>()
        stringsID = java.util.ArrayList()
        loadData()
        if (stringsID.size == 0) {
            //20 adet not eklemek için sınır koyulacak
            for (i in 1..20) {
                stringsID.add(ListSizeControl("Note$i", true))
                //durum true ise kullanılmamış bir id, eğer false ise id kullanılıyor
            }
        }
        gorunurlukDegistir()
        binding.toolbar.notSayisi.text = kayitliCoinlers.size.toString() + "/20"
    }

    private fun gorunurlukDegistir() {
        if (kayitliCoinlers.size == 0) {
            binding.listeRecyclerview.visibility = View.GONE
            binding.textViewBosUyarisi.visibility = View.VISIBLE
        } else {
            binding.listeRecyclerview.visibility = View.VISIBLE
            binding.textViewBosUyarisi.visibility = View.GONE
        }

    }

    private fun buildrecyclerview() {
        adapter = SavedCoinsDiffutilAdapter(this)
        binding.listeRecyclerview.setHasFixedSize(true)
        binding.listeRecyclerview.adapter = adapter.apply {
            submitList(kayitliCoinlers)
        }
        binding.listeRecyclerview.smoothScrollToPosition(adapter.itemCount)
    }

    override fun onItemClickedSavedCoins(savedCoins: SavedCoins, position: Int, islemID: Int) {
        if (islemID == 1) {
            stringsID[savedCoins.idPosition] =  ListSizeControl(savedCoins.id,true)
            kayitliCoinlers.removeAt(position)
            adapter.notifyDataSetChanged()

            //Verileri hafızan kalıcı olarak siliyor
            val sharedPreferences: SharedPreferences =
                requireActivity().getSharedPreferences(savedCoins.id, Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            saveData()
            gorunurlukDegistir()
        } else if (islemID == 2) {
            tiklananPosition = position

            navController.navigate(R.id.calculateFragment, Bundle().apply {
                val value: String = savedCoins.id
                putString("coinAdi", value)
            }
            )
        }
        binding.toolbar.notSayisi.text = kayitliCoinlers?.size.toString() + "/20"
    }

    private fun loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("Liste", Context.MODE_PRIVATE)

        // creating a variable for gson.
        val gson = Gson()
        val gson2 = Gson()

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences.getString("listeler", null)
        val json2 = sharedPreferences.getString("IDler", null)

        // below line is to get the type of our array list.
        val type = object : TypeToken<java.util.ArrayList<SavedCoins?>?>() {}.type
        val type2 = object : TypeToken<java.util.ArrayList<ListSizeControl?>?>() {}.type

        // in below line we are getting data from gson
        // and saving it to our array list
        json?.let {
            type?.let {
                kayitliCoinlers = gson.fromJson<java.util.ArrayList<SavedCoins>>(json, type)
            }
        }

        json2?.let {
            type2?.let {
                stringsID = gson2.fromJson(json2, type2)
            }
        }
    }

    private fun saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("Liste", Context.MODE_PRIVATE)

        // creating a variable for editor to
        // store data in shared preferences.
        val editor = sharedPreferences.edit()

        // creating a new variable for gson.
        val gson = Gson()
        val gson2 = Gson()

        // getting data from gson and storing it in a string.
        val json = gson.toJson(kayitliCoinlers)
        val json2 = gson2.toJson(stringsID)

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("listeler", json)
        editor.putString("IDler", json2)

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply()
    }

    override fun onResume() {
        updateData()
        super.onResume()
    }
    //Alım sayısını guncellemek icin
    private fun updateData(){
        if (i != 0) {
            val preferences: SharedPreferences = requireActivity().getSharedPreferences(
                kayitliCoinlers[tiklananPosition].id, Context.MODE_PRIVATE
            )
            val isim = preferences.getString("coinAdi", "Eklemek için tıkla")
            var hesapArrayList: java.util.ArrayList<Calculate> = java.util.ArrayList()
            val gson = Gson()
            val json = preferences.getString("hesaplar", null)
            // below line is to get the type of our array list.
            val type = object : TypeToken<java.util.ArrayList<Calculate>?>() {}.type
            // in below line we are getting data from gson
            // and saving it to our array list
            json?.let {
                hesapArrayList = gson.fromJson(it, type)
            }

            val adet = hesapArrayList.size

            kayitliCoinlers[tiklananPosition].adet = adet
            kayitliCoinlers[tiklananPosition].isim = isim.toString()

            saveData()
        }
        i++
    }
}
