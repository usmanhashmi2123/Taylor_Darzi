package pk.taylor_darzi.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pk.taylor_darzi.dataModels.Customer
import pk.taylor_darzi.databinding.OrderItemBinding

class OrdersRecyclerViewAdapter(val onClick: (Customer) -> Unit, val onDelivered: (Customer) -> Unit, val sendSms: (Customer) -> Unit) : RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder?>() {
    private var dataList: ArrayList<Customer>? = ArrayList<Customer>()
    private var dataListFiltered: ArrayList<Customer>? = null

    fun setData(arrayList: ArrayList<Customer>?) {
        dataList = arrayList
        dataListFiltered = arrayList
    }

    fun getItem(position: Int): Customer? {
        return if (dataListFiltered != null && position < dataListFiltered!!.size) dataListFiltered!![position] else null
    }

    val listLoaded: List<Customer>? get() = dataListFiltered

    class ViewHolder(private val itemBinding: OrderItemBinding, val onClick: (Customer) -> Unit, val onDelivered: (Customer) -> Unit, val sendSms: (Customer) -> Unit) : RecyclerView.ViewHolder(
        itemBinding.root
    ){

        fun bindItems(user: Customer, pos :Int) {
            itemBinding.nameUser.text = user.name
            itemBinding.phoneUser.text = user.phone
            itemBinding.wasooliVal.text = user.order?.amountRcvd
            itemBinding.remaVal.text = user.order?.amountRemaining
            itemBinding.khataVal.text = user.no.toString()
            itemBinding.date.text = user.order?.deliveryDate
            user.imageUri?.let { it->
                Glide.with(itemBinding.customerPic.context)
                    .load(Uri.parse(it))
                    .into(itemBinding.customerPic)
            }
            itemBinding.deliver.setOnClickListener { user.let { onDelivered(it)} }
            itemBinding.sendSms.setOnClickListener { user.let { sendSms(it)} }
            itemBinding.root.setOnClickListener { user.let { onClick(it)} }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = OrderItemBinding.inflate( LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v, onClick, onDelivered, sendSms)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(itemCount > 0)
            holder.bindItems(dataListFiltered!!.get(position), position)
    }

    override fun getItemCount(): Int {
        if (dataListFiltered == null) return 0 else return dataListFiltered!!.size
    }
    fun filter(querry: String)
    {
        val filteredList: ArrayList<Customer> = ArrayList<Customer>()
        if(dataList!= null)
        {
            if(querry.isNullOrBlank()) filteredList.addAll(dataList!!)
            else
            {
                for (customer in dataList!!)
                {
                    if(customer.name!!.toLowerCase().contains(querry.toLowerCase()) || customer.phone.toLowerCase()
                            .contains(
                            querry.toLowerCase()
                        ))
                        filteredList.add(customer)

                }
            }

        }
        dataListFiltered = filteredList
        notifyDataSetChanged()
    }

    fun clearDataAdapter() {
        if(dataList!= null) dataList!!.clear()
        if(dataListFiltered!= null) dataListFiltered!!.clear()

    }


}