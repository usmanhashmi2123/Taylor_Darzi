package pk.taylor_darzi.customViews

import android.Manifest
import android.app.Dialog
import android.telephony.SmsManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import pk.taylor_darzi.databinding.DialogLayoutBinding
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Utils
import java.util.regex.Pattern


class CustomDialogue {
    private var dialog: Dialog? = null

    fun showSmsDialog(message: String, phone: String) {
        dismissDialog()
        dialog = Dialog(Utils.mContext!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawableResource(pk.taylor_darzi.R.color.white_black)
        val binding = DialogLayoutBinding.inflate(LayoutInflater.from(Utils.mContext!!))

        dialog!!.setContentView(binding.root)
        dialog!!.setCanceledOnTouchOutside(true)

        dialog!!.setCancelable(true)
        val window = dialog!!.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        wlp.dimAmount = 0.5f
        window.attributes = wlp
        binding.smsNo.setText(phone)
        binding.sms.setText(message)
        binding.send.setOnClickListener {
            if(!binding.smsNo.text.isNullOrBlank() && Pattern.compile(Config.phonePat).matcher(
                    binding.smsNo.text.toString().trim()).matches())
            {
                binding.smsNo.error = null
                binding.sms.error = null
                if(!binding.sms.text.isNullOrBlank())
                {
                    try {

                        val missingPerms = arrayOf(Manifest.permission.SEND_SMS/*, Manifest.permission.READ_PHONE_STATE*/)
                        if(checkSelfPermission(Utils.curentActivity!!, Manifest.permission.SEND_SMS) == PERMISSION_GRANTED)
                        {
                            var no = binding.smsNo.text.toString().trim()

                            val smsManager = SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())
                            val parts = smsManager.divideMessage(binding.sms.text.toString())
                            smsManager.sendMultipartTextMessage(
                                no,
                                null,
                                parts,
                                null,
                                null
                            )
                            dismissDialog()
                        }
                        else requestPermissions(
                            Utils.curentActivity!!,
                            missingPerms,
                            30
                        )
                    }catch (ex: Exception)
                    {
                        ex.printStackTrace()
                        Config.appToast(
                            Utils.curentActivity!!,
                            ex.message
                        )
                    }


                }
                else binding.sms.error=Utils.mContext!!.getString(pk.taylor_darzi.R.string.invalid_data)
            }
            else binding.smsNo.error=Utils.mContext!!.getString(pk.taylor_darzi.R.string.phone_msg)
        }
        dialog!!.setOnDismissListener { dismissDialog() }
        dialog!!.show()
    }

    fun dismissDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }


    companion object {
        var customdialog: CustomDialogue? = null
        val instance: CustomDialogue?
            get() {
                if (customdialog == null) customdialog = CustomDialogue()
                return customdialog
            }
    }
}