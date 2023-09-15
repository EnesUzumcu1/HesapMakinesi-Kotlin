package com.example.hesapmakinesi.ui.orderlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.hesapmakinesi.data.model.Order
import com.example.hesapmakinesi.databinding.OrderListBottomSheetBinding
import com.example.hesapmakinesi.ui.buy.BuyViewModel
import com.example.hesapmakinesi.ui.sell.adapter.SavedDatasAdapter
import com.example.hesapmakinesi.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderListBottomSheet : BottomSheetDialogFragment(), SavedDatasAdapter.OnClickListener {

    private lateinit var binding: OrderListBottomSheetBinding
    private var preferencesName : String = ""
    private val viewModel by viewModels<BuyViewModel>()
    private lateinit var adapterOrders: SavedDatasAdapter
    private lateinit var ordersArrayList: ArrayList<Order>

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
        }
        ordersArrayList = ArrayList()
        lifecycleScope.launchWhenResumed {
            viewModel.preferencesName = preferencesName
            viewModel.getCalculates().apply {
                ordersArrayList = this
                buildRecyclerView()
            }
        }
    }

    private fun buildRecyclerView() {
        adapterOrders = SavedDatasAdapter(ordersArrayList, this)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = adapterOrders
    }

    override fun onItemClickedDelete(position: Int) {
        adapterOrders.removeItem(position)
        viewModel.setCalculates(ordersArrayList)
    }
}