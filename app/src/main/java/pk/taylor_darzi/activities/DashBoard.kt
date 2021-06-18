package pk.taylor_darzi.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.customer_data_layout.*
import pk.taylor_darzi.BaseActivity
import pk.taylor_darzi.BuildConfig
import pk.taylor_darzi.R
import pk.taylor_darzi.adapters.FragmentsAdapter
import pk.taylor_darzi.customViews.CustomAlertDialogue
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

    private lateinit var auth: FirebaseAuth
    private var pagerAdapter: FragmentsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        mActivity = this
        Utils.setCurrentActivity(mActivity as DashBoard)
        Config.getFirebaseAuth
        Config.getFirebaseFirestore.collection(Config.User).document(Config.Version).addSnapshotListener { snapshot, e ->
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
        pagerAdapter = FragmentsAdapter(this)
        var arrayList : ArrayList<Fragment> = ArrayList()
        arrayList.add(HomeFragnment())
        arrayList.add(CustomersFragnment())
        arrayList.add(OrdersFragnment())
        arrayList.add(MyAccountFragnment())

        pagerAdapter!!.setFragments(arrayList)
        viewpager.adapter = pagerAdapter
        TabLayoutMediator(tabs, viewpager) { tab, position ->  getTabIcon(tab, position) }.attach()

    }

    fun getTabIcon(tabIcon: TabLayout.Tab, postion: Int)
    {
        if(tabIcon.icon ==null)
        {
            when (postion) {
                0 -> {
                    tabIcon.icon = getDrawable(R.drawable.home_selector)
                    tabIcon.icon!!.setTintList(getColorStateList(R.color.icon_color_selector))
                }
                1 -> {
                    tabIcon.icon = getDrawable(R.drawable.customer_selector)
                    tabIcon.icon!!.setTintList(getColorStateList(R.color.icon_color_selector))
                }
                2 -> {
                    tabIcon.icon = getDrawable(R.drawable.delivery_selector)
                    tabIcon.icon!!.setTintList(getColorStateList(R.color.icon_color_selector))
                }
                3 -> {
                    tabIcon.icon = getDrawable(R.drawable.user_selector)
                    tabIcon.icon!!.setTintList(getColorStateList(R.color.icon_color_selector))
                }
            }
        }

    }

    override fun onResume()
    {
        super.onResume()
        Utils.setCurrentActivity(mActivity as DashBoard)
    }
    fun fragmentMethod(i: Int) {
        viewpager.currentItem= i

    }
    override fun onBackPressed() {
        var fragment: Fragment= supportFragmentManager.fragments.get(1)
        var fragmentO: Fragment= supportFragmentManager.fragments.get(2)
        if(fragment!= null && fragment.isResumed)
        {
            if( (fragment as CustomersFragnment).doBack())
                super.onBackPressed()
        }
        else if(fragment!= null && fragment.isResumed)
        {
            if( (fragment as OrdersFragnment).doBack())
                super.onBackPressed()
        }
        else
            super.onBackPressed()
    }
    override fun onDestroy() {
        super.onDestroy()
        LoadingProgressDialog.destroy(mActivity)
    }


}