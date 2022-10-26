package com.example.hesapmakinesi.ui.calculateViewPager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.hesapmakinesi.databinding.FragmentCalculateBinding
import com.example.hesapmakinesi.ui.calculateViewPager2.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class CalculateFragment : Fragment() {
    private lateinit var binding: FragmentCalculateBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculateBinding.inflate(inflater, container, false)
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
        viewPagerAdapter =
            ViewPagerAdapter(requireActivity(), binding.tabLayout.tabCount, arguments)
        binding.viewPager.adapter = viewPagerAdapter
    }
}