package com.example.hesapmakinesi.utils

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface
import com.example.hesapmakinesi.data.model.SavedCoins


class AlertDialogBuilder(context: Context,listener: DialogInterface.OnClickListener,savedCoins: SavedCoins) {
    init {
        createAlertDialog(context, listener,savedCoins)?.show()
    }
    private fun createAlertDialog(context: Context, listener: DialogInterface.OnClickListener,savedCoins: SavedCoins): Dialog? {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
            .setTitle(Constants.ALERT_DIALOG_DELETE_TITLE)
            .setMessage("${savedCoins.isim} ${savedCoins.adet} adet alım ${Constants.ALERT_DIALOG_DELETE_MESSAGE}")
            .setPositiveButton("İPTAL",listener)
            .setNegativeButton("SİL",listener)
        return alertDialog.create()
    }
}