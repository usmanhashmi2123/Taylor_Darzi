package pk.taylor_darzi.dataModels

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import pk.taylor_darzi.utils.Config
import java.util.*

@Keep
@Serializable
data class Order(var bookingDate: String?=Config.getFormatedStringDate(Calendar.getInstance().time), var deliveryDate:String?="", var amountRcvd:String?="", var  amountRemaining:String?="",var  suits :String?="")
