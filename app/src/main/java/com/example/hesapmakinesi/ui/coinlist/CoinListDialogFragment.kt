package com.example.hesapmakinesi.ui.coinlist

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.databinding.CustomDialogBoxCoinListBinding
import com.example.hesapmakinesi.ui.buy.adapter.CoinsAdapter
import com.example.hesapmakinesi.utils.Constants
import com.example.hesapmakinesi.utils.LoadingProgressBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoinListDialogFragment : DialogFragment(), CoinsAdapter.OnClickListener {
    private lateinit var binding: CustomDialogBoxCoinListBinding
    private val viewModel by viewModels<CoinListViewModel>()
    private lateinit var dialogCoinsList: Dialog
    private lateinit var adapterCoinsList: CoinsAdapter
    private lateinit var loadingProgressBar: LoadingProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomDialogBoxCoinListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingProgressBar = LoadingProgressBar(requireContext())
        dialogCoinsList = Dialog(requireContext())
        viewModel.getCoinList()
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.uiEvent.collect {
                    when (it) {
                        is CoinListViewEvent.ShowData -> {
                            showDialogAlertCoin(it.data)
                        }
                        is CoinListViewEvent.ShowError -> {
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
                        is CoinListUiState.Loading -> {
                            loadingProgressBar.show()
                        }
                        is CoinListUiState.Empty -> {
                            loadingProgressBar.cancel()
                        }
                    }
                }
            }
        }
    }

    private fun showDialogAlertCoin(mutableList: MutableList<CoinsResponseItem>) {
        adapterCoinsList = CoinsAdapter(mutableList, this)
        binding.rvCoinList.setHasFixedSize(true)
        binding.rvCoinList.adapter = adapterCoinsList

        binding.svCoins.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapterCoinsList.filter.filter(p0)
                return true
            }
        })
    }

    override fun onItemClickedCoinlerList(coins: CoinsResponseItem) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.SAVED_STATE_HANDLE_KEY_COIN,coins)
        adapterCoinsList.filter.filter("")
        dialog?.dismiss()
    }

}