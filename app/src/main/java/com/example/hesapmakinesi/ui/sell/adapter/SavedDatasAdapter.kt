package com.example.hesapmakinesi.ui.sell.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hesapmakinesi.data.model.Order
import com.example.hesapmakinesi.databinding.ListItemBinding
import java.text.DecimalFormat

class SavedDatasAdapter(
    private var hesapArrayList: java.util.ArrayList<Order>,
    private var listener: OnClickListener
) : RecyclerView.Adapter<SavedDatasAdapter.SavedDatasViewHolder>() {

    class SavedDatasViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order, listener: OnClickListener, position: Int) {

            val df = DecimalFormat("0")
            df.maximumFractionDigits = 340

            binding.tvPosition.text = String.format("%s)",position + 1)

            binding.tvAdet.text = df.format(order.adet)
            binding.tvFiyat.text = df.format(order.fiyat)

            binding.btnSil.setOnClickListener {
                listener.onItemClickedDelete(position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedDatasViewHolder {
        return SavedDatasViewHolder(
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SavedDatasViewHolder, position: Int) {
        holder.bind(hesapArrayList[position], listener, position)
    }

    override fun getItemCount(): Int {
        return hesapArrayList.size
    }

    interface OnClickListener {
        //eleman silmek icin position bilgisi gonderiliyor
        fun onItemClickedDelete(position: Int)
    }

    fun removeItem(position: Int) {
        hesapArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, hesapArrayList.size)
    }

}