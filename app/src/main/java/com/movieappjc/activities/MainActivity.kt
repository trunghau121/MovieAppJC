package com.movieappjc.activities

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.movieappjc.R
import com.movieappjc.fragmets.ScanDetailsFragment
import com.movieappjc.fragmets.ScanFragment
import org.opencv.android.OpenCVLoader
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    private var previousMenuItemBottomNav: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!OpenCVLoader.initDebug())
            Timber.tag("OpenCV").e("Unable to load OpenCV!")
        else
            Timber.tag("OpenCV").d("OpenCV loaded Successfully!")
        setup()
    }

    private fun setup(){
        setupUI()
        actionBarToggle()
        setUpToolbar()
        setupHome()
        listeners()
        askPermission()
        downloadInkRecognitionModel()
    }

    private fun downloadInkRecognitionModel() {
        val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US")
        if (modelIdentifier != null) {
            val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
            val remoteModelManager = RemoteModelManager.getInstance()
            remoteModelManager.download(model, DownloadConditions.Builder().build())
                .addOnSuccessListener {
                    println("✅ Model tải thành công!")
                }
                .addOnFailureListener { e ->
                    println("⚠️ Lỗi tải model: $e")
                }
        }
    }

    private fun askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
    }

    private fun listeners(){
        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItemBottomNav != null) {
                it.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItemBottomNav = it

            when(it.itemId){
                R.id.scan ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ScanFragment())
                        .commit()
                    supportActionBar?.title = getString(R.string.scan_cheque)
                    drawerLayout.closeDrawers()
                }

                R.id.exit ->{
                    finishAffinity()
                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun setupUI(){
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setupHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, ScanFragment())
            .commit()
        supportActionBar?.title = getString(R.string.home)
        navigationView.menu.findItem(R.id.scan).isChecked = true
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = getString(R.string.home)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun actionBarToggle() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        actionBarDrawerToggle.drawerArrowDrawable.color = ResourcesCompat.getColor(
            resources,
            R.color.text_color, null
        )
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawers()
            }
            else -> {
                when (supportFragmentManager.findFragmentById(R.id.frame)) {
                    is ScanDetailsFragment ->{
                        val sp = getSharedPreferences("saved", Context.MODE_PRIVATE)
                        val check = sp.getBoolean("saved", true)
                        if(check){
                            sp.edit().putBoolean("saved", false).apply()
                            setupHome()
                        }else{
                            MaterialAlertDialogBuilder(this)
                                .setTitle("Alert")
                                .setMessage("Exit without saving?")
                                .setPositiveButton("Yes"){_,_->
                                    setupHome()
                                }
                                .setNeutralButton("No"){_,_->}
                                .show()
                        }
                    }

                    is ScanFragment -> {
                        setupHome()
                    }
                    else -> super.onBackPressed()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            if(grantResults.isNotEmpty()&&grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "You have disabled camera functions.", Toast.LENGTH_LONG).show()
            }
        }
    }
}