package com.enesproje.firebaseauth

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.HashMap

class ForceUpdateChecker (val activity : Activity , val fragment: Fragment){

    fun check(){

        val remoteConfig = Firebase.remoteConfig

        if(remoteConfig.getBoolean("force_update_required")){

            val currentVersion = remoteConfig.getString("force_update_current_version")
            val appVersion = getAppVersion(activity)

            Log.e("VERSION","Current : $currentVersion, App : $appVersion")
            val updateUrl = remoteConfig.getString(("force_update_store_url"))

            if(currentVersion != appVersion){

                fragment.findNavController().navigate(LoginScreenDirections.actionLoginScreenToForceUpdate(updateUrl))

            }


        }

    }

    fun checkAsynchronous(interval : Long) {

        val firebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings{
            minimumFetchIntervalInSeconds = interval
        }
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        val remoteConfigDefaults = HashMap<String?,Any?>()
        remoteConfigDefaults["force_update_required"] = false
        remoteConfigDefaults["force_update_current_version"] = getAppVersion(activity)
        remoteConfigDefaults["force_update_store_url"] = "https://play.google.com/store/apps/details?id=com.enrotek.apronyemek&hl=tr"

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults)

        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(object: OnCompleteListener<Boolean> {
                    override fun onComplete(p0: Task<Boolean>) {

                        if (fragment.findNavController().currentDestination!!.label != "fragment_force_update") {

                            check()

                            Log.e("ForceUpdateChecker","${fragment.findNavController().currentDestination!!.label}")

                        }

                    }
                })

    }

    private fun getAppVersion(context: Context): String {

        var result = ""
        try{
            result = context.packageManager
                    .getPackageInfo(context.packageName,0).versionName

            result = result.replace("[a-zA-Z]|-".toRegex(), "")
        }catch(e: PackageManager.NameNotFoundException){

            Log.e("Force Update","Exception aldın. ForceUpdateChecker -> getAppVersion")

        }

        return result
    }

    class Builder(val activity : Activity, val fragment: Fragment){

        fun build() : ForceUpdateChecker{

            return ForceUpdateChecker(activity,fragment)

        }

        fun check() : ForceUpdateChecker{

            val forceUpdateChecker = build()
            forceUpdateChecker.check()
            return forceUpdateChecker

        }

        fun checkAsynchronous(interval : Long) : ForceUpdateChecker {

            val forceUpdateChecker = build()
            forceUpdateChecker.checkAsynchronous(interval)
            return forceUpdateChecker

        }

    }

    fun onUpdateNeeded(updateUrl: String) {
        val dialog = AlertDialog.Builder(activity)
                .setTitle("Yeni versiyon mevcut")
                .setMessage("Daha iyi bir kullanıcı deneyimi için uygulamayı güncelleyiniz.")
                .setCancelable(false)
                .setPositiveButton("Güncelle",object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        directToMarket(updateUrl)
                    }
                })
                .setNegativeButton("Reddet",object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        activity.finish()
                    }
                }).create()

        dialog.show()

    }

    fun directToMarket(updateUrl: String) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)

    }

    companion object{

        fun with(activity: Activity , fragment: Fragment) : Builder{
            return Builder(activity,fragment)
        }

    }


}