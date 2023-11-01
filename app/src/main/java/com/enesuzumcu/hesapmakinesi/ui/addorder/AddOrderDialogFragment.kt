package com.enesuzumcu.hesapmakinesi.ui.addorder

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.hesapmakinesi.databinding.CustomDialogBoxAddOrderBinding
import com.enesuzumcu.hesapmakinesi.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class AddOrderDialogFragment: DialogFragment() {
    private val listOrder: MutableList<String> = MutableList(3){Constants.DIRECTION_BUY}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bindingDialogBox: CustomDialogBoxAddOrderBinding =
            CustomDialogBoxAddOrderBinding.inflate(layoutInflater)
        return Dialog(requireContext()).apply {
            this.setContentView(bindingDialogBox.root)
            bindingDialogBox.btnAdd.setOnClickListener{
                if (inputCheck(bindingDialogBox.etAmount) && inputCheck(bindingDialogBox.etPrice)) {
                    listOrder[0] = bindingDialogBox.etAmount.text.toString()
                    listOrder[1] = bindingDialogBox.etPrice.text.toString()
                } else {
                    Toast.makeText(context, Constants.WRONG_INPUT_ERROR, Toast.LENGTH_SHORT).show()
                }
                findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.SAVED_STATE_HANDLE_KEY_ORDER,listOrder)
                this.dismiss()
            }
            bindingDialogBox.radioBtnBuy.setOnClickListener {
                bindingDialogBox.tvTitle.text = "Yeni Alış Yeri Ekle"
                listOrder[2] = Constants.DIRECTION_BUY
            }
            bindingDialogBox.radioBtnSell.setOnClickListener {
                bindingDialogBox.tvTitle.text = "Yeni Satış Yeri Ekle"
                listOrder[2] = Constants.DIRECTION_SELL
            }
            this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
}