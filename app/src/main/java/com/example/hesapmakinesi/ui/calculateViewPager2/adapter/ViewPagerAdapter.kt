package com.example.hesapmakinesi.ui.calculateViewPager2.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hesapmakinesi.ui.calculateViewPager2.buyandsell.BuyFragment
import com.example.hesapmakinesi.ui.calculateViewPager2.buyandsell.SellFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val numsOfTabs: Int,
    private val bundle: Bundle?
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return numsOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> SellFragment(bundle)
            else -> BuyFragment(bundle)
        }
    }

}