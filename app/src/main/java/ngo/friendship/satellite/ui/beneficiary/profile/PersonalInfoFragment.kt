package ngo.friendship.satellite.ui.beneficiary.profile

import android.content.ContentValues.TAG
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ngo.friendship.satellite.R
import ngo.friendship.satellite.databinding.ActivityBeneficiariesDetailsBinding
import ngo.friendship.satellite.databinding.BeneficiaryPersonalInfoBinding
import ngo.friendship.satellite.model.Beneficiary
import ngo.friendship.satellite.viewmodels.OfflineViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PersonalInfoFragment : Fragment() {
    private var beneficiary: Beneficiary = Beneficiary()
    private lateinit var binding: BeneficiaryPersonalInfoBinding;
    val viewModel: OfflineViewModel by viewModels()

    companion object {
        fun newInstance(data: String): PersonalInfoFragment {
            val fragment = PersonalInfoFragment()
            val args = Bundle()
            args.putString("key", data)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BeneficiaryPersonalInfoBinding.inflate(
            layoutInflater
        )

        val data = arguments?.getString("key")
        if (data != null) {
            val gson = Gson()
            beneficiary = gson.fromJson(data, Beneficiary::class.java)


        }
try {
    activity?.let {
        viewModel.getSingleBenef(beneficiary.benefCodeFull)
            .observe(it) { benefModel ->
                binding.model = benefModel;
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                val date: Date = inputFormat.parse(benefModel.createDate)
                val formattedDate: String = outputFormat.format(date)

                binding.tvReg.setText(""+formattedDate)
                if (benefModel.mobileNumber.isNullOrEmpty()){
                    binding.tvMobileNo.setText("Not Available ")
                }else{
                    binding.tvMobileNo.setText("${benefModel.mobileNumber}")
                }


                val dob: Date = inputFormat.parse(benefModel.dob)
                val formattedddob: String = outputFormat.format(dob)

                binding.tvDob.setText(""+formattedddob)
                if (benefModel.benefCode.isNullOrEmpty()){
                    binding.tvBenefInfo.visibility =View.VISIBLE
                }else{
                    binding.tvBenefInfo.visibility =View.GONE
                }


                if (benefModel.relationToGurdian.isNullOrEmpty()){
                    binding.tvGuardian.setText("${benefModel.guardianName}".replace("null",""))
                }else{
                    binding.tvGuardian.setText("${benefModel.guardianName} (${benefModel.relationToGurdian})".replace("null",""))
                }
            }
    }
}catch (e:Exception){e.printStackTrace()}


        return binding.root
    }
}