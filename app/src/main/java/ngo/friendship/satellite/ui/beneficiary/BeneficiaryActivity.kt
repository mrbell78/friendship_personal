package ngo.friendship.satellite.ui.beneficiary

import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.constants.ActivityDataKey
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.databinding.ActivityBeneficiaryBinding
import ngo.friendship.satellite.views.MdiTextView

/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */
@AndroidEntryPoint
class BeneficiaryActivity : BaseActivity() {
    private lateinit var binding: ActivityBeneficiaryBinding
    private lateinit var viewModel: BeneficiaryViewModel
    var type = Constants.BENEFICIARY_LIST_mHEALTH;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeneficiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.beneficiary_list)
        enableBackButton()
        viewModel = run {
            ViewModelProvider(this).get(BeneficiaryViewModel::class.java)
        }

        if (intent.extras!!.containsKey(ActivityDataKey.INTENT_DATA_PASS)) {
            type = intent.extras!!.getString(ActivityDataKey.INTENT_DATA_PASS).toString()
            if (type.contains(Constants.BENEFICIARY_LIST_mHEALTH)){
                isTabActiveDeactive(
                    binding.llTabmHealthBenef,
                    binding.tvTabAll,
                    binding.mdiTabAllIcon,
                    binding.llTabMyBenef,
                    binding.tvTabMaternalMother,
                    binding.mdiTabMaternalMotherIcon
                )
            }
            if (type.contains(Constants.BENEFICIARY_LIST_PARAMEDIC)){
                isTabActiveDeactive(
                    binding.llTabMyBenef,
                    binding.tvTabMaternalMother,
                    binding.mdiTabMaternalMotherIcon,
                    binding.llTabmHealthBenef,
                    binding.tvTabAll,
                    binding.mdiTabAllIcon
                )
            }
        }
        supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainer, BeneficiaryListFragment.newInstance(type, ""
            )
        ).commit()

        binding.llTabmHealthBenef.updatePadding(top = 5, bottom = 5)
        binding.llTabMyBenef.updatePadding(top = 5, bottom = 5)

        binding.llTabmHealthBenef.setOnClickListener {
            App.showProgressBar(this@BeneficiaryActivity)
            isTabActiveDeactive(
                binding.llTabmHealthBenef,
                binding.tvTabAll,
                binding.mdiTabAllIcon,
                binding.llTabMyBenef,
                binding.tvTabMaternalMother,
                binding.mdiTabMaternalMotherIcon
            )

//            supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.fragmentContainer,
//                    BeneficiaryListFragment.newInstance(Constants.BENEFICIARY_LIST_mHEALTH, "")
//                )
//                .commit()

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    BeneficiaryListFragment.newInstance(Constants.BENEFICIARY_LIST_mHEALTH, "")
                )
                .commit()
        }
        binding.llTabMyBenef.setOnClickListener {
            App.showProgressBar(this@BeneficiaryActivity)

            isTabActiveDeactive(
                binding.llTabMyBenef,
                binding.tvTabMaternalMother,
                binding.mdiTabMaternalMotherIcon,
                binding.llTabmHealthBenef,
                binding.tvTabAll,
                binding.mdiTabAllIcon
            )

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    BeneficiaryListFragment.newInstance(Constants.BENEFICIARY_LIST_PARAMEDIC, "")
                ).commit()
        }


    }

    private fun isTabActiveDeactive(
        llTabactvieName: LinearLayout,
        tvTabActiveName: TextView,
        mdiActive: MdiTextView,
        llTabDeactvieName1: LinearLayout,
        tvTabDeactvieName1: TextView,
        mdiDeactive1: MdiTextView
    ) {

        llTabactvieName.setBackground(
            ContextCompat.getDrawable(
                this,
                R.drawable.border_rounded_corner
            )
        )
        tvTabActiveName.setTextColor(ContextCompat.getColor(this, R.color.white))
        mdiActive.setTextColor(ContextCompat.getColor(this, R.color.white))

        llTabDeactvieName1.setBackground(
            ContextCompat.getDrawable(
                this,
                R.drawable.border_rounded_corner_white
            )
        )

        tvTabDeactvieName1.setTextColor(ContextCompat.getColor(this, R.color.black))
        mdiDeactive1.setTextColor(ContextCompat.getColor(this, R.color.black))


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.common_menu_search, menu)
        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.equals("")) {
                    if (query != null) {
                        viewModel.selectedItem(query)
                    }
                } else {
                    viewModel.selectedItem("")
                }

                return true
            }
        })


        return true
    }

}