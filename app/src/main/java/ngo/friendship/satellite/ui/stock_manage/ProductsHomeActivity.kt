package ngo.friendship.satellite.ui.stock_manage

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import dagger.hilt.android.AndroidEntryPoint
import ngo.friendship.satellite.R
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.constants.ActivityDataKey
import ngo.friendship.satellite.databinding.ActivityMyProductsBinding

@AndroidEntryPoint
class ProductsHomeActivity : BaseActivity() {
    private lateinit var binding: ActivityMyProductsBinding;
    var colorStateList: ColorStateList? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProductsBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        if (intent.extras != null) {
         var  activityPath: String? = intent.extras!!.getString(ActivityDataKey.ACTIVITY)
            if (activityPath.equals("AdjustmentConfirmActivity")){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, AdjustmentFragment.newInstance()).commit()
                binding.bottomNavigationView.menu.getItem(1).isChecked = true
            }else{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, StockRequisitionFragment.newInstance()).commit()
                binding.bottomNavigationView.menu.getItem(0).isChecked = true
            }
        }else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, StockRequisitionFragment.newInstance()).commit()
            binding.bottomNavigationView.menu.getItem(0).isChecked = true
        }
//        binding.tvHome.setTextColor(getResources().getColor(R.color.bottom_navigation_color_deactive));
        colorStateList =
            ColorStateList.valueOf(resources.getColor(R.color.bottom_navigation_color_deactive))
        //        binding.btnHome.setImageTintList(colorStateList);
        binding.bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            when (id) {
                R.id.nav_product_rise -> {
                    // binding.tvHome.setTextColor(getResources().getColor(R.color.bottom_navigation_color_deactive));
                    colorStateList =
                        ColorStateList.valueOf(resources.getColor(R.color.bottom_navigation_color_deactive))
                    // binding.btnHome.setImageTintList(colorStateList);
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, StockRequisitionFragment.newInstance())
                        .commit()
                    return@setOnItemSelectedListener true
                }

                R.id.nav_product_adjust -> {
                    //   binding.tvHome.setTextColor(getResources().getColor(R.color.bottom_navigation_color_deactive));
                    colorStateList =
                        ColorStateList.valueOf(resources.getColor(R.color.bottom_navigation_color_deactive))
                    // binding.btnHome.setImageTintList(colorStateList);
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AdjustmentFragment.newInstance()).commit()
                    return@setOnItemSelectedListener true
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_product_menu, menu)
        return true
    }
//
    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        int id = item.getItemId();
    //
    //        if (id == android.R.id.home) {
    //            onBackPressed();
    //            return true;
    //        } else if (id == R.id.nav_product_rise) {
    //            // finish the activity
    //
    //            startActivity(new Intent(this, RiseListActivity.class));
    //            return true;
    //        } else if (id == R.id.nav_product_adjust) {
    //            startActivity(new Intent(this, ProductAdjustmentActivity.class));
    //            return true;
    //        } else if (id == R.id.all_invoice) {
    //            startActivity(new Intent(this, InvoiceListActivity.class));
    //            return true;
    //        }
    //
    //
    //        return super.onOptionsItemSelected(item);
    //    }
}