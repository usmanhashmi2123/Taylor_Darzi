package pk.taylor_darzi.customViews

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import pk.taylor_darzi.databinding.DialogLayoutBinding
import pk.taylor_darzi.databinding.ImageDialogueBinding
import pk.taylor_darzi.utils.Config
import pk.taylor_darzi.utils.Preferences
import pk.taylor_darzi.utils.Utils
import java.net.URLEncoder
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


                            var no = binding.smsNo.text.toString().trim()

                            if(Preferences.instance!!.isSmsApp)
                            {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    type = "text/plain"
                                    data = Uri.parse("smsto:" + no)  // This ensures only SMS apps respond
                                    putExtra("sms_body", message)
                                }
                                if (intent.resolveActivity(Utils.curentActivity!!.packageManager) != null) {
                                    Utils.curentActivity!!.startActivity(intent)
                                }
                            }
                        else
                            {
                                try {
                                    Utils.curentActivity!!.applicationContext!!.packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                                    val intent = Intent(Intent.ACTION_VIEW )
                                    val url = "https://api.whatsapp.com/send?phone=" + no + "&text=" + URLEncoder.encode(message, "UTF-8")
                                    intent.setPackage("com.whatsapp")
                                    intent.data = Uri.parse(url)

                                    Utils.curentActivity!!.startActivity(intent)

                                } catch (e: PackageManager.NameNotFoundException) {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        type = "text/plain"
                                        data = Uri.parse("smsto:" + no)  // This ensures only SMS apps respond
                                        putExtra("sms_body", message)
                                    }
                                    if (intent.resolveActivity(Utils.curentActivity!!.packageManager) != null) {
                                        Utils.curentActivity!!.startActivity(intent)
                                    }
                                }
                            }



                            dismissDialog()

                    }catch (ex: Exception)
                    {
                        ex.printStackTrace()
                        Config.appToast(ex.message
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

    fun ImageCaptureDialog( dismissListener: ImageCaptureListener) {
        dismissDialog()
        dialog = Dialog(Utils.mContext!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawableResource(pk.taylor_darzi.R.color.white_black)
        val binding = ImageDialogueBinding.inflate(LayoutInflater.from(Utils.mContext!!))

        dialog!!.setContentView(binding.root)
        dialog!!.setCanceledOnTouchOutside(true)

        binding.mCameraTextView.setOnClickListener {
            dismissDialog()
            dismissListener.onDismissed("Camera")
        }
        binding.mGalleryTextView.setOnClickListener { v: View? ->
            dismissDialog()
            dismissListener.onDismissed("Gallery")      }

        binding.mCancelTextView.setOnClickListener { v: View? -> dismissDialog() }

        dialog!!.setOnDismissListener { dialogInterface: DialogInterface? -> dismissDialog() }

        dialog!!.show()
    }
    interface ImageCaptureListener {
        fun onDismissed(option: String)
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