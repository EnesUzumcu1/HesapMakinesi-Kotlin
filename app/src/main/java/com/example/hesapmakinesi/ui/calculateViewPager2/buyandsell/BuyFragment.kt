package com.example.hesapmakinesi.ui.calculateViewPager2.buyandsell

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.hesapmakinesi.data.model.Calculate
import com.example.hesapmakinesi.data.model.Coins
import com.example.hesapmakinesi.databinding.CustomDialogBoxBinding
import com.example.hesapmakinesi.databinding.CustomListBinding
import com.example.hesapmakinesi.databinding.FragmentBuyBinding
import com.example.hesapmakinesi.ui.calculateViewPager2.buyandsell.adapter.CoinsAdapter
import com.example.hesapmakinesi.ui.calculateViewPager2.buyandsell.adapter.SavedDatasAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class BuyFragment(private val bundle: Bundle?) : Fragment(), SavedDatasAdapter.OnClickListener, CoinsAdapter.OnClickListener {
    private lateinit var binding: FragmentBuyBinding
    private lateinit var navController: NavController

    private lateinit var hesapArrayList: ArrayList<Calculate>
    private lateinit var dialog: Dialog
    private lateinit var dialogCoin: Dialog
    private lateinit var decimalFormat: DecimalFormat
    private lateinit var decimalFormatYuzde: DecimalFormat
    private lateinit var adapter: SavedDatasAdapter
    private lateinit var adapterCoinler: CoinsAdapter
    private lateinit var mevcutCoinAdi: String
    private lateinit var coinYukle: String
    companion object{
        var toplamHamPara: Double =0.0
    }


    private lateinit var coinlerArray: ArrayList<Coins>

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBuyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        atamalar()
        loadData(coinYukle)
        buildrecyclerview()
        ortalamaHesapla()
        getData()

        if (binding.textViewYeniAdet.text.toString() != "" && binding.textViewYeniAdet.text.toString() != "Yeni adet için tıkla"
        ) {
            val str = binding.textViewYeniAdet.text.toString()
            val strings = str.split(" ").toTypedArray()
            yeniOrtalamaHesapla(strings[0].trim { it <= ' ' }.toDouble())
        }

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
                yuzdeHesapla()
                saveData(coinYukle)
            }
        }

        binding.textViewYeniAdet.setOnClickListener {
            showDialogAlert()
            saveData(coinYukle)
        }

        binding.toolbar.coinAdi.setOnClickListener {
            if (coinlerArray.size > 0) {
                showDialogAlertCoin()
            } else {
                //eger liste yüklenmezse tekrar denenmesi icin fonksiyon cagiriliyor.
                Toast.makeText(context, "Tekrar deneyin!", Toast.LENGTH_SHORT).show()
                getData()
            }
        }

        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                //yenile butonu yerine belirtilen sürede bir sürekli güncelleme yapacak
                getDataKaydedilenCoin(mevcutCoinAdi)
                yuzdeHesapla()
                handler.postDelayed(this, 6000)
            }
        }
    }

    private fun atamalar() {
        navController = findNavController()
        bundle?.run {
            val coinAdi = this.getString("coinAdi")
            coinAdi?.let {
                coinYukle = it
            }
        }
        hesapArrayList = ArrayList<Calculate>()
        coinlerArray = ArrayList()

        //Double ifadeler için ondalık ayırıcının nokta olması gerekiyor. Bunu sabit olarak uygulamak için kural eklendi
        val currentLocale = Locale.getDefault()
        val otherSymbols = DecimalFormatSymbols(currentLocale)
        otherSymbols.decimalSeparator = '.'
        otherSymbols.groupingSeparator = ','

        //end
        decimalFormat = DecimalFormat("#.#####", otherSymbols)
        decimalFormatYuzde = DecimalFormat("#.##", otherSymbols)
        dialog = Dialog(requireContext())
        dialogCoin = Dialog(requireContext())
        if (!isInternetAvailable()) {
            Toast.makeText(context, "İnternet bağlantısı bulunamadı!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ortalamaHesapla() {
        var adet = 0.0
        var fiyatOrt = 0.0
        var toplamPara = 0.0
        for (i in hesapArrayList.indices) {
            adet += hesapArrayList[i].adet
            toplamPara += hesapArrayList[i].adet * hesapArrayList[i].fiyat
            fiyatOrt = toplamPara / adet
        }
        binding.textViewAdet.text = decimalFormat.format(adet)
        binding.textViewOrtalama.text = decimalFormat.format(fiyatOrt)
        binding.textViewToplamPara.text = decimalFormat.format(toplamPara)
        toplamHamPara = toplamPara
    }

    private fun yeniOrtalamaHesapla(yeniAdet: Double) {
        var fark = yeniAdet - binding.textViewAdet.text.toString().toDouble()
        if (yeniAdet == 0.0) {
            fark = 0.0
        }
        binding.textViewYeniAdet.text =
            String.format("%s (%s)", decimalFormat.format(yeniAdet), decimalFormat.format(fark))
        if (yeniAdet > 0) {
            val yeniOrt = binding.textViewToplamPara.text.toString().toDouble() / yeniAdet
            binding.textViewYeniOrtalama.text = decimalFormat.format(yeniOrt)
            if (yeniOrt < binding.textViewOrtalama.text.toString().toDouble()) {
                binding.textViewYeniOrtalama.setTextColor(Color.GREEN)
            } else if (yeniOrt > binding.textViewOrtalama.text.toString().toDouble()) {
                binding.textViewYeniOrtalama.setTextColor(Color.RED)
            }
        }
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

    private fun showDialogAlert() {
        val bindingDialogBox: CustomDialogBoxBinding =
            CustomDialogBoxBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialogBox.root)
        bindingDialogBox.btnKaydet.setOnClickListener {
            if (bindingDialogBox.tvYeniAdet.text.toString() != "") {
                val yeniadetsayisi: Double =
                    bindingDialogBox.tvYeniAdet.text.toString().toDouble()
                if (yeniadetsayisi != 0.0) {
                    val yeniAdet: Double =
                        bindingDialogBox.tvYeniAdet.text.toString().toDouble()
                    yeniOrtalamaHesapla(yeniAdet)
                    yuzdeHesapla()
                } else {
                    yeniOrtalamaSifirlayici()
                }
                saveData(coinYukle)
            }
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun yeniOrtalamaSifirlayici() {
        yeniOrtalamaHesapla(0.0)
        binding.textViewKarYuzdeKarDahil.text = "% 0"
        binding.textViewYeniOrtalama.text = "0"
        binding.textViewKarYuzdeKarDahil.setTextColor(Color.BLACK)
        binding.textViewYeniOrtalama.setTextColor(Color.BLACK)
        binding.textViewKarliBakiye.text = "0"
    }

    private fun showDialogAlertCoin() {
        val listBinding: CustomListBinding = CustomListBinding.inflate(layoutInflater)
        dialogCoin.setContentView(listBinding.root)
        coinlerArray.sortBy {
            it.isim
        }
        adapterCoinler = CoinsAdapter(coinlerArray, this)
        listBinding.recyclerViewCoinler.setHasFixedSize(true)
        listBinding.recyclerViewCoinler.adapter = adapterCoinler

        listBinding.svCoins.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapterCoinler.filter.filter(p0)
                return true
            }
        })

        dialogCoin.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogCoin.show()
    }

    private fun loadData(coinYukle: String) {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        val sharedPreferences =
            requireActivity().getSharedPreferences(coinYukle, Context.MODE_PRIVATE)

        // creating a variable for gson.
        val gson = Gson()

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences.getString("hesaplar", null)

        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<Calculate?>?>() {}.type

        // in below line we are getting data from gson
        // and saving it to our array list
        json?.let {
            hesapArrayList = gson.fromJson<ArrayList<Calculate>>(json, type)
        } ?: kotlin.run {
            // checking below if the array list is empty or not
            hesapArrayList = ArrayList<Calculate>()
        }

        val yeniOrt = sharedPreferences.getString("yeniAdet", "Yeni adet için tıkla")
        binding.textViewYeniAdet.text = yeniOrt
        val coinAdi = sharedPreferences.getString("coinAdi", "BTCUSDT")
        coinAdi?.let {
            getDataKaydedilenCoin(it)
        } ?: kotlin.run {
            getDataKaydedilenCoin("BTCUSDT")
        }
        mevcutCoinAdi = coinAdi.toString()
    }

    private fun saveData(coinYukle: String) {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        val sharedPreferences =
            requireActivity().getSharedPreferences(coinYukle, Context.MODE_PRIVATE)

        // creating a variable for editor to
        // store data in shared preferences.
        val editor = sharedPreferences.edit()

        // creating a new variable for gson.
        val gson = Gson()

        // getting data from gson and storing it in a string.
        val json = gson.toJson(hesapArrayList)

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("hesaplar", json)
        editor.putString("yeniAdet", binding.textViewYeniAdet.text.toString())
        if (binding.toolbar.coinAdi.text.toString() != "") {
            editor.putString("coinAdi", binding.toolbar.coinAdi.text.toString())
        }

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply()
    }

    private fun getDataKaydedilenCoin(coinAdi: String) {
        val requestQueue1: RequestQueue = Volley.newRequestQueue(context)
        val url = "https://api1.binance.com/api/v3/ticker/price?symbol=$coinAdi"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val json = JSONObject(response.toString())
                    binding.toolbar.coinAdi.text = json.getString("symbol")
                    binding.toolbar.coinFiyati.text =
                        decimalFormat.format(json.getString("price").toDouble()) + " $"
                    yuzdeHesapla()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { }
        requestQueue1.add(jsonObjectRequest)
    }

    private fun getData() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, "https://api1.binance.com/api/v3/ticker/price", null,
            { response ->
                try {
                    coinlerArray.clear()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        if (jsonObject.getString("symbol").contains("USDT")) {
                            val coins = Coins(
                                jsonObject.getString("symbol"),
                                jsonObject.getString("price").toDouble()
                            )
                            coinlerArray.add(coins)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        ) { }
        requestQueue.add(jsonArrayRequest)
    }

    fun yuzdeHesapla() {
        val anlikDegerstr = binding.toolbar.coinFiyati.text.toString()
        if (anlikDegerstr != "") {
            val str1 = anlikDegerstr.split(" ").toTypedArray()
            val anlikDeger = str1[0].toDouble()
            var karliOrtalama = 0.0
            var karsizOrtalama = 0.0
            var netHamKar = 0.0
            var netKarDahilKar = 0.0
            var karliArtisOrani = 0f
            var karsizArtisOrani = 0f
            val toplamPara = binding.textViewToplamPara.text.toString().toDouble()
            if (binding.textViewYeniOrtalama.text.toString() == "" || binding.textViewYeniOrtalama.text.toString()
                    .toDouble() == 0.0
            ) {
            } else {
                karliOrtalama = binding.textViewYeniOrtalama.text.toString().toDouble()
                karliArtisOrani = ((anlikDeger - karliOrtalama) / karliOrtalama * 100).toFloat()
                netKarDahilKar = toplamPara * karliArtisOrani / 100
            }
            if (binding.textViewOrtalama.text.toString().toDouble() == 0.0) {
            } else {
                karsizOrtalama = binding.textViewOrtalama.text.toString().toDouble()
                karsizArtisOrani = ((anlikDeger - karsizOrtalama) / karsizOrtalama * 100).toFloat()
                netHamKar = toplamPara * karsizArtisOrani / 100
            }
            binding.textViewKarYuzde.text = String.format(
                "%% %s (%s)",
                decimalFormatYuzde.format(karsizArtisOrani.toDouble()),
                decimalFormatYuzde.format(netHamKar)
            )
            binding.textViewKarYuzdeKarDahil.text = String.format(
                "%% %s (%s)",
                decimalFormatYuzde.format(karliArtisOrani.toDouble()),
                decimalFormatYuzde.format(netKarDahilKar)
            )
            if (karsizArtisOrani > 0.0) {
                binding.textViewKarYuzde.setTextColor(Color.GREEN)
            } else {
                binding.textViewKarYuzde.setTextColor(Color.RED)
            }
            if (karliArtisOrani > 0.0) {
                binding.textViewKarYuzdeKarDahil.setTextColor(Color.GREEN)
            } else {
                binding.textViewKarYuzdeKarDahil.setTextColor(Color.RED)
            }
            anlikBakiyeHesaplama(str1)
        } else {
            Toast.makeText(
                context,
                "Kâr oranı hesaplanması için internet bağlantısı gerekir",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun anlikBakiyeHesaplama(guncelFiyatstr: Array<String>) {
        val karsizAdet = binding.textViewAdet.text.toString().toDouble()
        var karliAdet = 0.0
        if (binding.textViewYeniAdet.text.toString() != "" && binding.textViewYeniAdet.text.toString() != "Yeni adet için tıkla"
        ) {
            val str = binding.textViewYeniAdet.text.toString()
            val strings = str.split(" ").toTypedArray()
            karliAdet = strings[0].trim { it <= ' ' }.toDouble()
        }
        val guncelFiyat = guncelFiyatstr[0].toDouble()
        binding.textViewKarliBakiye.text = decimalFormat.format(karliAdet * guncelFiyat)
        binding.textViewKarsizBakiye.text = decimalFormat.format(karsizAdet * guncelFiyat)
    }

    /*
    private fun isConnected(): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return if (networkInfo != null) {
            if (networkInfo.isConnected) true else false
        } else false
    }

     */
    private fun isInternetAvailable(): Boolean {
        var result = false
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 6000)
    }

    override fun onItemClickedCoinlerList(coins: Coins) {
        binding.toolbar.coinAdi.text = coins.isim
        binding.toolbar.coinFiyati.text =
            String.format("%s $", decimalFormat.format(coins.fiyat))
        mevcutCoinAdi = coins.isim

        saveData(coinYukle)

        adapterCoinler.getFilter().filter("")
        yuzdeHesapla()
        dialogCoin.dismiss()
    }

    override fun onItemClicked(position: Int) {
        //hesapArrayList.removeAt(position)
        adapter.removeItem(position)
        ortalamaHesapla()
        yuzdeHesapla()
        saveData(coinYukle)
        if (adapter.itemCount == 0) {
            yeniOrtalamaSifirlayici()
        }

    }
}