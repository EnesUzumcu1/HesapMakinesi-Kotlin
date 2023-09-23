package com.example.hesapmakinesi.ui.buy

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hesapmakinesi.R
import com.example.hesapmakinesi.data.model.Order
import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.databinding.FragmentBuyBinding
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
class BuyFragment : Fragment() {
    private lateinit var binding: FragmentBuyBinding

    private val viewModel by viewModels<BuyViewModel>()

    private lateinit var ordersBuyArrayList: ArrayList<Order>
    private lateinit var ordersSellArrayList: ArrayList<Order>
    private lateinit var dfPriceAndAmount: DecimalFormat

    private lateinit var dfPercentage: DecimalFormat
    private lateinit var otherSymbols: DecimalFormatSymbols
    private lateinit var coinName: String
    private var currency: String = " $"

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

    private var checkVisibility = true

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
        averageCalculate()
        averageSellCalculate()
        decimalFormatUpdate()
        newAverageCalculate()
        updateBuyOrderListSize()
        updateSellOrderListSize()
        if (this::coinDetailJob.isInitialized.not()) {
            coinDetailJob = getCoinDetail()
        }
        binding.btnEkle.setOnClickListener {
            findNavController().navigate(R.id.addOrderDialogFragment)
        }

        binding.cvShowOrder.setOnClickListener {
            findNavController().navigate(R.id.orderListBottomSheet, Bundle().apply {
                val value: String = viewModel.preferencesName
                putString(Constants.SEND_PREF_NAME, value)
                putString(Constants.DIRECTION, Constants.DIRECTION_BUY)
            })
        }

        binding.cvShowOrderSell.setOnClickListener {
            findNavController().navigate(R.id.orderListBottomSheet, Bundle().apply {
                val value: String = viewModel.preferencesName
                putString(Constants.SEND_PREF_NAME, value)
                putString(Constants.DIRECTION, Constants.DIRECTION_SELL)
            })
        }

        binding.tvYeniAdet.setOnClickListener {
            findNavController().navigate(R.id.newAmountDialogFragment)
        }

        binding.toolbar.coinAdi.setOnClickListener {
            findNavController().navigate(R.id.coinListDialogFragment)
        }

