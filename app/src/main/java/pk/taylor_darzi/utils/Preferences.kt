package pk.taylor_darzi.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import pk.taylor_darzi.TaylorDarzi

class Preferences private constructor() {
    val PREF_USER_EMAIL = "pref_user_e"
    val PREF_USER_ID = "pref_user_id"
    val PREF_USER_PASS = "pref_user_pass"
    val PREF_LOGINED = "pref_user_login"
    val PREF_LANG = "pref_lang"
    val PREF_ALERTS = "pref_alerts"
    val PREF_TOKEN = "Pref_token"
    val PHOTO_URI = "pref_user_photo_local"
    private val PREFERENCE_FILE = "time_preferences"
    private val mPrefs: SharedPreferences
    private val mEdit: Editor

    val userEmailId: String
        get() = instance!!.mPrefs.getString(PREF_USER_EMAIL, "").toString()
    val userAuthId: String
        get() = instance!!.mPrefs.getString(PREF_USER_ID, "").toString()
    val userPass: String
        get() = instance!!.mPrefs.getString(PREF_USER_PASS, "").toString()
    val token: String?
        get() = instance!!.mPrefs.getString(PREF_TOKEN, "")
    val isAlertsOn: Boolean
        get() = instance!!.mPrefs.getBoolean(PREF_ALERTS, true)

    fun getStringPrefrence(key: String?): String? {
        return instance!!.mPrefs.getString(key, "")
    }

    val language: String?
        get() = instance!!.mPrefs.getString(PREF_LANG, "English")
    val isEnglish: Boolean
        get() = !instance!!.mPrefs.getString(PREF_LANG, "")!!.contains("Eng")

    fun getIntPrefrence(key: String?): Int {
        return instance!!.mPrefs.getInt(key, -1)
    }

    fun getBolleanPrefrence(key: String?): Boolean {
        return instance!!.mPrefs.getBoolean(key, false)
    }

    fun saveStringPrefValue(key: String?, value: String?) {
        mEdit.putString(key, value)
        save()
    }

    fun saveIntPrefValue(key: String?, value: Int) {
        mEdit.putInt(key, value)
        save()
    }

    fun saveBooleanPrefValue(key: String?, value: Boolean) {
        mEdit.putBoolean(key, value)
        save()
    }

    fun ClearPrefsValue() {
        val sp = instance!!.mPrefs
        val editor = sp.edit()
        editor.clear()
        editor.apply()
    }

    // Save content
    private fun save() {
        mEdit.apply()
    }

    companion object {
        private var INSTANCE: Preferences? = null

        // Returns singleton instance
        val instance: Preferences?
            get() {
                if (INSTANCE == null) INSTANCE = Preferences()
                return INSTANCE
            }
    }

    init {
        val app: Application = TaylorDarzi.instance!!
        mPrefs = app.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
        mEdit = mPrefs.edit()
    }
}