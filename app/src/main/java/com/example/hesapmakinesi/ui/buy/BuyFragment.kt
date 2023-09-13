package com.example.hesapmakinesi.ui.buy

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hesapmakinesi.R
import com.example.hesapmakinesi.data.model.Order
import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.databinding.CustomListBinding
import com.example.hesapmakinesi.databinding.FragmentBuyBinding
import com.example.hesapmakinesi.ui.buy.adapter.CoinsAdapter
import com.example.hesapmakinesi.utils.LoadingProgressBar
import com.example.hesapmakinesi.ui.sell.adapter.SavedDatasAdapter
import com.example.hesapmakinesi.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class BuyFragment : Fragment(), SavedDatasAdapter.OnClickListener,
    CoinsAdapter.OnClickListener {
    private lateinit var binding: FragmentBuyBinding

    private val viewModel by viewModels<BuyViewModel>()

    private lateinit var loadingProgressBar: LoadingProgressBar

    private lateinit var ordersArrayList: ArrayList<Order>
    private lateinit var dialogNewAmount: Dialog
    private lateinit var dialogCoinsList: Dialog
    private lateinit var dfPriceAndAmount: DecimalFormat

    private lateinit var dfPercentage: DecimalFormat
    private lateinit var otherSymbols: DecimalFormatSymbols
    private lateinit var adapterOrders: SavedDatasAdapter
    private lateinit var adapterCoinsList: CoinsAdapter
    private lateinit var coinName: String

    private lateinit var coinDetailJob: Job

    private var coinPrice: BigDecimal = BigDecimal(0)

    companion object {
        var totalMoney = BigDecimal(0)
        lateinit var dfAverage: DecimalFormat
    }

    private var amount = BigDecimal(0)
    private var priceAverage = BigDecimal(0)
    private var newAmount = BigDecimal(-1)
    private var newPriceAverage = BigDecimal(0)

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
        loadData()
        buildRecyclerView()
        averageCalculate()
        decimalFormatUpdate()
        newAverageCalculate()

        binding.btnEkle.setOnClickListener {
            if (inputCheck(binding.etAdet) && inputCheck(binding.etFiyat)) {
                addOrder()
            } else {
                Toast.makeText(context, Constants.WRONG_INPUT_ERROR, Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvYeniAdet.setOnClickListener {
            findNavController().navigate(R.id.addOrderDialogFragment)
        }

        binding.toolbar.coinAdi.setOnClickListener {
            viewModel.getCoinList()
        }

        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiEventDetail.collect {
                    when (it) {
                        is BuyViewEventCoinDetail.ShowData -> {
                            it.data.price?.toBigDecimal()
                                ?.let { safePrice ->
                                    calculatePercentage(safePrice)
                                    coinPrice = safePrice
                                    binding.toolbar.coinFiyati.text =
                                        String.format(
                                            "%s $",
                                            dfPriceAndAmount.format(safePrice)
                                        )
                                }
                        }
                        is BuyViewEventCoinDetail.ShowError -> {
                            Toast.makeText(
                                context,
                                Constants.PROFIT_COULD_NOT_BE_CALCULATED_ERROR,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is BuyViewEvent.ShowData -> {
                            coinDetailJob.cancel()
                            showDialogAlertCoin(it.data)
                        }
                        is BuyViewEvent.ShowError -> {
                            Toast.makeText(
                                requireContext(),
                                it.error.toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
            launch {
                viewModel.uiState.collect {
                    when (it) {
                        is BuyUiState.Loading -> {
                            loadingProgressBar.show()
                        }
                        is BuyUiState.Empty -> {
                            loadingProgressBar.cancel()
                        }
                    }
                }
            }
        }

        val currentFragment = findNavController().getBackStackEntry(R.id.calculateFragment)
        val dialogObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && currentFragment.savedStateHandle.contains("newAmount")) {
                getNewAmountFromDialog(currentFragment.savedStateHandle["newAmount"]!!)
            }
        }

        val dialogLifecycle = currentFragment.lifecycle
        dialogLifecycle.addObserver(dialogObserver)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                dialogLifecycle.removeObserver(dialogObserver)
            }
        })
    }

    private fun getCoinDetail(): Job {
        return lifecycleScope.launch {
            while (isActive) {
                viewModel.getCoinDetail(coinName)
                delay(2000)
            }
        }
    }

    private fun atamalar() {
        loadingProgressBar = LoadingProgressBar(requireContext())
        arguments?.run {
            val coinAdi = this.getString(Constants.SEND_PREF_NAME)
            coinAdi?.let {
                viewModel.preferencesName = it
            }
        }
        ordersArrayList = ArrayList()

        //Double ifadeler için ondalık ayırıcının nokta olması gerekiyor. Bunu sabit olarak uygulamak için kural eklendi
        val currentLocale = Locale.getDefault()
        otherSymbols = DecimalFormatSymbols(currentLocale)
        otherSymbols.decimalSeparator = '.'
        otherSymbols.groupingSeparator = ','

        //end
        dfPriceAndAmount = DecimalFormat("#.########", otherSymbols)
        dfAverage = DecimalFormat("#.##", otherSymbols)
        dfPercentage = DecimalFormat("#.##", otherSymbols)
        dialogNewAmount = Dialog(requireContext())
        dialogCoinsList = Dialog(requireContext())
        if (!isInternetAvailable()) {
            Toast.makeText(context, Constants.NO_INTERNET_ERROR, Toast.LENGTH_SHORT).show()
        }
    }

    private fun decimalFormatUpdate(): Job {
        return lifecycleScope.launch {
            while (coinPrice == BigDecimal(0)) {
                delay(2000)
            }
            if (coinPrice > BigDecimal(0)) {
                updateDecimalFormat(coinPrice.toString())
                decimalFormatUpdate().cancel()
            }
        }
    }

    private fun updateDecimalFormat(priceStr: String) {
        //noktadan sonra olan fazladan sıfırları silip kalan basamak sayısını bulup uygun decimalFormat ayarlanıyor.
        var splittedPriceFromDot = ""
        if (priceStr.contains(".")) {
            //ex:12.41 - '.' varsa kontrol ediliyor
            val afterDotDigitCount = priceStr.split(".").toTypedArray()
            splittedPriceFromDot = afterDotDigitCount.last()
        }
        //'.' olmayan durumlarda direkt 0 dönmesi için değer atanmadı.

        while (splittedPriceFromDot.lastOrNull() == '0') {
            splittedPriceFromDot = splittedPriceFromDot.dropLast(1)
        }

        when (splittedPriceFromDot.length) {
            0, 1, 2 -> dfAverage = DecimalFormat("#.##", otherSymbols)
            3 -> dfAverage = DecimalFormat("#.###", otherSymbols)
            4 -> dfAverage = DecimalFormat("#.####", otherSymbols)
            5 -> dfAverage = DecimalFormat("#.#####", otherSymbols)
            6 -> dfAverage = DecimalFormat("#.######", otherSymbols)
            7 -> dfAverage = DecimalFormat("#.#######", otherSymbols)
            8 -> dfAverage = DecimalFormat("#.########", otherSymbols)
        }

        averageCalculate()
        newAverageCalculate()
    }

    private fun averageCalculate() {
        amount = BigDecimal(0)
        priceAverage = BigDecimal(0)
        totalMoney = BigDecimal(0)
        for (order in ordersArrayList) {
            amount += order.adet
            totalMoney += order.adet * order.fiyat
            priceAverage = totalMoney.divide(amount, 8, RoundingMode.HALF_EVEN)
        }
        binding.tvAdet.text = dfPriceAndAmount.format(amount)
        binding.tvOrtalama.text = dfAverage.format(priceAverage)
        binding.tvToplamPara.text = dfPercentage.format(totalMoney)
    }

    private fun newAverageCalculate() {
        if (newAmount > BigDecimal(0)) {
            val fark = newAmount - amount

            binding.tvYeniAdet.text =
                String.format(
                    "%s (%s)",
                    dfPriceAndAmount.format(newAmount),
                    dfPriceAndAmount.format(fark)
                )

            newPriceAverage = totalMoney.divide(newAmount, 8, RoundingMode.HALF_EVEN)
            binding.tvYeniOrtalama.text = dfAverage.format(newPriceAverage)

            updateTextColor(priceAverage, newPriceAverage, binding.tvYeniOrtalama)
        }
    }

    private fun updateTextColor(value1: BigDecimal, value2: BigDecimal, textView: TextView) {
        if (value1 > value2) {
            textView.setTextColor(Color.GREEN)
        } else if (value1 < value2) {
            textView.setTextColor(Color.RED)
        } else {
            textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.defaultTextColor
                )
            )
        }
    }

    private fun inputCheck(editText: EditText): Boolean {
        return if (editText.text.trim().isEmpty()) false
        else {
            if (editText.text.trim().toString() == ".") false
            else {
                editText.text.trim().toString().toBigDecimal() > BigDecimal(0)
            }
        }
    }

    private fun addOrder() {
        val adet = binding.etAdet.text.toString().toBigDecimal()
        val fiyat = binding.etFiyat.text.toString().toBigDecimal()
        ordersArrayList.add(Order(adet, fiyat))
        afterItemAdded()
    }

    private fun afterItemAdded() {
        adapterOrders.notifyItemInserted(adapterOrders.itemCount - 1)
        binding.recyclerview.smoothScrollToPosition(adapterOrders.itemCount - 1)
        averageCalculate()
        calculatePercentage(coinPrice)
        newAverageCalculate()
        saveData()
        binding.etAdet.setText("")
        binding.etFiyat.setText("")
    }

    private fun buildRecyclerView() {
        adapterOrders = SavedDatasAdapter(ordersArrayList, this)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = adapterOrders
        binding.recyclerview.smoothScrollToPosition(adapterOrders.itemCount)
    }

    private fun getNewAmountFromDialog(inputAmount: String) {
        if (inputAmount.isNotEmpty() && (inputAmount == ".").not()) {
            if (inputAmount.toBigDecimal() > BigDecimal(0)) {
                newAmount = inputAmount.toBigDecimal()
                newAverageCalculate()
                calculatePercentage(coinPrice)
            } else {
                newAmount = BigDecimal(0)
                newAverageReset()
            }
            saveData()
        }
    }
    private fun newAverageReset() {
        binding.tvYeniAdet.text = "0 (0)"
        binding.tvKarYuzdeKarDahil.text = "% 0"
        binding.tvYeniOrtalama.text = "0"
        binding.tvKarYuzdeKarDahil.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.defaultTextColor
            )
        )
        binding.tvYeniOrtalama.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.defaultTextColor
            )
        )
        binding.tvKarliBakiye.text = "0"
    }


    private fun showDialogAlertCoin(mutableList: MutableList<CoinsResponseItem>) {
        val listBinding: CustomListBinding = CustomListBinding.inflate(layoutInflater)
        dialogCoinsList.setContentView(listBinding.root)

        adapterCoinsList = CoinsAdapter(mutableList, this)
        listBinding.recyclerViewCoinler.setHasFixedSize(true)
        listBinding.recyclerViewCoinler.adapter = adapterCoinsList

        listBinding.svCoins.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapterCoinsList.filter.filter(p0)
                return true
            }
        })
        dialogCoinsList.setOnDismissListener {
            it.dismiss()
            if (coinDetailJob.isCancelled) coinDetailJob = getCoinDetail()
        }
        dialogCoinsList.setOnCancelListener {
            it.cancel()
            if (coinDetailJob.isCancelled) coinDetailJob = getCoinDetail()
        }
        dialogCoinsList.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogCoinsList.show()
    }

    private fun loadData() {
        viewModel.getCalculates().apply {
            ordersArrayList = this
        }
        viewModel.getNewQuantity().apply {

            if (this == Constants.DEFAULT_NEW_QUANTITY_TEXT) {
                binding.tvYeniAdet.text = this
            } else {
                newAmount = this.toBigDecimal()
                //adet 0 olursa yazdırmadıgı icin burada yazdırılıyor
                binding.tvYeniAdet.text =
                    String.format("%s (0)", dfPriceAndAmount.format(newAmount))
            }
        }
        viewModel.getCoinName().apply {
            coinName = this
            binding.toolbar.coinAdi.text = coinName
        }
    }

    private fun saveData() {
        viewModel.setCalculates(ordersArrayList)
        if (newAmount >= BigDecimal(0)) {
            viewModel.setNewQuantity(newAmount.toString())
        }
        if (binding.toolbar.coinAdi.text.toString() != "") {
            viewModel.setCoinName(binding.toolbar.coinAdi.text.toString())
        }
        updateSavedCoinList()
    }

    private fun calculatePercentage(price: BigDecimal) {
        val karliOrtalama: BigDecimal
        val karsizOrtalama: BigDecimal
        var netHamKar = 0.0
        var netKarDahilKar = 0.0
        var karliArtisOrani = 0.0
        var karsizArtisOrani = 0.0

        if (newPriceAverage > BigDecimal(0) && newAmount > BigDecimal(0)) {
            karliOrtalama = newPriceAverage
            karliArtisOrani =
                ((price - karliOrtalama) / karliOrtalama * BigDecimal(100)).toDouble()
            netKarDahilKar = totalMoney.toDouble() * karliArtisOrani / 100
        }

        if (priceAverage > BigDecimal(0)) {
            karsizOrtalama = priceAverage
            karsizArtisOrani =
                ((price - karsizOrtalama) / karsizOrtalama * BigDecimal(100)).toDouble()
            netHamKar = totalMoney.toDouble() * karsizArtisOrani / 100
        }

        binding.tvKarYuzde.text = String.format(
            "%% %s (%s)",
            dfPercentage.format(karsizArtisOrani),
            dfPercentage.format(netHamKar)
        )
        binding.tvKarYuzdeKarDahil.text = String.format(
            "%% %s (%s)",
            dfPercentage.format(karliArtisOrani),
            dfPercentage.format(netKarDahilKar)
        )
        updateTextColor(karsizArtisOrani.toBigDecimal(), BigDecimal(0), binding.tvKarYuzde)
        updateTextColor(karliArtisOrani.toBigDecimal(), BigDecimal(0), binding.tvKarYuzdeKarDahil)
        totalBalanceCalculate(price)
    }

    private fun totalBalanceCalculate(price: BigDecimal) {
        val karsizAdet = amount
        var karliAdet = BigDecimal(0)

        if (newAmount > BigDecimal(0)) {
            karliAdet = newAmount
        }
        binding.tvKarliBakiye.text = dfPercentage.format(karliAdet * price)
        binding.tvKarsizBakiye.text = dfPercentage.format(karsizAdet * price)
    }

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

    override fun onItemClickedCoinlerList(coins: CoinsResponseItem) {
        binding.toolbar.coinAdi.text = coins.symbol
        coins.price?.let {
            updateDecimalFormat(it)
            calculatePercentage(it.toBigDecimal())
        }
        binding.toolbar.coinFiyati.text =
            String.format("%s $", dfPriceAndAmount.format(coins.price?.toDouble()))
        coinName = coins.symbol.toString()

        saveData()

        adapterCoinsList.filter.filter("")

        dialogCoinsList.dismiss()
    }

    override fun onItemClickedDelete(position: Int) {
        adapterOrders.removeItem(position)
        averageCalculate()
        calculatePercentage(coinPrice)
        saveData()
        if (adapterOrders.itemCount == 0) {
            newAverageReset()
        } else {
            newAverageCalculate()
        }
    }

    override fun onResume() {
        super.onResume()
        //start the loop
        coinDetailJob = getCoinDetail()
    }

    override fun onPause() {
        super.onPause()
        //cancel the loop
        coinDetailJob.cancel()
    }

    private fun updateSavedCoinList() {
        var previousList = viewModel.getSavedCoinsList()

        previousList.first {
            it.id == viewModel.preferencesName
        }.apply {
            this.adet = adapterOrders.itemCount
            this.isim = binding.toolbar.coinAdi.text.toString()
        }
        viewModel.updateSavedCoinsList(previousList)
    }
}