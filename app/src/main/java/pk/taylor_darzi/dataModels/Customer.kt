package pk.taylor_darzi.dataModels
import androidx.annotation.Keep
import kotlinx.serialization.Serializable
@Keep
@Serializable
data class Customer(var name:String?="", var phone: String="", var no:Int =0, var order:Order?=Order(), var naapQameez: NaapQameez?=NaapQameez(), var naapShalwar: NaapShalwar?=NaapShalwar(), var extraInfo:ExtraInfo?=ExtraInfo(), var imageUri:String?="")
