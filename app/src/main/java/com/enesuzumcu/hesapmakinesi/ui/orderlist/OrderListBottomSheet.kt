package com.enesuzumcu.hesapmakinesi.ui.orderlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.hesapmakinesi.data.model.Order
import com.enesuzumcu.hesapmakinesi.databinding.OrderListBottomSheetBinding
import com.enesuzumcu.hesapmakinesi.ui.buy.BuyViewModel
import com.enesuzumcu.hesapmakinesi.ui.orderlist.adapter.SavedDatasAdapter
import com.enesuzumcu.hesapmakinesi.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderListBottomSheet : BottomSheetDialogFragment(), SavedDatasAdapter.OnClickListener {

    private lateinit var binding: OrderListBottomSheetBinding
    private var preferencesName: String = ""
    private val viewModel by viewModels<BuyViewModel>()
    private lateinit var adapterOrders: SavedDatasAdapter
    private lateinit var ordersArrayList: ArrayList<Order>
    private var priceName: String = ""
    private var amountName: String = ""
    private var direction: String = "BUY"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OrderListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.run {
            val prefName = this.getString(Constants.SEND_PREF_NAME)
            prefName?.let {
                preferencesName = it
            }
            val getDirection = this.getString(Constants.DIRECTION)
            getDirection?.let {
                direction = it
            }
        }
        ordersArrayList = ArrayList()
        lifecycleScope.launchWhenResumed {
            viewModel.preferencesName = preferencesName
            if (direction == Constants.DIRECTION_BUY) setPriceAndAmountName(viewModel.getBuyCalculates())
            else if (direction == Constants.DIRECTION_SELL) setPriceAndAmountName(viewModel.getSellCalculates())
        }
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            Constants.SAVED_STATE_HANDLE_KEY_CLOSED_BOTTOM_SHEET,
            ""
        )
    }

    private fun setPriceAndAmountName(arrayList: ArrayList<Order>) {
        arrayList.apply {
            ordersArrayList = this
            viewModel.getCoinName().apply {
                if (this.takeLast(4) == "USDT") {
                    priceName = "USDT"
                    amountName = this.dropLast(4)
                } else {
                    priceName = this.substring(4)
                    amountName = this.take(4)
                }
            }
            buildRecyclerView()
        }
    }

    private fun buildRecyclerView() {
        adapterOrders = SavedDatasAdapter(ordersArrayList, this, priceName, amountName)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = adapterOrders
    }

    override fun onItemClickedDelete(position: Int) {
        adapterOrders.removeItem(position)
        if (direction == Constants.DIRECTION_BUY) viewModel.setBuyCalculates(ordersArrayList)
        else if (direction == Constants.DIRECTION_SELL) viewModel.setSellCalculates(ordersArrayList)
    }
}