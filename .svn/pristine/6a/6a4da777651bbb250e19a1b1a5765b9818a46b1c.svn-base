package ngo.friendship.satellite.ui.beneficiary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import ngo.friendship.satellite.databinding.BeneficiaryPersonalInfoBinding
import ngo.friendship.satellite.model.Beneficiary

class BeneficiaryPersonalInfoFragment : Fragment() {
    private lateinit var binding: BeneficiaryPersonalInfoBinding
    var beneficiaryInfo: String = ""
    lateinit var beneficiary: Beneficiary

    companion object {
        fun newInstance(BENEFICIARY_INFO: String): BeneficiaryPersonalInfoFragment {
            val homePage = BeneficiaryPersonalInfoFragment()
            val args = Bundle()
            args.putString("BENEFICIARY_INFO", BENEFICIARY_INFO)
            homePage.arguments = args
            return homePage
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BeneficiaryPersonalInfoBinding.inflate(inflater, container, false)

        try {
            beneficiary = Gson().fromJson(beneficiaryInfo, Beneficiary::class.java)
            binding.model = beneficiary
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            beneficiaryInfo = requireArguments().getString("BENEFICIARY_INFO", "")
        }
    }
}