package com.example.hesapmakinesi.ui.calculateViewPager2.buyandsell.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.hesapmakinesi.data.model.Coins
import com.example.hesapmakinesi.databinding.ListItemCoinBinding
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class CoinsAdapter(
    private var coinlerArrayList: ArrayList<Coins>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<CoinsAdapter.CoinsAdapterViewHolder>(), Filterable {
    var coinlerFilterArrayList: ArrayList<Coins> = ArrayList()

    init {
        coinlerFilterArrayList = coinlerArrayList
    }

    class CoinsAdapterViewHolder(private val binding: ListItemCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(coins: Coins, listener: OnClickListener, position: Int) {

            val df = DecimalFormat("0")
            df.maximumFractionDigits = 340

            binding.tvPosition.text = (position + 1).toString() + ")"

            binding.tvCoinAdi.text = coins.isim
            binding.tvCoinFiyati.text = String.format("%s $", df.format(coins.fiyat))
            itemView.setOnClickListener {
                listener.onItemClickedCoinlerList(
                    coins
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsAdapterViewHolder {
        return CoinsAdapterViewHolder(
            ListItemCoinBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CoinsAdapterViewHolder, position: Int) {
        holder.bind(coinlerFilterArrayList[position], listener, position)
    }

    override fun getItemCount(): Int {
        return coinlerFilterArrayList.size
    }

    override fun getFilter(): Filter {
        return filtre
    }

    private var filtre: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filtereliListe: ArrayList<Coins> = ArrayList()
            if (charSequence == "" || charSequence.isEmpty()) {
                filtereliListe.addAll(coinlerArrayList)
            } else {
                val filterPattern =
                    charSequence.toString().uppercase().trim()
                for (coins in coinlerArrayList) {
                    if (coins.isim.uppercase().contains(filterPattern)) {
                        filtereliListe.add(coins)
                    }
                }
                coinlerFilterArrayList = filtereliListe
            }
            val results = FilterResults()
            results.values = coinlerFilterArrayList
            return results
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            //coinlerArrayList.clear()
            coinlerFilterArrayList = (filterResults.values as ArrayList<Coins>)
            notifyDataSetChanged()
        }
    }

    interface OnClickListener {
        //secilen coin bilgilerii getirmek icin onClick atandi
        fun onItemClickedCoinlerList(coins: Coins)
    }

}