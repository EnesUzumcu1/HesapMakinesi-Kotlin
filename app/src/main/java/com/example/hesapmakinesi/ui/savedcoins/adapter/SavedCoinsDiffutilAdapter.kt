package com.example.hesapmakinesi.ui.savedcoins.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hesapmakinesi.data.model.SavedCoins
import com.example.hesapmakinesi.databinding.ListItemKayitliCoinlerBinding

class SavedCoinsDiffutilAdapter(private val listener: OnClickListenerSavedCoins) :
    ListAdapter<SavedCoins, SavedCoinsDiffutilAdapter.SavedCoinsViewHolder>(SavedCoinsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCoinsViewHolder {
        return SavedCoinsViewHolder(
            ListItemKayitliCoinlerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SavedCoinsViewHolder, position: Int) {
        holder.bind(getItem(position), listener, position)
    }

    class SavedCoinsDiffUtil : DiffUtil.ItemCallback<SavedCoins>() {
        override fun areItemsTheSame(oldItem: SavedCoins, newItem: SavedCoins): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavedCoins, newItem: SavedCoins): Boolean {
            return oldItem == newItem
        }
    }

    class SavedCoinsViewHolder(private val binding: ListItemKayitliCoinlerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            savedCoin: SavedCoins,
            listener: OnClickListenerSavedCoins,
            position: Int
        ) {
            binding.tvKayitliAdet.text = String.format("%s adet alım",savedCoin.adet)
            binding.tvKayitliCoinAdi.text = savedCoin.isim

            binding.tvPosition.text = String.format("%s)",position + 1)

            binding.btnSil.setOnClickListener {
                listener.onItemClickedSavedCoinsDelete(savedCoin, position)
            }

            itemView.setOnClickListener {
                listener.onItemClickedSavedCoinsDetail(savedCoin)
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    interface OnClickListenerSavedCoins {
        fun onItemClickedSavedCoinsDetail(savedCoins: SavedCoins)
        fun onItemClickedSavedCoinsDelete(savedCoins: SavedCoins, position: Int)
    }
}