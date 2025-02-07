package pk.taylor_darzi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pk.taylor_darzi.R
import pk.taylor_darzi.activities.ActivityStackManager
import pk.taylor_darzi.activities.DashBoard
import pk.taylor_darzi.dataModels.Customer
import pk.taylor_darzi.dataModels.CustomersList
import pk.taylor_darzi.databinding.HomeFragmentBinding
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Preferences
import pk.taylor_darzi.utils.Utils

class HomeFragnment : ParentFragnment() {
    private lateinit var binding: HomeFragmentBinding
    private var resumed = false
    private  var createdfirstTime = false
    private var ordersList: ArrayList<Customer> = ArrayList<Customer>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = HomeFragmentBinding.inflate(layoutInflater,  container, false)
      
        createdfirstTime = true
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        binding.logout.setOnClickListener(clickListener)
        binding.customersH.setOnClickListener(clickListener)
        binding.ordersH.setOnClickListener(clickListener)
        binding.remains.setOnClickListener(clickListener)
        if(createdfirstTime) getData()

    }
    private fun getData()
    {
        try{
            docRef?.addSnapshotListener { snapshot, e ->
                if(Utils.curentActivity is DashBoard)
                {
                    if (e != null) {
                        Config.appToast( e.message)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists() && snapshot.get(Config.Customers) != null) {

                        var list: List<Customer>? = snapshot.toObject(CustomersList::class.java)?.customers
                        customersList?.clear()
                        if(!list.isNullOrEmpty())
                        {
                            customersList?.addAll(list)
                            customers = customersList!!.size
                        }
                        else customers =0
                        filterList()
                    }
                    else Config.appToast( "Current data: null")
                }


            }
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            Config.appToast( ex.message)

        }
        createdfirstTime = false
    }

    fun filterList()
    {

        ordersList.clear()
        var baqaya = 0f
        if(customersList!= null)
        {
            binding.customerTotal.text = customersList!!.size.toString()
            for (customer in customersList!!)
            {
                val remAmount = Utils.getAmount(customer.order?.amountRemaining)

                if(remAmount > 0  || !customer.order?.deliveryDate.isNullOrBlank())
                {
                    ordersList.add(customer)
                    baqaya += remAmount
                }


            }
        }
        else binding.customerTotal.text = "0"

        binding.ordersTotal.text = ordersList.size.toString()
        binding.remainsTotal.text = baqaya.toString()
    }
    private val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.logout -> {
                Preferences.instance!!.saveStringPrefValue(Preferences.instance!!.PREF_USER_EMAIL, "")
                Preferences.instance!!.saveStringPrefValue(Preferences.instance!!.PREF_USER_PASS, "")
                Preferences.instance!!.saveStringPrefValue(Preferences.instance!!.PREF_USER_ID, "")
                Config.getFirebaseAuth.signOut()
                Config.currentUser = null
                Config.firebaseDb = null
                Config.auth=null
                ActivityStackManager.instance!!.startLoginActivity(requireActivity())
            }R.id.customers_h -> {
            (activity as DashBoard?)?.fragmentMethod(1)
        }
            R.id.orders_h ,  R.id.remains-> {
                (activity as DashBoard?)?.fragmentMethod(2)
            }


        }
    }
    override fun onPause() {
        super.onPause()
        resumed = false
    }
}