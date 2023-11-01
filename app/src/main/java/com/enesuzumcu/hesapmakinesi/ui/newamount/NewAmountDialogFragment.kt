package com.enesuzumcu.hesapmakinesi.ui.newamount

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.enesuzumcu.hesapmakinesi.databinding.CustomDialogBoxBinding
import com.enesuzumcu.hesapmakinesi.utils.Constants
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
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.SAVED_STATE_HANDLE_KEY_NEW_AMOUNT,this)
                }
                this.dismiss()
            }
            this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}
