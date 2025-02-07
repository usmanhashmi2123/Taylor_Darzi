package pk.taylor_darzi.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pk.taylor_darzi.R

object AppPermissions {
    const val REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110
    const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    var currentApiVersion = Build.VERSION.SDK_INT
    private val perms = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    )
    private val permsQ = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION,
        Manifest.permission.CAMERA
    )
    private var alertDialog: AlertDialog? = null
    fun checkPermissions(mActivity: Context?): Boolean {
        if (mActivity == null) return false
        var hasPerm = false
        for (perm in if(currentApiVersion >28)permsQ else perms) {
            hasPerm = ContextCompat.checkSelfPermission(
                mActivity,
                perm
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPerm) {
                return hasPerm
            }
        }
        return hasPerm
    }

    fun checkSpecificPermissions(mActivity: Context?, permission: String?): Boolean {
        return if (mActivity == null) false else ContextCompat.checkSelfPermission(
            mActivity,
            permission!!
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(
        mActivity: Activity, forPermissionsResult: ActivityResultLauncher<Array<String>>
    ) {
        var permissionNeeded = ""
        var showRequestPermissionRationale = false
        for (perm in if(currentApiVersion >28)permsQ else perms) {
            showRequestPermissionRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(mActivity, perm)
            if (showRequestPermissionRationale) {
                permissionNeeded =
                    if (permissionNeeded.isEmpty()) perm else "$permissionNeeded,$perm"
            }
        }
        if (!permissionNeeded.isEmpty()) {
            val missingPerms = permissionNeeded.split(",").toTypedArray()
            if (showRequestPermissionRationale) {
                val messsage = mActivity.getString(R.string.permission_rationale)
                showDialogue(mActivity, missingPerms, "OK", messsage, forPermissionsResult)
            } else forPermissionsResult.launch(missingPerms)
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            forPermissionsResult.launch(if (currentApiVersion > 28) permsQ else perms)
        }
    }

    fun showDialogue(
        activity: Activity,
        missingPerms: Array<String>?,
        button: String?,
        message: String,
        forPermissionsResult: ActivityResultLauncher<Array<String>>
    ) {
        if (alertDialog != null) destroyDialogue()
        val builder = AlertDialog.Builder(activity)

        // Setting Dialog Title
        builder.setTitle(activity.getString(R.string.permission_rationale))
        builder.setCancelable(false)
        val foregroundColorSpan =
            ForegroundColorSpan(activity.resources.getColor(R.color.bluish))

        // Initialize a new spannable string builder instance
        val ssBuilder = SpannableStringBuilder(message)

        // Apply the text color span
        ssBuilder.setSpan(
            foregroundColorSpan,
            0,
            message.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Setting Dialog Message
        builder.setMessage(ssBuilder)

        // On pressing Settings button
        builder.setPositiveButton(button) { dialog, which ->
            destroyDialogue()
            if (missingPerms != null)  forPermissionsResult.launch(missingPerms)
           else {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity.startActivity(intent)
            }
        }
        alertDialog = builder.create()
        alertDialog!!.show()
    }

    fun destroyDialogue() {
        if (alertDialog != null) {
            alertDialog!!.dismiss()
            alertDialog = null
        }
    }
}