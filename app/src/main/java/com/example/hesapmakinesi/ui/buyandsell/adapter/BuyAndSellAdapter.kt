package com.example.hesapmakinesi.ui.buyandsell.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hesapmakinesi.ui.buy.BuyFragment
import com.example.hesapmakinesi.ui.sell.SellFragment

class BuyAndSellAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val numsOfTabs: Int,
    private val bundle: Bundle?
) :
    FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int {
        return numsOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> SellFragment().apply {
                this.arguments = bundle
            }
            else -> BuyFragment().apply {
                this.arguments = bundle
            }
        }
    }

}