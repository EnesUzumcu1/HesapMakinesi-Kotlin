package com.example.hesapmakinesi.ui.calculateViewPager2.buyandsell

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hesapmakinesi.data.model.Calculate
import com.example.hesapmakinesi.data.model.Coins
import com.example.hesapmakinesi.databinding.FragmentSellBinding
import com.example.hesapmakinesi.ui.calculateViewPager2.buyandsell.adapter.SavedDatasAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class SellFragment(private val bundle: Bundle?) : Fragment(), SavedDatasAdapter.OnClickListener {
    private lateinit var binding: FragmentSellBinding
    private lateinit var hesapArrayList: ArrayList<Calculate>
    private lateinit var decimalFormat: DecimalFormat
    private lateinit var decimalFormatYuzde: DecimalFormat
    private lateinit var adapter: SavedDatasAdapter
    private lateinit var coinYukle: String

    private lateinit var coinerArray: ArrayList<Coins>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        atamalar()
        loadData(coinYukle)
        buildrecyclerview()
        ortalamaHesapla()

        binding.btnEkle.setOnClickListener {
            val bosMuKontrol1 = binding.etAdet.text.toString() != ""
            val bosMuKontrol2 = binding.etFiyat.text.toString() != ""
            val noktaKontrol1 = binding.etAdet.text.toString() != "."
            val noktaKontrol2 = binding.etFiyat.text.toString() != "."
            if (bosMuKontrol1 && bosMuKontrol2 && noktaKontrol1 && noktaKontrol2) {
                veriEkle()
                adapter.notifyDataSetChanged()
                binding.recyclerview.smoothScrollToPosition(adapter.itemCount)
                binding.etAdet.setText("")
                binding.etFiyat.setText("")
                ortalamaHesapla()
                saveData(coinYukle)
            }
        }
    }

    private fun ortalamaHesapla() {
        var adet = 0.0
        var fiyatOrt = 0.0
        var gerceklesirsePara = 0.0
        for (i in hesapArrayList.indices) {
            adet += hesapArrayList[i].adet
            gerceklesirsePara += hesapArrayList[i].adet * hesapArrayList[i].fiyat
            fiyatOrt = gerceklesirsePara / adet
        }
        binding.textViewAdet.text = decimalFormat.format(adet)
        binding.textViewOrtalama.text = decimalFormat.format(fiyatOrt)
        binding.textViewGerceklesirsePara.text = decimalFormat.format(gerceklesirsePara)
        val hamPara = decimalFormat.format(BuyFragment.toplamHamPara).toDouble()
        binding.textViewBaslangictakiPara.text = hamPara.toString()
        if (gerceklesirsePara > 0) {
            val artisOrani = ((gerceklesirsePara - hamPara) / gerceklesirsePara * 100).toFloat()
            val netKar = (gerceklesirsePara - hamPara).toFloat()
            binding.textViewKarOrani.text = String.format(
                "%% %s (%s)",
                decimalFormatYuzde.format(artisOrani.toDouble()),
                decimalFormatYuzde.format(netKar.toDouble())
            )
            if (artisOrani > 0.0) {
                binding.textViewKarOrani.setTextColor(Color.GREEN)
            } else {
                binding.textViewKarOrani.setTextColor(Color.RED)
            }
        } else {
            binding.textViewKarOrani.text = "0"
        }
    }
    private fun atamalar() {
        bundle?.run {
            val coinAdi = this.getString("coinAdi")
            coinAdi?.let {
                coinYukle = it
            }
        }

        hesapArrayList = ArrayList<Calculate>()
        coinerArray = ArrayList()

        //Double ifadeler için ondalık ayırıcının nokta olması gerekiyor. Bunu sabit olarak uygulamak için kural eklendi
        val currentLocale = Locale.getDefault()
        val otherSymbols = DecimalFormatSymbols(currentLocale)
        otherSymbols.decimalSeparator = '.'
        otherSymbols.groupingSeparator = ','

        //end
        decimalFormat = DecimalFormat("#.#####", otherSymbols)
        decimalFormatYuzde = DecimalFormat("#.##", otherSymbols)
    }
    private fun veriEkle() {
        val adet = binding.etAdet.text.toString().toDouble()
        val fiyat = binding.etFiyat.text.toString().toDouble()
        if (adet != 0.0 && fiyat != 0.0) {
            hesapArrayList.add(Calculate(adet, fiyat))
        } else {
            Toast.makeText(context, "Kabul edilmeyen değer", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildrecyclerview() {
        adapter = SavedDatasAdapter(hesapArrayList, this)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.smoothScrollToPosition(adapter.itemCount)
    }

    private fun loadData(coinYukle: String) {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        val sharedPreferences = requireActivity().getSharedPreferences(coinYukle, Context.MODE_PRIVATE)

        // creating a variable for gson.
        val gson = Gson()

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences.getString("hesaplarSatis", null)

        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<Calculate>>() {}.type

        // in below line we are getting data from gson
        // and saving it to our array list
        json?.let {
            hesapArrayList = gson.fromJson(json, type)
        } ?: kotlin.run {
            // if the array list is empty
            // creating a new array list.
            hesapArrayList = ArrayList<Calculate>()
        }
    }

    private fun saveData(coinYukle: String) {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        val sharedPreferences = requireActivity().getSharedPreferences(coinYukle, Context.MODE_PRIVATE)

        // creating a variable for editor to
        // store data in shared preferences.
        val editor = sharedPreferences.edit()

        // creating a new variable for gson.
        val gson = Gson()

        // getting data from gson and storing it in a string.
        val json = gson.toJson(hesapArrayList)

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("hesaplarSatis", json)

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply()
    }

    override fun onItemClicked(position: Int) {
        //hesapArrayList.removeAt(position)
        adapter.removeItem(position)
        ortalamaHesapla()
        saveData(coinYukle)
    }
}