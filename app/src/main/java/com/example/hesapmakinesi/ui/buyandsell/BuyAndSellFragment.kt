package com.example.hesapmakinesi.ui.buyandsell

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.hesapmakinesi.databinding.FragmentBuyandsellBinding
import com.example.hesapmakinesi.ui.buyandsell.adapter.BuyAndSellAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyAndSellFragment : Fragment() {
    private lateinit var binding: FragmentBuyandsellBinding
    private lateinit var buyAndSellAdapter: BuyAndSellAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBuyandsellBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerTabLayout()
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        //ekran yan kaydırılarak değiştirildiğinde tabLayout itemleride kendini günceller.
        val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        }
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun viewPagerTabLayout() {
        buyAndSellAdapter =
            BuyAndSellAdapter(
                fragmentManager = childFragmentManager,
                lifecycle = lifecycle,
                numsOfTabs = binding.tabLayout.tabCount,
                bundle = arguments
            )
        binding.viewPager.adapter = buyAndSellAdapter
    }
}