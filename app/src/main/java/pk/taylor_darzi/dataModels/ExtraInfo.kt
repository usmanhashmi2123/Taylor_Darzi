package pk.taylor_darzi.dataModels
import androidx.annotation.Keep
import kotlinx.serialization.Serializable
@Keep
@Serializable
data class ExtraInfo(var karhaiNo: String?="", var pocketFront: Boolean= false, var pocketSide: Boolean= true, var notes:String?="")
