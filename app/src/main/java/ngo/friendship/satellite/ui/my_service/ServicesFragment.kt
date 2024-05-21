package ngo.friendship.satellite.ui.my_service

import android.content.Context
import android.content.Intent
import ngo.friendship.satellite.model.Beneficiary
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.databinding.FragmentServicesBinding
import ngo.friendship.satellite.views.MdiTextView
import java.util.ArrayList

class ServicesFragment : Fragment() {
    private lateinit var binding: FragmentServicesBinding;
    lateinit var entryPrams: ArrayList<String>;
    lateinit var beneficiaryList: ArrayList<Beneficiary>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryPrams = ArrayList()
        beneficiaryList = ArrayList();
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_services, container, false)



        App.loadApplicationData(requireContext())
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.fl_service_content,
                FollowUpListFragment.newInstance(Constants.TODAY_FOLLOW_UP)
            )?.commit()
//
binding.fbServiceAdd.setOnClickListener {
//    val serviceCategory = Intent(activity, ServiceCategoryActivity::class.java)
//    startActivity(serviceCategory)
}
        binding.llTabFollowUp.updatePadding(top = 5, bottom = 5);
        binding.llTabComplete.updatePadding(top = 5, bottom = 5);
//        binding.llTabAll.updatePadding(top = 5, bottom = 5);

        isTabActiveDeactive(
            binding.llTabFollowUp,
            binding.tvTabFollowUp,
            binding.mdiTabFollowUpIcon,
            binding.llTabComplete,
            binding.tvTabComplete,
            binding.mdiTabComplete
//            , binding.llTabAll,
//            binding.tvTabAll,
//            binding.mdiTabAll
        )

        binding.llTabFollowUp.setOnClickListener {
            isTabActiveDeactive(
                binding.llTabFollowUp,
                binding.tvTabFollowUp,
                binding.mdiTabFollowUpIcon,
                binding.llTabComplete,
                binding.tvTabComplete,
                binding.mdiTabComplete
                //,
//                binding.llTabAll,
//                binding.tvTabAll,
//                binding.mdiTabAll
            )

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fl_service_content,
                    FollowUpListFragment.newInstance(Constants.TODAY_FOLLOW_UP)
                )
                .commit()
        }

        binding.llTabComplete.setOnClickListener {
            isTabActiveDeactive(
                binding.llTabComplete,
                binding.tvTabComplete,
                binding.mdiTabComplete,
                binding.llTabFollowUp,
                binding.tvTabFollowUp,
                binding.mdiTabFollowUpIcon
                //,
//                binding.llTabAll,
//                binding.tvTabAll,
//                binding.mdiTabAll
            )


            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fl_service_content,
                    FollowUpListFragment.newInstance(Constants.COMPLETE_FOLLOW_UP)
                )
                .commit()


        }

//        binding.llTabAll.setOnClickListener {
//
//            isTabActiveDeactive(
//                binding.llTabAll,
//                binding.tvTabAll,
//                binding.mdiTabAll,
//                binding.llTabComplete,
//                binding.tvTabComplete,
//                binding.mdiTabComplete,
//                binding.llTabFollowUp,
//                binding.tvTabFollowUp,
//                binding.mdiTabFollowUpIcon
//
//            )
//
//
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.fl_service_content,
//                    FollowUpListFragment.newInstance(Constants.ALL_FOLLOW_UP)
//                )
//                .commit()
//        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun isTabActiveDeactive(
        llTabactvieName: LinearLayout,
        tvTabActiveName: TextView,
        mdiActive: MdiTextView,
        llTabDeactvieName: LinearLayout,
        tvTabDeactvieName: TextView,
        mdiDeactive: MdiTextView
    ) {

        llTabactvieName.setBackground(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.border_rounded_corner
            )
        )
        tvTabActiveName.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        mdiActive.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))

        llTabDeactvieName.setBackground(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.border_rounded_corner_white
            )
        )

        tvTabDeactvieName.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
        mdiDeactive.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
    }


    companion object {
        fun newInstance(): ServicesFragment {
            return ServicesFragment()
        }
    }
}