        binding.tvSeparator.setOnClickListener {
            if (checkVisibility) {
                binding.tvSeparator.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_round_arrow_up_24,0)
                binding.cvTotalSellAmount.visibility = View.GONE
                binding.cvSellAverage.visibility = View.GONE
                binding.cvAllSellOrdersAreSold.visibility = View.GONE
                binding.cvAllSellOrdersProfitRate.visibility = View.GONE
                checkVisibility = false
            } else {
                binding.tvSeparator.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_round_arrow_down_24,0)
                binding.cvTotalSellAmount.visibility = View.VISIBLE
                binding.cvSellAverage.visibility = View.VISIBLE
                binding.cvAllSellOrdersAreSold.visibility = View.VISIBLE
                binding.cvAllSellOrdersProfitRate.visibility = View.VISIBLE
                checkVisibility = true
            }
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
                                            "%s%s",
                                            dfPriceAndAmount.format(safePrice),
                                            currency
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
        }

        val currentFragment = findNavController().getBackStackEntry(R.id.calculateFragment)
        val dialogObserver = LifecycleEventObserver { _, event ->

            when (event) {
                ON_RESUME -> {
                    if (currentFragment.savedStateHandle.contains(
                            Constants.SAVED_STATE_HANDLE_KEY_NEW_AMOUNT
                        )
                    ) {
                        getNewAmountFromDialog(currentFragment.savedStateHandle[Constants.SAVED_STATE_HANDLE_KEY_NEW_AMOUNT]!!)
                        currentFragment.savedStateHandle.remove<String>(Constants.SAVED_STATE_HANDLE_KEY_NEW_AMOUNT)
                    } else if (currentFragment.savedStateHandle.contains(
                            Constants.SAVED_STATE_HANDLE_KEY_ORDER
                        )
                    ) {
                        val mutableList: MutableList<String>? =
                            currentFragment.savedStateHandle[Constants.SAVED_STATE_HANDLE_KEY_ORDER]
                        currentFragment.savedStateHandle.remove<String>(Constants.SAVED_STATE_HANDLE_KEY_ORDER)
                        if (mutableList.isNullOrEmpty().not() && mutableList?.size == 3) {
                            if (mutableList[2] == Constants.DIRECTION_BUY) {
                                addBuyOrder(mutableList[0], mutableList[1])
                                updateBuyOrderListSize()
                            } else if (mutableList[2] == Constants.DIRECTION_SELL) {
                                addSellOrder(mutableList[0], mutableList[1])
                                updateSellOrderListSize()
                            }
                        }
                    } else if (currentFragment.savedStateHandle.contains(
                            Constants.SAVED_STATE_HANDLE_KEY_COIN
                        )
                    ) {
                        getCoinNameFromDialog(currentFragment.savedStateHandle[Constants.SAVED_STATE_HANDLE_KEY_COIN]!!)
                        currentFragment.savedStateHandle.remove<String>(Constants.SAVED_STATE_HANDLE_KEY_COIN)
                    } else if (currentFragment.savedStateHandle.contains(
                            Constants.SAVED_STATE_HANDLE_KEY_CLOSED_BOTTOM_SHEET
                        )
                    ) {
                        checkAfterCloseOrderListBottomSheet()
                        currentFragment.savedStateHandle.remove<String>(Constants.SAVED_STATE_HANDLE_KEY_CLOSED_BOTTOM_SHEET)
                    }
                    if (coinDetailJob.isCancelled) coinDetailJob = getCoinDetail()

                }
                ON_PAUSE -> {
                    coinDetailJob.cancel()
                }
                else -> {}
            }
        }

        val dialogLifecycle = currentFragment.lifecycle
        dialogLifecycle.addObserver(dialogObserver)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == ON_DESTROY) {
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

    private fun checkAfterCloseOrderListBottomSheet() {
        viewModel.getBuyCalculates().apply {
            ordersBuyArrayList = this
        }
        viewModel.getSellCalculates().apply {
            ordersSellArrayList = this
        }
        averageCalculate()
        calculatePercentage(coinPrice)
        averageSellCalculate()
        if (viewModel.getBuyCalculates().size == 0) {
            newAverageReset()
        } else {
            newAverageCalculate()
        }
        updateSavedCoinList(updateAmount = true)
        updateBuyOrderListSize()
        updateSellOrderListSize()
    }

    private fun atamalar() {
        arguments?.run {
            val coinAdi = this.getString(Constants.SEND_PREF_NAME)
            coinAdi?.let {
                viewModel.preferencesName = it
            }
        }
        ordersBuyArrayList = ArrayList()
        ordersSellArrayList = ArrayList()

        //Double ifadeler için ondalık ayırıcının nokta olması gerekiyor. Bunu sabit olarak uygulamak için kural eklendi
        val currentLocale = Locale.getDefault()
        otherSymbols = DecimalFormatSymbols(currentLocale)
        otherSymbols.decimalSeparator = '.'
        otherSymbols.groupingSeparator = ','

        //end
        dfPriceAndAmount = DecimalFormat("#.########", otherSymbols)
        dfAverage = DecimalFormat("#.##", otherSymbols)
        dfPercentage = DecimalFormat("#.##", otherSymbols)
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
        for (order in ordersBuyArrayList) {
            amount += order.adet
            totalMoney += order.adet * order.fiyat
            priceAverage = totalMoney.divide(amount, 8, RoundingMode.HALF_EVEN)
        }
        binding.tvAdet.text = dfPriceAndAmount.format(amount)
        binding.tvOrtalama.text = dfAverage.format(priceAverage) + currency
        binding.tvToplamPara.text = dfPercentage.format(totalMoney) + currency
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
            binding.tvYeniOrtalama.text = dfAverage.format(newPriceAverage) + currency

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

    private fun addBuyOrder(amount: String?, price: String?) {
        amount?.let { safeAmount ->
            price?.let { safePrice ->
                ordersBuyArrayList.add(Order(safeAmount.toBigDecimal(), safePrice.toBigDecimal()))
                afterBuyItemAdded()
            }
        }
    }

    private fun addSellOrder(amount: String?, price: String?) {
        amount?.let { safeAmount ->
            price?.let { safePrice ->
                ordersSellArrayList.add(Order(safeAmount.toBigDecimal(), safePrice.toBigDecimal()))
                afterSellItemAdded()
            }
        }
    }

    private fun afterBuyItemAdded() {
        averageCalculate()
        averageSellCalculate()
        calculatePercentage(coinPrice)
        newAverageCalculate()
        saveBuyCalculates()
        saveCoinName()
        updateSavedCoinList(updateAmount = true)
    }

    private fun afterSellItemAdded() {
        averageSellCalculate()
        saveSellCalculates()
    }

    private fun averageSellCalculate() {
        var adet = BigDecimal(0)
        var fiyatOrt = BigDecimal(0)
        var gerceklesirsePara = BigDecimal(0)
        for (order in ordersSellArrayList) {
            adet += order.adet
            gerceklesirsePara += order.adet * order.fiyat
            fiyatOrt = gerceklesirsePara / adet
        }
        binding.tvTotalSellAmount.text = dfPriceAndAmount.format(adet)
        binding.tvSellAverage.text = dfAverage.format(fiyatOrt) + currency
        binding.tvAllSellOrdersAreSold.text = dfPercentage.format(gerceklesirsePara) + currency
        val hamPara = totalMoney
        if (gerceklesirsePara > BigDecimal(0)) {
            val artisOrani =
                ((gerceklesirsePara - hamPara) / gerceklesirsePara * BigDecimal(100))
            val netKar = (gerceklesirsePara - hamPara)
            binding.tvAllSellOrdersProfitRate.text = String.format(
                "%% %s (%s)",
                dfPercentage.format(artisOrani),
                dfPercentage.format(netKar)
            )
            if (artisOrani > BigDecimal(0)) {
                binding.tvAllSellOrdersProfitRate.setTextColor(Color.GREEN)
            } else {
                binding.tvAllSellOrdersProfitRate.setTextColor(Color.RED)
            }
        } else {
            binding.tvAllSellOrdersProfitRate.text = "0"
        }
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
            saveNewAmount()
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

    private fun loadData() {
        viewModel.getBuyCalculates().apply {
            ordersBuyArrayList = this
        }
        viewModel.getSellCalculates().apply {
            ordersSellArrayList = this
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
        findCurrency()
    }

    private fun saveBuyCalculates() {
        viewModel.setBuyCalculates(ordersBuyArrayList)
    }

    private fun saveNewAmount() {
        if (newAmount >= BigDecimal(0)) {
            viewModel.setNewQuantity(newAmount.toString())
        }
    }

    private fun saveCoinName() {
        if (binding.toolbar.coinAdi.text.toString() != "") {
            viewModel.setCoinName(binding.toolbar.coinAdi.text.toString())
        }
        updateSavedCoinList(updateCoinName = true)
    }

    private fun saveSellCalculates() {
        viewModel.setSellCalculates(ordersSellArrayList)
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
        binding.tvKarliBakiye.text = dfPercentage.format(karliAdet * price) + currency
        binding.tvKarsizBakiye.text = dfPercentage.format(karsizAdet * price) + currency
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

    private fun getCoinNameFromDialog(coins: CoinsResponseItem) {
        binding.toolbar.coinAdi.text = coins.symbol
        coinName = coins.symbol.toString()
        findCurrency()
        coins.price?.let {
            updateDecimalFormat(it)
            calculatePercentage(it.toBigDecimal())
        }
        binding.toolbar.coinFiyati.text =
            String.format("%s%s", dfPriceAndAmount.format(coins.price?.toDouble()), currency)
        saveCoinName()
    }

    override fun onResume() {
        super.onResume()
        //start the loop
        if (coinDetailJob.isActive.not()) coinDetailJob = getCoinDetail()
    }

    override fun onPause() {
        super.onPause()
        //cancel the loop
        coinDetailJob.cancel()
    }

    private fun updateSavedCoinList(
        updateCoinName: Boolean = false,
        updateAmount: Boolean = false
    ) {
        val previousList = viewModel.getSavedCoinsList()

        previousList.first {
            it.id == viewModel.preferencesName
        }.apply {
            if (updateAmount) this.adet = viewModel.getBuyCalculates().size
            if (updateCoinName) this.isim = binding.toolbar.coinAdi.text.toString()
        }
        viewModel.updateSavedCoinsList(previousList)
    }

    private fun updateBuyOrderListSize() {
        viewModel.getBuyCalculates().size.apply {
            binding.tvOrderListSize.text = "$this adet alış emri var. \nDetay için tıkla."
        }
    }

    private fun updateSellOrderListSize(){
        viewModel.getSellCalculates().size.apply {
            binding.tvSellOrderListSize.text = "$this adet satış emri var. \nDetay için tıkla."
        }
    }

    private fun findCurrency() {
        currency = if (coinName.takeLast(4) == "USDT") " $"
        else {
            " " + coinName.substring(4)
        }
    }
}