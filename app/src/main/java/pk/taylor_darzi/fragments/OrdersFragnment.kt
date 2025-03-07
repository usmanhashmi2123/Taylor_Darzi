package pk.taylor_darzi.fragments

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import pk.taylor_darzi.R
import pk.taylor_darzi.activities.DashBoard
import pk.taylor_darzi.adapters.OrdersRecyclerViewAdapter
import pk.taylor_darzi.customViews.CustomAlertDialogue
import pk.taylor_darzi.customViews.CustomDialogue
import pk.taylor_darzi.dataModels.*
import pk.taylor_darzi.databinding.OrdersFragmentBinding
import pk.taylor_darzi.interfaces.NumPadCommandKeyEvent
import pk.taylor_darzi.interfaces.fragmentbackEvents
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Preferences
import pk.taylor_darzi.utils.Utils
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class OrdersFragnment : ParentFragnment(),  fragmentbackEvents, NumPadCommandKeyEvent {
    private lateinit var binding: OrdersFragmentBinding
            private var customerId = ""
    var selectedCustomer: Customer? =null
    private var resumed = false
    var customersAdapter: OrdersRecyclerViewAdapter?= null
    val date_Cal = Calendar.getInstance()
    private var ordersList: ArrayList<Customer> = ArrayList<Customer>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= OrdersFragmentBinding.inflate(layoutInflater, container, false)

        binding.customerDataI.shirtNaap.setOnClickListener(clickListener)
        binding.customerDataI.trouserNaap.setOnClickListener(clickListener)
        binding.customerDataI.backButton.setOnClickListener(clickListener)
        binding.customerDataI.editPencil.setOnClickListener(clickListener)
        binding.customerDataI.delete.setOnClickListener(clickListener)
        binding.customerDataI.extrainfoLayoutI.save.setOnClickListener(clickListener)
        binding.customerDataI.extrainfoLayoutI.wapsiVal.setOnClickListener(clickListener)
        binding.customerDataI.extrainfoLayoutI.wapsiConst.setOnClickListener(clickListener)
       
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resumed = true
        if(Preferences.instance!!.isCustomKeyboard)
        {
            binding.customerDataI.shirtLayoutI.lengthQVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.lengthQVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.lengthQVal.onFocusChangeListener =
                focusChangeListener
            binding.customerDataI.shirtLayoutI.lengthQVal.setOnEditorActionListener(editor)

            binding.customerDataI.shirtLayoutI.lengthBVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.lengthBVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.lengthBVal.onFocusChangeListener =
                focusChangeListener
            binding.customerDataI.shirtLayoutI.lengthBVal.setOnEditorActionListener(editor)

            binding.customerDataI.shirtLayoutI.kuffVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.kuffVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.kuffVal.onFocusChangeListener =
                focusChangeListener
            binding.customerDataI.shirtLayoutI.kuffVal.setOnEditorActionListener(editor)


            binding.customerDataI.shirtLayoutI.shoulderWVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.shoulderWVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.shoulderWVal.onFocusChangeListener =
                focusChangeListener
            binding.customerDataI.shirtLayoutI.shoulderWVal.setOnEditorActionListener(editor)

            binding.customerDataI.shirtLayoutI.shoulderVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.shoulderVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.shoulderVal.onFocusChangeListener =
                focusChangeListener
            binding.customerDataI.shirtLayoutI.shoulderVal.setOnEditorActionListener(editor)

            binding.customerDataI.shirtLayoutI.colarVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.colarVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.colarVal.onFocusChangeListener = focusChangeListener
            binding.customerDataI.shirtLayoutI.colarVal.setOnEditorActionListener(editor)

            binding.customerDataI.shirtLayoutI.chestVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.chestVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.chestVal.onFocusChangeListener = focusChangeListener
            binding.customerDataI.shirtLayoutI.chestVal.setOnEditorActionListener(editor)

            binding.customerDataI.shirtLayoutI.waistVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.waistVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.waistVal.onFocusChangeListener = focusChangeListener
            binding.customerDataI.shirtLayoutI.waistVal.setOnEditorActionListener(editor)

            binding.customerDataI.shirtLayoutI.hipVal.setOnClickListener(clickListener)
            binding.customerDataI.shirtLayoutI.hipVal.setOnTouchListener(touchListener)
            binding.customerDataI.shirtLayoutI.hipVal.onFocusChangeListener = focusChangeListener
            binding.customerDataI.shirtLayoutI.hipVal.setOnEditorActionListener(editor)

            binding.customerDataI.trouserLayoutI.lengthSVal.setOnClickListener(clickListener)
            binding.customerDataI.trouserLayoutI.lengthSVal.setOnTouchListener(touchListener)
            binding.customerDataI.trouserLayoutI.lengthSVal.onFocusChangeListener =
                focusChangeListener
            binding.customerDataI.trouserLayoutI.lengthSVal.setOnEditorActionListener(editor)

            binding.customerDataI.trouserLayoutI.panchaVal.setOnClickListener(clickListener)
            binding.customerDataI.trouserLayoutI.panchaVal.setOnTouchListener(touchListener)
            binding.customerDataI.trouserLayoutI.panchaVal.onFocusChangeListener =
                focusChangeListener
            binding.customerDataI.trouserLayoutI.panchaVal.setOnEditorActionListener(editor)

            binding.customerDataI.extrainfoLayoutI.embVal.setOnClickListener(clickListener)
            binding.customerDataI.extrainfoLayoutI.embVal.setOnTouchListener(touchListener)
            binding.customerDataI.extrainfoLayoutI.embVal.onFocusChangeListener = focusChangeListener
            binding.customerDataI.extrainfoLayoutI.embVal.setOnEditorActionListener(editor)


            binding.customerDataI.extrainfoLayoutI.suitsVal.setOnClickListener(clickListener)
            binding.customerDataI.extrainfoLayoutI.suitsVal.setOnTouchListener(touchListener)
            binding.customerDataI.extrainfoLayoutI.suitsVal.onFocusChangeListener = focusChangeListener
            binding.customerDataI.extrainfoLayoutI.suitsVal.setOnEditorActionListener(editor)
        }
        else
        {
            binding.customerDataI.shirtLayoutI.lengthQVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.lengthQVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.lengthQVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.lengthQVal.setOnEditorActionListener(null)

            binding.customerDataI.shirtLayoutI.lengthBVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.lengthBVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.lengthBVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.lengthBVal.setOnEditorActionListener(null)

            binding.customerDataI.shirtLayoutI.kuffVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.kuffVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.kuffVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.kuffVal.setOnEditorActionListener(null)

            binding.customerDataI.shirtLayoutI.shoulderWVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.shoulderWVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.shoulderWVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.shoulderWVal.setOnEditorActionListener(null)

            binding.customerDataI.shirtLayoutI.shoulderVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.shoulderVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.shoulderVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.shoulderVal.setOnEditorActionListener(null)

            binding.customerDataI.shirtLayoutI.colarVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.colarVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.colarVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.colarVal.setOnEditorActionListener(null)

            binding.customerDataI.shirtLayoutI.chestVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.chestVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.chestVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.chestVal.setOnEditorActionListener(null)

            binding.customerDataI.shirtLayoutI.waistVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.waistVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.waistVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.waistVal.setOnEditorActionListener(null)

            binding.customerDataI.shirtLayoutI.hipVal.setOnClickListener(null)
            binding.customerDataI.shirtLayoutI.hipVal.setOnTouchListener(null)
            binding.customerDataI.shirtLayoutI.hipVal.onFocusChangeListener = null
            binding.customerDataI.shirtLayoutI.hipVal.setOnEditorActionListener(null)

            binding.customerDataI.trouserLayoutI.lengthSVal.setOnClickListener(null)
            binding.customerDataI.trouserLayoutI.lengthSVal.setOnTouchListener(null)
            binding.customerDataI.trouserLayoutI.lengthSVal.onFocusChangeListener = null
            binding.customerDataI.trouserLayoutI.lengthSVal.setOnEditorActionListener(null)

            binding.customerDataI.trouserLayoutI.panchaVal.setOnClickListener(null)
            binding.customerDataI.trouserLayoutI.panchaVal.setOnTouchListener(null)
            binding.customerDataI.trouserLayoutI.panchaVal.onFocusChangeListener = null
            binding.customerDataI.trouserLayoutI.panchaVal.setOnEditorActionListener(null)

            binding.customerDataI.extrainfoLayoutI.embVal.setOnClickListener(null)
            binding.customerDataI.extrainfoLayoutI.embVal.setOnTouchListener(null)
            binding.customerDataI.extrainfoLayoutI.embVal.onFocusChangeListener = null
            binding.customerDataI.extrainfoLayoutI.embVal.setOnEditorActionListener(null)

            binding.customerDataI.extrainfoLayoutI.suitsVal.setOnClickListener(null)
            binding.customerDataI.extrainfoLayoutI.suitsVal.setOnTouchListener(null)
            binding.customerDataI.extrainfoLayoutI.suitsVal.onFocusChangeListener = null
            binding.customerDataI.extrainfoLayoutI.suitsVal.setOnEditorActionListener(null)
        }
        if (binding.ordersDataShow.isVisible)
            getData()
    }
    override fun onPause() {
        super.onPause()
        binding.keyboard.visibility =View.GONE
        resumed = false

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
                        selectedCustomer = null
                        binding.customerDataI.backButton.callOnClick()
                        var list: List<Customer>? = snapshot.toObject(CustomersList::class.java)?.customers
                        customersList?.clear()
                        if(!list.isNullOrEmpty()) customersList?.addAll(list)

                    }
                    else Config.appToast( "Current data: null")
                    filterList()
                }

            }
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            Config.appToast( ex.message)

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
        if(ordersList.isNullOrEmpty()) Config.appToast( "No data to show")
        binding.ordersDataShow.visibility=View.VISIBLE
        binding.scrollviewOrders.visibility = View.GONE
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(
            requireActivity(), RecyclerView.VERTICAL,
            false
        )
        if(customersAdapter== null)
        {
            customersAdapter = OrdersRecyclerViewAdapter(
                { customer -> adapterOnClick(customer) },
                { deliver ->
                    onDelivered(
                        deliver
                    )
                },
                { bookingMsg -> sendSms(bookingMsg,getString(R.string.smsMessage),bookingMsg.order?.amountRemaining, bookingMsg.order?.suits,bookingMsg.order?.deliveryDate) },
                { readyMsg -> sendSms(readyMsg,getString(R.string.readyMessage),readyMsg.order?.amountRemaining, readyMsg.order?.suits, null) })

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
    private fun sendSms(customer: Customer, msg:String, amountRemaining:String?, suits:String?, returnDate:String?) {
        selectedCustomer = customer
        customerId = selectedCustomer?.name+"_"+selectedCustomer?.phone+"_"+selectedCustomer?.no
        val sb = StringBuilder()
        sb.append(Config.currentUser?.displayName).append(" \n")
        sb.append(msg)
        if(!suits.isNullOrBlank())sb.append("\n${getString(R.string.totalsuits)} $suits")
        if(!amountRemaining.isNullOrBlank())sb.append("\n${getString(R.string.remainingAmount)} $amountRemaining")
        if(!returnDate.isNullOrBlank())sb.append("\n${getString(R.string.wapsi)} $returnDate")
        CustomDialogue.instance?.showSmsDialog(sb.toString() , selectedCustomer!!.phone)

    }
    private fun onDelivered(deliver: Customer) {

        selectedCustomer = deliver
        customerId = selectedCustomer?.name+"_"+selectedCustomer?.phone+"_"+selectedCustomer?.no
        selectedCustomer!!.order?.deliveryDate =""
        selectedCustomer!!.order?.suits =""
        selectedCustomer!!.order?.amountRcvd = (Utils.getAmount(selectedCustomer!!.order!!.amountRemaining)+Utils.getAmount(
            selectedCustomer!!.order!!.amountRcvd)).toString()
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
        binding.customerDataI.shirtLayoutI.kuffVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.shoulderWVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.shoulderVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.colarVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.chestVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.waistVal.isEnabled = enable
        binding.customerDataI.shirtLayoutI.hipVal.isEnabled = enable
        binding.customerDataI.trouserLayoutI.lengthSVal.isEnabled = enable
        binding.customerDataI.trouserLayoutI.panchaVal.isEnabled = enable
        binding.customerDataI.trouserLayoutI.tpocketCheckbox.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.embVal.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.suitsVal.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.spocketCheckbox.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.fpocketCheckbox.isEnabled = enable
        binding.customerDataI.extrainfoLayoutI.notesVal.isEnabled = enable
        binding.customerDataI.editPencil.isSelected = enable
        if(enable)
            binding.customerDataI.extrainfoLayoutI.save.visibility = View.VISIBLE
        else
        {
            binding.customerDataI.extrainfoLayoutI.save.visibility = View.GONE
            binding.keyboard.visibility =View.GONE
        }


    }

    private fun showCustomerInfo(customer: Customer)
    {
        binding.customerDataI.nameUserVal.setText(customer.name)
        binding.customerDataI.phoneUserVal.setText(customer.phone)

        binding.customerDataI.noVal.text = customer.no.toString()
        var naapQameez: NaapQameez = customer.naapQameez!!
        binding.customerDataI.shirtLayoutI.lengthQVal.setText(naapQameez.shirtLength)
        binding.customerDataI.shirtLayoutI.lengthBVal.setText(naapQameez.armLength)
        binding.customerDataI.shirtLayoutI.kuffVal.setText(naapQameez.armLength)
        binding.customerDataI.shirtLayoutI.shoulderWVal.setText(naapQameez.shoulderLength)
        binding.customerDataI.shirtLayoutI.shoulderVal.setText(naapQameez.shoulder)
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
        binding.customerDataI.extrainfoLayoutI.suitsVal.setText(order.suits)

        var extraInfo: ExtraInfo = customer.extraInfo!!
        binding.customerDataI.extrainfoLayoutI.embVal.setText(extraInfo.karhaiNo)
        binding.customerDataI.extrainfoLayoutI.fpocketCheckbox.isChecked =extraInfo.pocketFront
        binding.customerDataI.extrainfoLayoutI.spocketCheckbox.isChecked = extraInfo.pocketSide
        binding.customerDataI.extrainfoLayoutI.notesVal.setText(extraInfo.notes)
        binding.scrollviewOrders.fullScroll(ScrollView.FOCUS_UP)
    }
    private fun updateCustomer()
    {
        binding.customerDataI.phoneUserVal.error = null
        if(!selectedCustomer!!.phone.isNullOrBlank())
        {
            if(!Pattern.compile(Config.phonePat).matcher(selectedCustomer!!.phone.trim()).matches())
            {
                binding.customerDataI.phoneUserVal.error = getString(R.string.phone_msg)

                CustomAlertDialogue.instance?.showAlertDialog(requireActivity(),
                    getString(R.string.error),
                    getString(R.string.phone_msg),
                    "OK",
                    null,
                    null)

                return
            }
        }

        docRef!!.update(Config.Customers, customersList)
            .addOnCompleteListener(requireActivity()) { task1 ->
                if (task1.isSuccessful) {
                    Config.appToast( getString(R.string.updated))
                    selectedCustomer = null
                    binding.customerDataI.backButton.callOnClick()
                } else Config.appToast( "failed to update")
            }.addOnFailureListener(requireActivity()){ task ->
            Config.appToast(
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
            naapQameez.kuflink = binding.customerDataI.shirtLayoutI.kuffVal.text.toString()
            naapQameez.shoulderLength = binding.customerDataI.shirtLayoutI.shoulderWVal.text.toString()
            naapQameez.shoulder = binding.customerDataI.shirtLayoutI.shoulderVal.text.toString()
            naapQameez.colarSize = binding.customerDataI.shirtLayoutI.colarVal.text.toString()
            naapQameez.chest = binding.customerDataI.shirtLayoutI.chestVal.text.toString()
            naapQameez.waist = binding.customerDataI.shirtLayoutI.waistVal.text.toString()
            naapQameez.ghera = binding.customerDataI.shirtLayoutI.hipVal.text.toString()
            var naapShalwar: NaapShalwar = selectedCustomer!!.naapShalwar!!
            naapShalwar.shalwarLength = binding.customerDataI.trouserLayoutI.lengthSVal.text.toString()
            naapShalwar.pancha = binding.customerDataI.trouserLayoutI.panchaVal.text.toString()
            naapShalwar.pocket = binding.customerDataI.trouserLayoutI.tpocketCheckbox.isChecked

            var order: Order = selectedCustomer!!.order!!
            order.amountRcvd = binding.customerDataI.wasoolVal.text.toString()
            order.amountRemaining = binding.customerDataI.remVal.text.toString()
            order.deliveryDate = binding.customerDataI.extrainfoLayoutI.wapsiVal.text.toString()
            order.suits = binding.customerDataI.extrainfoLayoutI.suitsVal.text.toString()

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
            Config.appToast( ex.message)
        }

    }
    var editor = OnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            binding.keyboard.visibility = View.GONE
            Utils.hideKeyboard(requireActivity())
            true
        } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
            Utils.hideKeyboard(requireActivity())
            true
        }
        false
    }
    var touchListener = OnTouchListener { v, event ->
        showKeyBoard((v as TextInputEditText))
        true
    }
    var focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            Utils.hideKeyboard(requireActivity())
            binding.keyboard.visibility = View.GONE
        } else {

            showKeyBoard((v as TextInputEditText))
        }
    }
    private val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.edit_pencil -> {
                enableDisable(!binding.customerDataI.editPencil.isSelected)
            }
            R.id.delete -> {
                if (selectedCustomer != null) {
                    docRef!!.update(Config.Customers, FieldValue.arrayRemove(selectedCustomer))
                        .addOnCompleteListener(
                            requireActivity()
                        ) { task1 ->
                            if (task1.isSuccessful) {
                                Config.appToast( "Data Removed")
                                selectedCustomer = null
                                binding.customerDataI.backButton.callOnClick()
                            } else Config.appToast( "failed to remove")
                        }.addOnFailureListener(requireActivity()) { task ->
                        Config.appToast(
                            task.message
                        )
                    }

                }

            }
            R.id.shirt_naap -> {
                if (binding.customerDataI.shirtLayoutI.root.isVisible) binding.customerDataI.shirtLayoutI.root.visibility =
                    View.GONE
                else binding.customerDataI.shirtLayoutI.root.visibility = View.VISIBLE
            }
            R.id.trouser_naap -> {
                if (binding.customerDataI.trouserLayoutI.root.isVisible) binding.customerDataI.trouserLayoutI.root.visibility =
                    View.GONE
                else binding.customerDataI.trouserLayoutI.root.visibility = View.VISIBLE
            }
            R.id.back_button -> {
                if(binding.keyboard.visibility == View.VISIBLE) binding.keyboard.visibility =View.GONE
                else
                {
                    binding.ordersDataShow.visibility = View.VISIBLE
                    binding.scrollviewOrders.visibility = View.GONE
                }

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
                        binding.customerDataI.extrainfoLayoutI.wapsiVal.text =
                            Config.getFormatedStringDate(
                                date_Cal.time
                            )
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
            R.id.length_q_Val, R.id.length_b_Val,  R.id.shoulder_w_Val,R.id.shoulder_Val, R.id.colar_Val,R.id.suits_Val, R.id.kuff_val,
            R.id.chest_Val, R.id.waist_Val, R.id.hip_Val, R.id.length_s_Val, R.id.pancha_Val, R.id.emb_Val -> {
                showKeyBoard(view as TextInputEditText)
            }
        }
    }
    private fun showKeyBoard(field: TextInputEditText) {
        if (binding.keyboard.visibility !== View.VISIBLE) {
            binding.keyboard.setFiled(field,  0, this)
            binding.keyboard.visibility = View.VISIBLE
        } else if (binding.keyboard.getInputField()?.id !== field.id) {
            binding.keyboard.changeField()
            binding.keyboard.setFiled(field, 0, this)
        }
    }
    override fun onNumPadCommandKeyEvent(command: String?, vararg args: Any?) {
        if (command.equals("done", ignoreCase = true) || command.equals("dismiss", ignoreCase = true)) binding.keyboard.visibility =View.GONE

    }
    override fun doBack(): Boolean {
        if(binding.scrollviewOrders.isVisible)
        {
            binding.customerDataI.backButton.callOnClick()
            return false
        }
        else return true
    }


}