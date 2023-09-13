package com.example.hesapmakinesi.ui.newamount

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.hesapmakinesi.databinding.CustomDialogBoxBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewAmountDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bindingDialogBox: CustomDialogBoxBinding =
            CustomDialogBoxBinding.inflate(layoutInflater)
        return Dialog(requireContext()).apply {
            this.setContentView(bindingDialogBox.root)
            bindingDialogBox.btnSave.setOnClickListener{
                bindingDialogBox.tvNewAmount.text.toString().apply {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("newAmount",this)
                }
                this.dismiss()
            }
            this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}
