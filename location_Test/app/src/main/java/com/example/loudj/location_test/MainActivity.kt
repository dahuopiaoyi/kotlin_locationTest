package com.example.loudj.location_test

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var locationManager: LocationManager
    val locationListener = object : LocationListener {
//        val textView: TextView = find(R.id.text)
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        override fun onProviderDisabled(provider: String?) {
            toast("关闭了GPS")
            textView.text = "关闭了GPS"
        }

        override fun onProviderEnabled(provider: String?) {
            toast("打开了GPS")
            showLocation(textView, locationManager)
        }

        override fun onLocationChanged(location: Location?) {
            toast("变化了")
            showLocation(textView, locationManager)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
    private val LOCATION_PERMISSION = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = find(R.id.text)
//        val textView: TextView = find(R.id.text)

        //检查是否获得了位置权限，如果没有就申请位置权限，如果有权限就刷新位置
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION)
            }
            else {
                showLocation(textView, locationManager)
            }
        }
        else {
            showLocation(textView, locationManager)
        }
    }

    override fun onPause() {
//        val locationMabager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        super.onPause()
        val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if ((locationManager != null) && ((hasLocationPermission == PackageManager.PERMISSION_GRANTED))) {
            locationManager.removeUpdates(locationListener)
        }
    }

    override fun onResume() {
//        val textView: TextView = find(R.id.text)
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //挂上LocationListener, 在状态变化时刷新位置显示
        super.onResume()
        val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if ((locationManager != null) && ((hasLocationPermission == PackageManager.PERMISSION_GRANTED))) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0F, locationListener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,0F, locationListener)
            showLocation(textView, locationManager)
        }
    }

    //申请下位置权限后，要刷新位置信息
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        val textView: TextView = find(R.id.text)
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toast("获取了位置权限")
                showLocation(textView, locationManager)
            }
        }
    }

    fun showLocation(textView: TextView, locationManager: LocationManager) {
        textView.text = getLocation(locationManager).toString()
    }

    fun getLocation(locationManager: LocationManager): Location? {
        var location: Location? = null
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            toast("没有位置权限")
        }
        else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toast("没有打开GPS")
        }
        else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                toast("位置信息为空")
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location == null) {
                    toast("网络位置信息也为空")
                }
                else {
                    toast("当前使用网络位置")
                }
            }
        }
        return location
    }
}
