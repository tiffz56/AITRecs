package hu.ait.aitrecs

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.ait.aitrecs.data.Rec
import hu.ait.aitrecs.databinding.ActivityMapsBinding
import hu.ait.aitrecs.viewmodel.RecsViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    MyLocationManager.OnNewLocationAvailable {

    companion object {
        const val KEY_CURRENT_LOC = "KEY_CURRENT_LOC"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var myLocationManager: MyLocationManager
    private lateinit var currentLoc: LatLng
    lateinit var recsViewModel: RecsViewModel
    private lateinit var allRecs: ArrayList<Rec>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allRecs = ArrayList()

        recsViewModel = ViewModelProvider(this)[RecsViewModel::class.java]

        myLocationManager = MyLocationManager(
            this,
            this
        )

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnAddRec.setOnClickListener {
            var myIntent = Intent(this, AddRecActivity::class.java)
            myIntent.putExtra(KEY_CURRENT_LOC, currentLoc)
            startActivity(myIntent)
        }

        binding.btnFindRec.setOnClickListener {
            var myIntent = Intent(this, FindRecActivity::class.java)
            myIntent.putExtra(KEY_CURRENT_LOC, currentLoc)
            startActivity(myIntent)
        }

        binding.btnReset.setOnClickListener {
            recsViewModel.queryRecs()
        }

        requestNeededPermission()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNeededPermission()
        }

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        // add all recs from db onto map
        recsViewModel.recs.observe(
            this, Observer { recs ->
                recs.forEach { rec ->
                    allRecs.add(rec)
                    updateMap(rec)
                }
            }
        )

        //recsViewModel.queryRecs()
    }

    override fun onResume() {
        super.onResume()

        val isMapFiltered = intent.getBooleanExtra(FindRecActivity.FILTER_ON, false)
        if (isMapFiltered) {
            filterMap()
            recsViewModel.queryRecsWithFilters()
        } else {
            recsViewModel.queryRecs()
        }

    }

    private fun filterMap() {
        recsViewModel.meOnly = intent.getBooleanExtra(FindRecActivity.FILTER_ME_ONLY, false)
        recsViewModel.distanceFromMe = intent.getLongExtra(FindRecActivity.FILTER_DISTANCE, 0)
        recsViewModel.recCategory = intent.getIntExtra(FindRecActivity.FILTER_CATEGORY, -1)
    }

    override fun onDestroy() {
        super.onDestroy()
        recsViewModel.snapshotListener?.remove()
    }

    private fun updateMap(rec: Rec) {
        val marker = LatLng(rec.lat, rec.lng)
        mMap.addMarker(MarkerOptions()
            .position(marker)
            .title(rec.locName)
            .snippet(rec.description)
        )
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        } else {
            // we have the permission
            myLocationManager.startLocationMonitoring()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "ACCESS_FINE_LOCATION perm granted", Toast.LENGTH_SHORT
                    )
                        .show()

                    myLocationManager.startLocationMonitoring()
                } else {
                    Toast.makeText(
                        this,
                        "ACCESS_FINE_LOCATION perm NOT granted", Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    override fun onNewLocation(location: Location) {
        currentLoc = LatLng(location.latitude, location.longitude)
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 10.0F))
    }
}