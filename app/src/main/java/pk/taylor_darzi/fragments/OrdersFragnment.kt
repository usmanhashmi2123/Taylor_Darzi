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
import pk.taylor_darzi.R
import pk.taylor_darzi.activities.DashBoard
import pk.taylor_darzi.adapters.OrdersRecyclerViewAdapter
import pk.taylor_darzi.dataModels.*
import pk.taylor_darzi.databinding.OrdersFragmentBinding
import pk.taylor_darzi.interfaces.fragmentbackEvents
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class OrdersFragnment : ParentFragnment(),  fragmentbackEvents {
    private lateinit var binding: OrdersFragmentBinding
            private var customerId = ""
    var selectedCustomer: Customer? =null
    private var resumed = false
    var customersAdapter: OrdersRecyclerViewAdapter?= null
    val date_Cal = Calendar.getInstance()
    private var ordersList: ArrayList<Customer> = ArrayList<Customer>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= OrdersFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        binding.customerDataI.shirtNaap.setOnClickListener(clickListener)
        binding.customerDataI.trouserNaap.setOnClickListener(clickListener)
        binding.customerDataI.backButton.setOnClickListener(clickListener)
        binding.customerDataI.editPencil.setOnClickListener(clickListener)
        binding.customerDataI.delete.setOnClickListener(clickListener)
        binding.customerDataI.extrainfoLayoutI.save.setOnClickListener(clickListener)
        binding.customerDataI.extrainfoLayoutI.wapsiVal.setOnClickListener(clickListener)
        binding.customerDataI.extrainfoLayoutI.wapsiConst.setOnClickListener(clickListener)
        if (binding.ordersDataShow.isVisible)
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
                        binding.customerDataI.backButton.callOnClick()
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
        binding.totalVal.text = baqaya.toString()
    }

    fun showList()
    {
        if(ordersList.isNullOrEmpty()) Config.appToast(requireActivity(), "No data to show")
        binding.ordersDataShow.visibility=View.VISIBLE
        binding.scrollviewOrders.visibility = View.GONE
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL,
            false)
        if(customersAdapter== null)
        {
            customersAdapter = OrdersRecyclerViewAdapter({customer -> adapterOnClick(customer)}, {deliver -> onDelivered(deliver)})

        }
        customersAdapter?.setData(ordersList)
        binding.recyclerViewOrders.adapter = customersAdapter
        customersAdapter?.notifyDataSetChanged()
    }

    private fun adapterOnClick(customer: Customer) {
        selectedCustomer = customer
        customerId = selectedCustomer?.name+"_"+selectedCustomer?.phone+"_"+selectedCustomer?.no
        binding.ordersDataShow.visibility=View.GONE
        binding.scrollviewOrders.visibility = View.VISIBLE
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
        binding.customerDataI.nameUserVal.isEnabled = enable
        binding.customerDataI.phoneUserVal.isEnabled = enable
        binding.customerDataI.wasoolVal.isEnabled = enable
        binding.customerDataI.remVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.lengthQVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.lengthBVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.shoulderVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.colarVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.chestVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.waistVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.hipVal.isEnabled = enable
        binding.customerDataI.trouserLayoutI.lengthSVal.isEnabled = enable
        binding.customerDataI.trouserLayoutI.panchaVal.isEnabled = enable
        binding.customerDataI.trouserLayoutI.tpocketCheckbox.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.embVal.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.spocketCheckbox.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.fpocketCheckbox.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.notesVal.isEnabled = enable
        binding.customerDataI.editPencil.isSelected = enable
        if(enable)
            binding.customerDataI.extrainfoLayoutI.save.visibility = View.VISIBLE
        else
            binding.customerDataI.extrainfoLayoutI.save.visibility = View.GONE

    }

    private fun showCustomerInfo(customer: Customer)
    {
        binding.customerDataI.nameUserVal.setText(customer.name)
        binding.customerDataI.phoneUserVal.setText(customer.phone)

        binding.customerDataI.noVal.text = customer.no.toString()
        var naapQameez: NaapQameez = customer.naapQameez!!
        binding.customerDataI.shirtLayoutI.lengthQVal.setText(naapQameez.shirtLength)
        binding.customerDataI.shirtLayoutI.lengthBVal.setText(naapQameez.armLength)
        binding.customerDataI.shirtLayoutI.shoulderVal.setText(naapQameez.shoulderLength)
        binding.customerDataI.shirtLayoutI.colarVal.setText(naapQameez.colarSize)
        binding.customerDataI.shirtLayoutI.chestVal.setText(naapQameez.chest)
        binding.customerDataI.shirtLayoutI.waistVal.setText(naapQameez.waist)
        binding.customerDataI.shirtLayoutI.hipVal.setText(naapQameez.ghera)
        var naapShalwar: NaapShalwar = customer.naapShalwar!!
        binding.customerDataI.trouserLayoutI.lengthSVal.setText(naapShalwar.shalwarLength)
        binding.customerDataI.trouserLayoutI.panchaVal.setText(naapShalwar.pancha)
        binding.customerDataI.trouserLayoutI.tpocketCheckbox.isChecked =naapShalwar.pocket

        var order: Order = customer.order!!
        binding.customerDataI.wasoolVal.setText(order.amountRcvd)
        binding.customerDataI.remVal.setText(order.amountRemaining)
        binding.customerDataI.extrainfoLayoutI.wapsiVal.text = order.deliveryDate

        var extraInfo: ExtraInfo = customer.extraInfo!!
        binding.customerDataI.extrainfoLayoutI.embVal.setText(extraInfo.karhaiNo)
        binding.customerDataI.extrainfoLayoutI.fpocketCheckbox.isChecked =extraInfo.pocketFront
        binding.customerDataI.extrainfoLayoutI.spocketCheckbox.isChecked = extraInfo.pocketSide
        binding.customerDataI.extrainfoLayoutI.notesVal.setText(extraInfo.notes)
        binding.scrollviewOrders.fullScroll(ScrollView.FOCUS_UP)
    }
    private fun updateCustomer()
    {


        docRef!!.update(Config.Customers, customersList)
            .addOnCompleteListener(requireActivity()) { task1 ->
                if (task1.isSuccessful) {
                    Config.appToast(requireActivity(), getString(R.string.updated))
                    selectedCustomer = null
                    binding.customerDataI.backButton.callOnClick()
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
            selectedCustomer!!.name = binding.customerDataI.nameUserVal.text.toString()
            selectedCustomer!!.phone = binding.customerDataI.phoneUserVal.text.toString()
            var naapQameez: NaapQameez = selectedCustomer!!.naapQameez!!
            naapQameez.shirtLength = binding.customerDataI.shirtLayoutI.lengthQVal.text.toString()
            naapQameez.armLength = binding.customerDataI.shirtLayoutI.lengthBVal.text.toString()
            naapQameez.shoulderLength = binding.customerDataI.shirtLayoutI.shoulderVal.text.toString()
            naapQameez.colarSize = binding.customerDataI.shirtLayoutI.colarVal.text.toString()
            naapQameez.chest = binding.customerDataI.shirtLayoutI.chestVal.text.toString()
            naapQameez.waist = binding.customerDataI.shirtLayoutI.waistVal.text.toString()
            naapQameez.ghera = binding.customerDataI.shirtLayoutI.hipVal.text.toString()
            var naapShalwar: NaapShalwar = selectedCustomer!!.naapShalwar!!
            naapShalwar.shalwarLength = binding.customerDataI.trouserLayoutI.lengthSVal.text.toString()
            naapShalwar.pancha = binding.customerDataI.trouserLayoutI.panchaVal.text.toString()
            naapShalwar.pocket = binding.customerDataI.trouserLayoutI.tpocketCheckbox.isChecked

            var order: Order = selectedCustomer!!.order!!
            order.amountRcvd = binding.customerDataI.extrainfoLayoutI.wapsiVal.text.toString()
            order.amountRemaining = binding.customerDataI.remVal.text.toString()
            order.deliveryDate = binding.customerDataI.extrainfoLayoutI.wapsiVal.text.toString()

            var extraInfo: ExtraInfo = selectedCustomer!!.extraInfo!!
            extraInfo.karhaiNo = binding.customerDataI.extrainfoLayoutI.embVal.text.toString()
            extraInfo.pocketFront = binding.customerDataI.extrainfoLayoutI.fpocketCheckbox.isChecked
            extraInfo.pocketSide = binding.customerDataI.extrainfoLayoutI.spocketCheckbox.isChecked
            extraInfo.notes = binding.customerDataI.extrainfoLayoutI.notesVal.text.toString()

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
                enableDisable(!binding.customerDataI.editPencil.isSelected)
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
                            binding.customerDataI.backButton.callOnClick()
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
                if (binding.customerDataI.shirtLayoutI.root.isVisible) binding.customerDataI.shirtLayoutI.root.visibility = View.GONE
                else binding.customerDataI.shirtLayoutI.root.visibility = View.VISIBLE
            }
            R.id.trouser_naap -> {
                if (binding.customerDataI.trouserLayoutI.root.isVisible) binding.customerDataI.trouserLayoutI.root.visibility = View.GONE
                else binding.customerDataI.trouserLayoutI.root.visibility = View.VISIBLE
            }
            R.id.back_button -> {
                binding.ordersDataShow.visibility = View.VISIBLE
                binding.scrollviewOrders.visibility = View.GONE
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
                        binding.customerDataI.extrainfoLayoutI.wapsiVal.text = Config.getFormatedStringDate(date_Cal.time)
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
                    binding.customerDataI.extrainfoLayoutI.wapsiVal.text = ""
                }
                datePickerDialog.show()
            }
        }
    }

    override fun doBack(): Boolean {
        if(!binding.scrollviewOrders.isVisible)
        {
            binding.customerDataI.backButton.callOnClick()
            return false
        }
        else return true
    }
}