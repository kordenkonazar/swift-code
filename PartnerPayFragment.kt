package com.citydrive.app.partners.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.citydrive.app.MySingleton
import com.citydrive.app.R
import com.citydrive.app.databinding.FragmentPartnerPayBinding
import com.citydrive.app.model.partnerOrderCreate.PartnerOrderCreate
import com.citydrive.app.model.partnerOrderCreate.PartnerOrderCreateResponse
import com.citydrive.app.retrofit.RetroClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class PartnerPayFragment : Fragment() {

    private lateinit var mBinding: FragmentPartnerPayBinding

    private val api = RetroClient.apiService
    private var createOrder: Call<PartnerOrderCreate>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_partner_pay, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            toolbarPartnersPay.setupWithNavController(
                findNavController(),
                AppBarConfiguration(findNavController().graph)
            )
            toolbarPartnersPay.title = "Заказ доставки"

            btnPartnersPay.setOnClickListener(this@PartnerPayFragment::onClick)
        }
    }

    private fun onClick(view: View) {
        when (view) {
            mBinding.btnPartnersPay -> {
                val pattern = Pattern.compile("^\\+380\\d{9}\$")
                val phoneString = mBinding.etPartnersPayPhone.text.toString()
                val matcher = pattern.matcher(phoneString)
                if (mBinding.etPartnersPayAddress.text.isNullOrEmpty()) {
                    mBinding.inputPartnersPayAddress.error =
                        resources.getText(R.string.sign_up_error_text)
                } else if (mBinding.etPartnersPayHouseNumber.text.isNullOrEmpty()
                ) {
                    mBinding.inputPartnersPayHouseNumber.error =
                        resources.getText(R.string.sign_up_error_text)
                    mBinding.inputPartnersPayAddress.error = null
                } else if (matcher.matches()) {
                    mBinding.inputPartnersPayAddress.error = null
                    mBinding.inputPartnersPayHouseNumber.error = null
                    mBinding.inputPartnersPayPhone.error = null
                    val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                createOrder()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                dialog.dismiss()
                            }
                        }
                    }
                    val builder =
                        AlertDialog.Builder(requireContext())
                    builder.setMessage("Вы уверены?").setPositiveButton("Да", dialogClickListener)
                        .setNegativeButton("Нет", dialogClickListener).show()
                } else {
                    mBinding.inputPartnersPayPhone.error =
                        resources.getText(R.string.number_incorrect)
                    mBinding.inputPartnersPayAddress.error = null
                    mBinding.inputPartnersPayHouseNumber.error = null
                }
            }
        }
    }

    private fun createOrder() {
        createOrder = api.partnerCreateOrder(
            "Bearer ${MySingleton.getInstance()?.idPartners}",
            PartnerOrderCreateResponse(
                delivery_phone = mBinding.etPartnersPayPhone.text.toString(),
                is_active = true,
                delivery_details = "",
                delivery_building_number = mBinding.etPartnersPayHouseNumber.text.toString(),
                delivery_apartment_number = mBinding.etPartnersPayApartmentNumber.text.toString(),
                delivery_date = "",
                delivery_street_name = mBinding.etPartnersPayAddress.text.toString(),
                value = mBinding.etPartnersPaySum.text.toString()
            )
        )

        createOrder?.enqueue(object : Callback<PartnerOrderCreate> {
            override fun onResponse(
                call: Call<PartnerOrderCreate>,
                response: Response<PartnerOrderCreate>
            ) {
                val address =
                    response.body()?.delivery_street_name + ", кв " + response.body()?.delivery_building_number + ", " + response.body()?.delivery_apartment_number + " эт"
                val action =
                    PartnerPayFragmentDirections.actionPartnerPayFragmentToPartnersDeliveryInProgressFragment(
                        address,
                        response.body()?.order_number,
                        response.body()?.id
                    )

                findNavController().navigate(action)
            }

            override fun onFailure(call: Call<PartnerOrderCreate>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onStop() {
        super.onStop()
        createOrder?.let { it.cancel() }
    }
}