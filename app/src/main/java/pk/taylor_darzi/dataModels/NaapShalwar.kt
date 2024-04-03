package pk.taylor_darzi.dataModels
import androidx.annotation.Keep
import kotlinx.serialization.Serializable
@Keep
@Serializable
data class NaapShalwar(var shalwarLength: String?="", var shalwarGhera: String?="",var pancha: String?="", var pocket: Boolean= true)
