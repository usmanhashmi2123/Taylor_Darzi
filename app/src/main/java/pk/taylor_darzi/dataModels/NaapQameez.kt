package pk.taylor_darzi.dataModels

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NaapQameez(var shirtLength: String?=null, var armLength: String?=null, var shoulderLength: String?=null, var colarSize: String?=null, var chest: String?=null, var waist: String?=null, var ghera: String?=null)
