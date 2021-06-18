package pk.taylor_darzi.dataModels

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
class CustomersList(var customers: List<Customer>? = ArrayList<Customer>(), var phoneNumber :String?="") {

    //var customers: Map<String, Customer>? = mapOf()
}