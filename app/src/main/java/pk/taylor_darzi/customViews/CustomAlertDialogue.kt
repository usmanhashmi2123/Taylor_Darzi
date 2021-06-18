package pk.taylor_darzi.customViews

import android.content.Context
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import pk.taylor_darzi.interfaces.GenericEvents
import pk.taylor_darzi.utils.Constants

class CustomAlertDialogue {
    private var dialog: AlertDialog? = null
    fun showAlertDialog(
        context: Context?, title: String, message: String, okButtonString: String,
        cancelButtonString: String?, genericEventListener: GenericEvents) {
        try {
            val builder = AlertDialog.Builder(
                context!!
            )
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setCancelable(true)
            if (!TextUtils.isEmpty(okButtonString)) {
                builder.setPositiveButton(okButtonString) { dialog, which ->
                    genericEventListener.onGenericEvent(Constants.ON_OK.toString())
                    destroy()
                }
            }
            if (!TextUtils.isEmpty(cancelButtonString)) {
                builder.setNegativeButton(cancelButtonString) { dialog, which -> destroy() }
            }
            dialog = builder.create()
            dialog!!.setOnCancelListener { destroy() }
            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun destroy() {
        if (dialog != null) {
            dialog!!.cancel()
            dialog = null
        }
    }

    companion object {
        var alertDialogue: CustomAlertDialogue? = null
        val instance: CustomAlertDialogue?
            get() {
                if (alertDialogue == null) alertDialogue = CustomAlertDialogue()
                return alertDialogue
            }
    }
}