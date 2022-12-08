package hu.ait.aitrecs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.aitrecs.MapsActivity.Companion.KEY_CURRENT_LOC
import hu.ait.aitrecs.databinding.ActivityAddRecBinding
import hu.ait.aitrecs.data.Rec

class AddRecActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val COLLECTION_RECS = "recs"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAddRecBinding
    private lateinit var coords: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddRecBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spCategory.adapter = categoryAdapter

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        coords = intent.getParcelableExtra(KEY_CURRENT_LOC)!!

        binding.btnAdd.setOnClickListener {
            uploadRec()
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.addMarker(
            MarkerOptions().position(coords)
        )?.isDraggable = true

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        mMap.setOnMarkerDragListener(
            object : GoogleMap.OnMarkerDragListener
            {
                override fun onMarkerDrag(p0: Marker) {
                }

                override fun onMarkerDragEnd(p0: Marker) {
                    coords = p0.position
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 10.0F))
                }

                override fun onMarkerDragStart(p0: Marker) {
                }

            }
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 10.0F))
    }

    private fun uploadRec() {
        val myRec = Rec(
            FirebaseAuth.getInstance().currentUser!!.uid,
            FirebaseAuth.getInstance().currentUser!!.email!!,
            binding.etLocName.text.toString(),
            coords.latitude,
            coords.longitude,
            binding.etDescription.text.toString(),
            binding.spCategory.selectedItemPosition
        )

        val recsCollection = FirebaseFirestore.getInstance()
            .collection(COLLECTION_RECS)

        recsCollection.add(myRec)
            .addOnSuccessListener {
                Toast.makeText(this,
                    "Rec saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this,
                    "Error: ${it.message}",
                Toast.LENGTH_SHORT).show()
            }
    }

}