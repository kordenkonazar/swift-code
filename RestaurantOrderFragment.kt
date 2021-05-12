package com.citydrive.app.restaurants.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.citydrive.app.MainActivity

import com.citydrive.app.R
import com.citydrive.app.databinding.FragmentRestaurantOrderBinding

import com.citydrive.app.partners.fragment.PathnerDeliveryFragment
import com.citydrive.app.utils.CommonFragmentPagerAdapter

class RestaurantOrderFragment : Fragment(), OnPathnerListener {

    private lateinit var mBinding: FragmentRestaurantOrderBinding

    private lateinit var fragmentPagerAdapter: CommonFragmentPagerAdapter

    private val safeArgs: RestaurantOrderFragmentArgs by navArgs()
    private val allSum by lazy { safeArgs.allSum }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_order, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.toolbarRestaurantOrder.setupWithNavController(findNavController(), AppBarConfiguration(findNavController().graph))
        mBinding.toolbarRestaurantOrder.title = resources.getString(R.string.order)



        fragmentPagerAdapter = CommonFragmentPagerAdapter(childFragmentManager,
            PathnerDeliveryFragment()
                .apply {
                    arguments = bundleOf("is_restaurant" to false, "allSumOrder" to allSum)
                    setOnPathnerListener(this@RestaurantOrderFragment)
                },
            PathnerDeliveryFragmentCard()
                .apply {
                    arguments = bundleOf("is_restaurant" to false, "allSumOrder" to allSum)
                    setOnPathnerListener(this@RestaurantOrderFragment)
                }
        )

        fragmentPagerAdapter.titles = resources.getStringArray(R.array.anythingOrder)
        mBinding.viewPagerRestaurantOrder.adapter = fragmentPagerAdapter
        mBinding.tabLayoutRestaurantOrder.setupWithViewPager(mBinding.viewPagerRestaurantOrder)

        (requireActivity() as MainActivity).onBackPressedDispatcher.addCallback(object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.post { findNavController().popBackStack() }
            }
        })
    }

    override fun onDeliveryInfoClick() {
        val action = RestaurantOrderFragmentDirections.actionRestaurantOrderFragmentToRestaurantDeliveryTermsFragment()
        findNavController().navigate(action)
    }

}

interface OnPathnerListener{
    fun onDeliveryInfoClick()
}
