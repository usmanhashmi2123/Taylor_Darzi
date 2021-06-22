package pk.taylor_darzi.fragments

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.customer_data_layout.*
import kotlinx.android.synthetic.main.extrainfo_layout.*
import kotlinx.android.synthetic.main.orders_fragment.*
import kotlinx.android.synthetic.main.shirt_layout.*
import kotlinx.android.synthetic.main.trouser_layout.*
import pk.taylor_darzi.R
import pk.taylor_darzi.activities.DashBoard
import pk.taylor_darzi.adapters.OrdersRecyclerViewAdapter
import pk.taylor_darzi.dataModels.*
import pk.taylor_darzi.interfaces.fragmentbackEvents
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class OrdersFragnment : ParentFragnment(),  fragmentbackEvents {
    private var customerId = ""
    var selectedCustomer: Customer? =null
    private var resumed = false
    var customersAdapter: OrdersRecyclerViewAdapter?= null
    val date_Cal = Calendar.getInstance()
    private var ordersList: ArrayList<Customer> = ArrayList<Customer>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.orders_fragment, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        shirt_naap.setOnClickListener(clickListener)
        trouser_naap.setOnClickListener(clickListener)
        back_button.setOnClickListener(clickListener)
        edit_pencil.setOnClickListener(clickListener)
        delete.setOnClickListener(clickListener)
        save.setOnClickListener(clickListener)
        wapsi_Val.setOnClickListener(clickListener)
        wapsi_const.setOnClickListener(clickListener)
        if (orders_data_show.isVisible)
            getData()
    }
    override fun onPause() {
        super.onPause()
        resumed = false

    }
    private fun getData()
    {
        try{
            docRef?.addSnapshotListener { snapshot, e ->
                if(Utils.curentActivity is DashBoard)
                {
                    if (e != null) {
                        Config.appToast(requireActivity(), e.message)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists() && snapshot.get(Config.Customers) != null) {
                        selectedCustomer = null
                        back_button.callOnClick()
                        var list: List<Customer>? = snapshot.toObject(CustomersList::class.java)?.customers
                        customersList?.clear()
                        if(!list.isNullOrEmpty()) customersList?.addAll(list)

                    }
                    else Config.appToast(requireActivity(), "Current data: null")
                    filterList()
                }

            }
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            Config.appToast(requireActivity(), ex.message)

        }

    }
    fun filterList()
    {
        ordersList.clear()
        var baqaya = 0f
        if(customersList!= null)
        {
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

        showList()
        total_Val.text = baqaya.toString()
    }

    fun showList()
    {
        if(ordersList.isNullOrEmpty()) Config.appToast(requireActivity(), "No data to show")
        orders_data_show.visibility=View.VISIBLE
        scrollview_orders.visibility = View.GONE
        recyclerView_orders.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL,
            false)
        if(customersAdapter== null)
        {
            customersAdapter = OrdersRecyclerViewAdapter({customer -> adapterOnClick(customer)}, {deliver -> onDelivered(deliver)})

        }
        customersAdapter?.setData(ordersList)
        recyclerView_orders.adapter = customersAdapter
        customersAdapter?.notifyDataSetChanged()
    }

    private fun adapterOnClick(customer: Customer) {
        selectedCustomer = customer
        customerId = selectedCustomer?.name+"_"+selectedCustomer?.phone+"_"+selectedCustomer?.no
        orders_data_show.visibility=View.GONE
        scrollview_orders.visibility = View.VISIBLE
        enableDisable(false)
        showCustomerInfo(selectedCustomer!!)

    }
    private fun onDelivered(deliver: Customer) {

        selectedCustomer = deliver
        customerId = selectedCustomer?.name+"_"+selectedCustomer?.phone+"_"+selectedCustomer?.no
        selectedCustomer!!.order?.deliveryDate =""
        selectedCustomer!!.order?.amountRcvd = (Utils.getAmount(selectedCustomer!!.order!!.amountRemaining)+Utils.getAmount(selectedCustomer!!.order!!.amountRcvd)).toString()
        selectedCustomer!!.order?.amountRemaining =""
        updateCustomer()
    }
    private fun enableDisable(enable: Boolean)
    {
        name_user_Val.isEnabled = enable
        phone_user_Val.isEnabled = enable
        wasool_Val.isEnabled = enable
        rem_Val.isEnabled = enable
        length_q_Val.isEnabled = enable
        length_b_Val.isEnabled = enable
        shoulder_Val.isEnabled = enable
        colar_Val.isEnabled = enable
        chest_Val.isEnabled = enable
        waist_Val.isEnabled = enable
        hip_Val.isEnabled = enable
        length_s_Val.isEnabled = enable
        pancha_Val.isEnabled = enable
        tpocket_checkbox.isEnabled = enable
        emb_Val.isEnabled = enable
        spocket_checkbox.isEnabled = enable
        fpocket_checkbox.isEnabled = enable
        notes_Val.isEnabled = enable
        edit_pencil.isSelected = enable
        if(enable)
            save.visibility = View.VISIBLE
        else
            save.visibility = View.GONE

    }


    private fun showCustomerInfo(customer: Customer)
    {
        name_user_Val.setText(customer.name)
        phone_user_Val.setText(customer.phone)

        no_Val.text = customer.no.toString()
        var naapQameez: NaapQameez = customer.naapQameez!!
        if(naapQameez!= null)
        {
            length_q_Val.setText(naapQameez.shirtLength)
            length_b_Val.setText(naapQameez.armLength)
            shoulder_Val.setText(naapQameez.shoulderLength)
            colar_Val.setText(naapQameez.colarSize)
            chest_Val.setText(naapQameez.chest)
            waist_Val.setText(naapQameez.waist)
            hip_Val.setText(naapQameez.ghera)
        }
        var naapShalwar: NaapShalwar = customer.naapShalwar!!
        if(naapShalwar!= null)
        {
            length_s_Val.setText(naapShalwar.shalwarLength)
            pancha_Val.setText(naapShalwar.pancha)
            tpocket_checkbox.isChecked =naapShalwar.pocket

        }
        var order: Order = customer.order!!
        if(order!= null)
        {
            wasool_Val.setText(order.amountRcvd)
            rem_Val.setText(order.amountRemaining)
            wapsi_Val.text = order.deliveryDate

        }
        var extraInfo: ExtraInfo = customer.extraInfo!!
        if(extraInfo!= null)
        {
            emb_Val.setText(extraInfo.karhaiNo)
            fpocket_checkbox.isChecked =extraInfo.pocketFront
            spocket_checkbox.isChecked = extraInfo.pocketSide
            notes_Val.setText(extraInfo.notes)
        }
        shirt_layout.visibility = View.GONE
        trouser_layout.visibility = View.GONE
        scrollview_orders.fullScroll(ScrollView.FOCUS_UP)
    }
    private fun updateCustomer()
    {


        docRef!!.update(Config.Customers, customersList)
            .addOnCompleteListener(requireActivity()) { task1 ->
                if (task1.isSuccessful) {
                    Config.appToast(requireActivity(), getString(R.string.updated))
                    selectedCustomer = null
                    back_button.callOnClick()
                } else Config.appToast(requireActivity(), "failed to update")
            }.addOnFailureListener(requireActivity()){ task ->
            Config.appToast(
                requireActivity(),
                task.message
            )
        }
    }

    private fun addData()
    {
        try{
            selectedCustomer!!.name = name_user_Val.text.toString()
            selectedCustomer!!.phone = phone_user_Val.text.toString()
            var naapQameez: NaapQameez = selectedCustomer!!.naapQameez!!
            if(naapQameez!= null)
            {
                naapQameez.shirtLength = length_q_Val.text.toString()
                naapQameez.armLength = length_b_Val.text.toString()
                naapQameez.shoulderLength = shoulder_Val.text.toString()
                naapQameez.colarSize = colar_Val.text.toString()
                naapQameez.chest = chest_Val.text.toString()
                naapQameez.waist = waist_Val.text.toString()
                naapQameez.ghera = hip_Val.text.toString()
            }
            var naapShalwar: NaapShalwar = selectedCustomer!!.naapShalwar!!
            if(naapShalwar!= null)
            {
                naapShalwar.shalwarLength = length_s_Val.text.toString()
                naapShalwar.pancha = pancha_Val.text.toString()
                naapShalwar.pocket = tpocket_checkbox.isChecked

            }
            var order: Order = selectedCustomer!!.order!!
            if(order!= null)
            {
                order.amountRcvd = wasool_Val.text.toString()
                order.amountRemaining = rem_Val.text.toString()
                order.deliveryDate = wapsi_Val.text.toString()

            }
            var extraInfo: ExtraInfo = selectedCustomer!!.extraInfo!!
            if(extraInfo!= null)
            {
                extraInfo.karhaiNo = emb_Val.text.toString()
                extraInfo.pocketFront = fpocket_checkbox.isChecked
                extraInfo.pocketSide = spocket_checkbox.isChecked
                extraInfo.notes = notes_Val.text.toString()
            }

            for ((index, customer) in customersList!!.withIndex()) {
                var id = customer.name+"_"+customer.phone+"_"+customer.no
                if(customerId.equals(id))
                {
                    customersList!!.set(index, selectedCustomer!!)
                    break
                }
            }

        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            Config.appToast(requireActivity(), ex.message)
        }

    }
    private val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.edit_pencil -> {
                enableDisable(!edit_pencil.isSelected)
            }
            R.id.delete -> {
                if(selectedCustomer!= null)
                {
                    docRef!!.update(Config.Customers, FieldValue.arrayRemove(selectedCustomer)).addOnCompleteListener(
                        requireActivity()
                    ){ task1  ->
                        if (task1.isSuccessful) {
                            Config.appToast(requireActivity(), "Data Removed")
                            selectedCustomer = null
                            back_button.callOnClick()
                        } else Config.appToast(requireActivity(), "failed to remove")
                    }.addOnFailureListener(requireActivity()){ task ->
                        Config.appToast(
                            requireActivity(),
                            task.message
                        )
                    }

                }

            }
            R.id.shirt_naap -> {
                if (shirt_layout.isVisible) shirt_layout.visibility = View.GONE
                else shirt_layout.visibility = View.VISIBLE
            }
            R.id.trouser_naap -> {
                if (trouser_layout.isVisible) trouser_layout.visibility = View.GONE
                else trouser_layout.visibility = View.VISIBLE
            }
            R.id.back_button -> {
                orders_data_show.visibility = View.VISIBLE
                scrollview_orders.visibility = View.GONE
            }
            R.id.save -> {
                addData()
                updateCustomer()
            }
            R.id.wapsi_Val, R.id.wapsi_const -> {
                val datePickerDialog = DatePickerDialog(
                    requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        date_Cal.set(Calendar.YEAR, year)
                        date_Cal.set(Calendar.MONTH, monthOfYear)
                        date_Cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        wapsi_Val.text = Config.getFormatedStringDate(date_Cal.time)
                    },
                    date_Cal.get(Calendar.YEAR),
                    date_Cal.get(Calendar.MONTH),
                    date_Cal.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.setButton(
                    DialogInterface.BUTTON_NEUTRAL,
                    "Clear"
                ) { dialog: DialogInterface?, which: Int ->
                    datePickerDialog.cancel()
                    wapsi_Val.text = ""
                }
                datePickerDialog.show()
            }
        }
    }

    override fun doBack(): Boolean {
        if(!scrollview_orders.isVisible)
        {
            back_button.callOnClick()
            return false
        }
        else return true
    }
}