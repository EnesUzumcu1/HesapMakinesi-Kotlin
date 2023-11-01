package com.example.hesapmakinesi.ui.savedcoins

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.hesapmakinesi.R
import com.example.hesapmakinesi.data.model.ListSizeControl
import com.example.hesapmakinesi.data.model.SavedCoins
import com.example.hesapmakinesi.databinding.FragmentSavedCoinsBinding
import com.example.hesapmakinesi.ui.savedcoins.adapter.SavedCoinsDiffutilAdapter
import com.example.hesapmakinesi.utils.AlertDialogBuilder
import com.example.hesapmakinesi.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedCoinsFragment : Fragment(), SavedCoinsDiffutilAdapter.OnClickListenerSavedCoins {
    private lateinit var binding: FragmentSavedCoinsBinding
    private lateinit var navController: NavController

    private val viewModel by viewModels<SavedCoinsViewModel>()

    private var kayitliCoinlers: ArrayList<SavedCoins> = java.util.ArrayList<SavedCoins>()
    private var stringsID: ArrayList<ListSizeControl> = java.util.ArrayList()

    private lateinit var adapter: SavedCoinsDiffutilAdapter

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

        binding.btnAddNewCoin.setOnClickListener {
            veriKontrol()
            adapter.notifyItemInserted(adapter.itemCount)
            binding.rvSavedCoinsList.smoothScrollToPosition(adapter.itemCount)
            saveData()
            changeVisibility()
        }
        updateSavedCoinListNewQuantity()
    }

    private fun coinEkle(prefName: String) {
        kayitliCoinlers.add(
            SavedCoins(
                isim = Constants.DEFAULT_UNSELECTED_COIN_NAME_TEXT,
                id = prefName,
                adet = 0
            )
        )
    }

    private fun veriKontrol() {
        var id = ""
        for (data in stringsID) {
            if (data.durum) {
                id = data.id
                data.durum = false
                break
            }
        }

        if (id != "") {
            coinEkle(
                prefName = id
            )
        } else {
            Toast.makeText(requireContext(), "Liste Dolu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atamalar() {
        navController = findNavController()
        loadData()
        if (stringsID.size == 0) {
            //durum true ise kullanılmamış bir id, eğer false ise id kullanılıyor
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_1, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_2, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_3, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_4, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_5, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_6, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_7, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_8, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_9, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_10, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_11, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_12, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_13, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_14, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_15, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_16, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_17, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_18, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_19, true))
            stringsID.add(ListSizeControl(Constants.COIN_DETAIL_20, true))
        }
        changeVisibility()
    }

    private fun changeVisibility() {
        if (kayitliCoinlers.size == 0) {
            binding.rvSavedCoinsList.visibility = View.GONE
            binding.tvEmptyList.visibility = View.VISIBLE
        } else {
            binding.rvSavedCoinsList.visibility = View.VISIBLE
            binding.tvEmptyList.visibility = View.GONE
        }
        binding.toolbar.notSayisi.text = String.format("%s/20", kayitliCoinlers.size.toString())
    }

    private fun buildrecyclerview() {
        adapter = SavedCoinsDiffutilAdapter(this)
        binding.rvSavedCoinsList.setHasFixedSize(true)
        binding.rvSavedCoinsList.adapter = adapter.apply {
            submitList(kayitliCoinlers)
        }
        binding.rvSavedCoinsList.smoothScrollToPosition(adapter.itemCount)
    }

    private fun deleteSavedCoin(savedCoins: SavedCoins, position: Int) {
        stringsID.first {
            it.id == savedCoins.id
        }.durum = true

        kayitliCoinlers.removeAt(position)
        //adapter.notifyDataSetChanged()
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position,kayitliCoinlers.size)
        //Verileri hafızan kalıcı olarak siliyor
        viewModel.deleteCoinDetail(savedCoins.id)
        saveData()
        changeVisibility()
    }

    override fun onItemClickedSavedCoinsDelete(savedCoins: SavedCoins, position: Int) {
        val listener = DialogInterface.OnClickListener { _: DialogInterface?, p1: Int ->
            when (p1) {
                AlertDialog.BUTTON_NEGATIVE -> {
                    deleteSavedCoin(savedCoins, position)
                }
                AlertDialog.BUTTON_POSITIVE -> {
                }
            }
        }
        AlertDialogBuilder(requireContext(), listener,savedCoins)
    }

    override fun onItemClickedSavedCoinsDetail(savedCoins: SavedCoins) {
        navController.navigate(
            R.id.action_savedCoinsFragment_to_buyFragment,
            Bundle().apply {
                val value: String = savedCoins.id
                putString(Constants.SEND_PREF_NAME, value)
            }
        )
    }

    private fun loadData() {
        viewModel.getSavedCoinsList().apply {
            kayitliCoinlers = this
        }
        viewModel.getIDs().apply {
            stringsID = this
        }
    }

    private fun saveData() {
        viewModel.updateSavedCoinsList(kayitliCoinlers)
        viewModel.updateIDs(stringsID)
    }

    //guncellemeden sonra silinecek
    private fun updateSavedCoinListNewQuantity() {
        for (i in stringsID) {
            if (!i.durum) {
                val oldquantity = viewModel.getNewQuantity(i.id)
                if (oldquantity.contains(" ")) {
                    val splited = oldquantity.split(" ").toTypedArray()
                    splited.first().apply {
                        if (this != "Yeni") {
                            viewModel.setNewQuantity(i.id, this)
                            Toast.makeText(
                                requireContext(),
                                "${i.id} yeni adet guncellendi $this",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }
        }
    }
}
