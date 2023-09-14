package com.example.hesapmakinesi.ui.addorder

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.hesapmakinesi.databinding.CustomDialogBoxAddOrderBinding
import com.example.hesapmakinesi.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class AddOrderDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bindingDialogBox: CustomDialogBoxAddOrderBinding =
            CustomDialogBoxAddOrderBinding.inflate(layoutInflater)
        return Dialog(requireContext()).apply {
            this.setContentView(bindingDialogBox.root)
            bindingDialogBox.btnAdd.setOnClickListener{
                val listOrder: MutableList<String> = mutableListOf()
                if (inputCheck(bindingDialogBox.etAmount) && inputCheck(bindingDialogBox.etPrice)) {
                    listOrder.add(bindingDialogBox.etAmount.text.toString())
                    listOrder.add(bindingDialogBox.etPrice.text.toString())
                } else {
                    Toast.makeText(context, Constants.WRONG_INPUT_ERROR, Toast.LENGTH_SHORT).show()
                }
                findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.SAVED_STATE_HANDLE_KEY_ORDER,listOrder)
                this.dismiss()
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