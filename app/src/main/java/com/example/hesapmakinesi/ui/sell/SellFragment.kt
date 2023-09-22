package com.example.hesapmakinesi.ui.sell

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hesapmakinesi.data.model.Order
import com.example.hesapmakinesi.databinding.FragmentSellBinding
import com.example.hesapmakinesi.ui.buy.BuyFragment
import com.example.hesapmakinesi.ui.sell.adapter.SavedDatasAdapter
import com.example.hesapmakinesi.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@AndroidEntryPoint
class SellFragment : Fragment(), SavedDatasAdapter.OnClickListener {
    private lateinit var binding: FragmentSellBinding
    private val viewModel by viewModels<SellViewModel>()
    private lateinit var ordersArrayList: ArrayList<Order>
    private lateinit var dfAmount: DecimalFormat
    private lateinit var dfPercentage: DecimalFormat
    private lateinit var adapterOrders: SavedDatasAdapter
    private lateinit var preferencesName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        atamalar()
        loadData(preferencesName)
        buildRecyclerView()
        averageCalculate()

        binding.btnEkle.setOnClickListener {
            if (inputCheck(binding.etAdet) && inputCheck(binding.etFiyat)) {
                addOrder()
                binding.etAdet.setText("")
                binding.etFiyat.setText("")
            } else {
                Toast.makeText(context, Constants.WRONG_INPUT_ERROR, Toast.LENGTH_SHORT).show()
            }
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

    private fun averageCalculate() {
        var adet = BigDecimal(0)
        var fiyatOrt = BigDecimal(0)
        var gerceklesirsePara = BigDecimal(0)
        for (order in ordersArrayList) {
            adet += order.adet
            gerceklesirsePara += order.adet * order.fiyat
            fiyatOrt = gerceklesirsePara / adet
        }
        binding.tvAdet.text = dfAmount.format(adet)
        binding.tvOrtalama.text = BuyFragment.dfAverage.format(fiyatOrt)
        binding.tvGerceklesirsePara.text = dfPercentage.format(gerceklesirsePara)
        val hamPara = BuyFragment.totalMoney
        binding.tvBaslangictakiPara.text = dfPercentage.format(hamPara)
        if (gerceklesirsePara > BigDecimal(0)) {
            val artisOrani =
                ((gerceklesirsePara - hamPara) / gerceklesirsePara * BigDecimal(100))
            val netKar = (gerceklesirsePara - hamPara)
            binding.tvKarOrani.text = String.format(
                "%% %s (%s)",
                dfPercentage.format(artisOrani),
                dfPercentage.format(netKar)
            )
            if (artisOrani > BigDecimal(0)) {
                binding.tvKarOrani.setTextColor(Color.GREEN)
            } else {
                binding.tvKarOrani.setTextColor(Color.RED)
            }
        } else {
            binding.tvKarOrani.text = "0"
        }
    }

    private fun atamalar() {
        arguments?.run {
            val coinAdi = this.getString(Constants.SEND_PREF_NAME)
            coinAdi?.let {
                preferencesName = it
            }
        }

        ordersArrayList = ArrayList<Order>()

        //Double ifadeler için ondalık ayırıcının nokta olması gerekiyor. Bunu sabit olarak uygulamak için kural eklendi
        val currentLocale = Locale.getDefault()
        val otherSymbols = DecimalFormatSymbols(currentLocale)
        otherSymbols.decimalSeparator = '.'
        otherSymbols.groupingSeparator = ','

        //end
        dfAmount = DecimalFormat("#.########", otherSymbols)
        dfPercentage = DecimalFormat("#.##", otherSymbols)
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
        saveData(preferencesName)
    }

    private fun buildRecyclerView() {
        adapterOrders = SavedDatasAdapter(ordersArrayList, this,"","")
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = adapterOrders
        binding.recyclerview.smoothScrollToPosition(adapterOrders.itemCount)
    }

    private fun loadData(preferencesName: String) {
        viewModel.getCalculates(preferencesName).apply {
            ordersArrayList = this
        }
    }

    private fun saveData(preferencesName: String) {
        viewModel.setCalculates(preferencesName, ordersArrayList)
    }

    override fun onItemClickedDelete(position: Int) {
        adapterOrders.removeItem(position)
        averageCalculate()
        saveData(preferencesName)
    }

    override fun onResume() {
        super.onResume()
        if (dfAmount.format(BuyFragment.totalMoney).equals(binding.tvBaslangictakiPara.text).not()
        ) averageCalculate()
    }
}