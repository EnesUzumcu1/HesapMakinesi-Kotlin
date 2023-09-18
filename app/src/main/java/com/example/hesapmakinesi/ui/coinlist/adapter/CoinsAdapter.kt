package com.example.hesapmakinesi.ui.coinlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.databinding.ListItemCoinBinding
import java.text.DecimalFormat

class CoinsAdapter(
    private var coinlerArrayList: MutableList<CoinsResponseItem>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<CoinsAdapter.CoinsAdapterViewHolder>(), Filterable {
    var coinlerFilterArrayList: MutableList<CoinsResponseItem> = mutableListOf()

    init {
        coinlerFilterArrayList = coinlerArrayList
    }

    class CoinsAdapterViewHolder(private val binding: ListItemCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(coins: CoinsResponseItem, listener: OnClickListener, position: Int) {

            val df = DecimalFormat("0")
            df.maximumFractionDigits = 340

            binding.tvPosition.text = String.format("%s)",position + 1)

            binding.tvCoinAdi.text = coins.symbol
            binding.tvCoinFiyati.text = String.format("%s $", df.format(coins.price?.toDouble()))
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
            val filtereliListe: MutableList<CoinsResponseItem> = mutableListOf()
            if (charSequence == "" || charSequence.isEmpty()) {
                filtereliListe.addAll(coinlerArrayList)
            } else {
                val filterPattern =
                    charSequence.toString().uppercase().trim()
                for (coins in coinlerArrayList) {
                    if (coins.symbol?.uppercase()?.contains(filterPattern) == true) {
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
            coinlerFilterArrayList = (filterResults.values as MutableList<CoinsResponseItem>)
            notifyDataSetChanged()
        }
    }

    interface OnClickListener {
        //secilen coin bilgilerii getirmek icin onClick atandi
        fun onItemClickedCoinlerList(coins: CoinsResponseItem)
    }

}