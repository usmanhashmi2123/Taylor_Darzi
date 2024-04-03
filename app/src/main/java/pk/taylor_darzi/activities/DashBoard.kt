package pk.taylor_darzi.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import pk.taylor_darzi.BaseActivity
import pk.taylor_darzi.BuildConfig
import pk.taylor_darzi.R
import pk.taylor_darzi.adapters.FragmentsAdapter
import pk.taylor_darzi.customViews.CustomAlertDialogue
import pk.taylor_darzi.databinding.ActivityDashboardBinding
import pk.taylor_darzi.fragments.CustomersFragnment
import pk.taylor_darzi.fragments.HomeFragnment
import pk.taylor_darzi.fragments.MyAccountFragnment
import pk.taylor_darzi.fragments.OrdersFragnment
import pk.taylor_darzi.interfaces.GenericEvents
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Constants
import pk.taylor_darzi.utils.LoadingProgressDialog
import pk.taylor_darzi.utils.Utils


class DashBoard : BaseActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private var pagerAdapter: FragmentsAdapter? = null
    private var created = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mActivity = this
        Utils.setCurrentActivity(mActivity as DashBoard)
        Config.getFirebaseAuth
        onBackPressedDispatcher.addCallback(this, callback)
        created = true

    }


    fun setViewpager()
    {
        if(created)
        {
            pagerAdapter = FragmentsAdapter(this)
            val arrayList : ArrayList<Fragment> = ArrayList()
            arrayList.add(HomeFragnment())
            arrayList.add(CustomersFragnment())
            arrayList.add(OrdersFragnment())
            arrayList.add(MyAccountFragnment())

            pagerAdapter!!.setFragments(arrayList)
            binding.viewpager.adapter = pagerAdapter
            TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->  getTabIcon(tab, position) }.attach()
            created= false
        }
    }

    fun getTabIcon(tabIcon: TabLayout.Tab, postion: Int)
    {
        if(tabIcon.icon ==null)
        {
            when (postion) {
                0 -> {
                    tabIcon.icon = AppCompatResources.getDrawable(applicationContext,R.drawable.home_selector)
                    tabIcon.icon!!.setTintList(getColorStateList(R.color.icon_color_selector))
                }
                1 -> {
                    tabIcon.icon = AppCompatResources.getDrawable(applicationContext,R.drawable.customer_selector)
                    tabIcon.icon!!.setTintList(getColorStateList(R.color.icon_color_selector))
                }
                2 -> {
                    tabIcon.icon = AppCompatResources.getDrawable(applicationContext,R.drawable.delivery_selector)
                    tabIcon.icon!!.setTintList(getColorStateList(R.color.icon_color_selector))
                }
                3 -> {
                    tabIcon.icon = AppCompatResources.getDrawable(applicationContext,R.drawable.user_selector)
                    tabIcon.icon!!.setTintList(getColorStateList(R.color.icon_color_selector))
                }
            }
        }

    }

    override fun onResume()
    {
        super.onResume()
        Utils.setCurrentActivity(mActivity as DashBoard)
        Config.getFirebaseFirestore.collection(Config.User).document(Config.Version).addSnapshotListener { snapshot, e ->

            if(Utils.curentActivity is DashBoard)
            {
                if (e != null) {

                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists() && snapshot.get(Config.Version) != null) {

                    if( snapshot.get(Config.Version) as Long > BuildConfig.VERSION_CODE)
                    {
                        CustomAlertDialogue.instance?.showAlertDialog(mActivity,
                                getString(R.string.update),
                                getString(R.string.update_available),
                                getString(R.string.update_btn),
                                getString(R.string.cancel),
                                object : GenericEvents {
                                    override fun onGenericEvent(eventType: String, vararg args: Any?) {
                                        if (eventType.equals(Constants.ON_OK.toString())) {
                                            try {
                                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                                            } catch (e: ActivityNotFoundException) {
                                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                                            }
                                        }
                                    }
                                })
                    }

                }
            }

        }
        setViewpager()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 30)
        {
            if(grantResults[0] == PERMISSION_GRANTED)
            {
                Config.appToast(
                    "SMS Permission granted"
                )
            }
            else  Config.appToast(
                "SMS Permission denied"
            )
        }
    }
    fun fragmentMethod(i: Int) {
        binding.viewpager.currentItem= i

    }
    val callback = object : OnBackPressedCallback(true) {
        val fragment: Fragment= supportFragmentManager.fragments.get(1)
        val fragmentO: Fragment= supportFragmentManager.fragments.get(2)
        override fun handleOnBackPressed() {
            if (fragment.isResumed) {
                if ((fragment as CustomersFragnment).doBack()) {
                    isEnabled = false // Disable this callback to allow the default back press behavior
                    finish() // Call the default behavior of Activity.onBackPressed()
                }
            } else if (fragmentO.isResumed) {
                if ((fragmentO as OrdersFragnment).doBack()) {
                    isEnabled = false // Disable this callback to allow the default back press behavior
                    finish() // Call the default behavior of Activity.onBackPressed()
                }
            } else {
                isEnabled = false // Disable this callback to allow the default back press behavior
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingProgressDialog.destroy(mActivity)
    }


}