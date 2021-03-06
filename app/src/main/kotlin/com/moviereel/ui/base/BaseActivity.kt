package com.moviereel.ui.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.moviereel.R
import com.moviereel.app.MovieReelApp
import com.moviereel.di.components.ActivityComponent
import com.moviereel.di.components.DaggerActivityComponent
import com.moviereel.di.modules.ActivityModule
import com.moviereel.receivers.ConnChangeReceiver
import com.moviereel.utils.CommonUtils
import com.moviereel.utils.NetworkUtils

/**
 * @author lusinabrian on 10/06/17.
 * *
 * @Notes
 */

abstract class BaseActivity : AppCompatActivity(), BaseView, BaseFragment.Callback {

    // fields
    private var mSweetAlertDialog: SweetAlertDialog? = null
    private val mProgressDialog: ProgressDialog? = null
    var activityComponent: ActivityComponent? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .appComponent((application as MovieReelApp).component)
                .build()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * Checks if there is network connected
     * Returns True if the device is connected to a network, false otherwise

     * @return [Boolean]
     */
    override val isNetworkConnected: Boolean
        get() = NetworkUtils.isNetworkAvailable(applicationContext)

    /**
     * before destroying the view, do a sanity check of the view bindings before destroying the
     * activity  */
    public override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * Abstract method that will be implemented by child activity classes
     * used to setup the views in the activity  */
    protected abstract fun setUp()

    /**
     * Hides keyboard
     */
    override fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Shows a snackbar if an error has been encountered

     * @param message the message to display in the snackbar
     * * *
     * *
     * @param length  how long the snack bar should be displayed
     */
    override fun onErrorSnackBar(message: String, length: Int) {
        if (message != null) {
            showSnackbar(message, length)
        } else {
            showSnackbar(getString(R.string.snackbar_api_error), length)
        }
    }

    /**
     * Override of [.onErrorSnackBar]
     * @param resId
     * *
     * @param length
     */
    override fun onErrorSnackBar(@StringRes resId: Int, length: Int) {
        onErrorSnackBar(getString(resId), length)
    }

    /**
     * Shows a [android.support.design.widget.Snackbar]
     * @param message the message to display in the snackbar
     * * *
     * *
     * @param length how long to display this snackbar
     */
    private fun showSnackbar(message: String, length: Int) {
        @SuppressLint("WrongViewCast") val snackbar = Snackbar.make(findViewById<View>(R.id.frame_container), message, length)
        val view = snackbar.view
        val textView = view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    override fun showNetworkErrorSnackbar(@StringRes message: Int, length: Int) {
        showNetworkErrorSnackbar(getString(message), length)
    }

    override fun showNetworkErrorSnackbar(message: String, length: Int) {
        @SuppressLint("WrongViewCast") val snackbar = Snackbar.make(findViewById<View>(R.id.frame_container), message, length)
        snackbar.setAction(R.string.reconnect_snackbar_action) {
            registerReceiver(
                    ConnChangeReceiver(),
                    IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            )
            startActivity(Intent(WifiManager.ACTION_PICK_WIFI_NETWORK))
        }

        val sbView = snackbar.view
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    /**
     * Callback for when a fragment is attached to an activity
     */
    override fun onFragmentAttached() {

    }

    /**
     * Callback for when a fragment is detached from an activity

     * @param tag the fragment tag to detach
     */
    override fun onFragmentDetached(tag: String) {

    }
}
