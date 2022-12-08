package hu.ait.aitrecs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import hu.ait.aitrecs.databinding.ActivityFindRecBinding

class FindRecActivity : AppCompatActivity() {

    companion object {
        const val FILTER_ON = "filter_on"
        const val FILTER_ME_ONLY = "filter_me_only"
        const val FILTER_DISTANCE = "filter_distance"
        const val FILTER_CATEGORY = "filter_category"
    }

    lateinit var binding: ActivityFindRecBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindRecBinding.inflate(layoutInflater)
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

        binding.btnFindRec.setOnClickListener {
            var myIntent = Intent(this, MapsActivity::class.java)

            myIntent.putExtra(FILTER_ON, true)
            myIntent.putExtra(FILTER_ME_ONLY, binding.tbUsers.isChecked)
            myIntent.putExtra(FILTER_DISTANCE, binding.etDistance.text.toString().toLong())
            myIntent.putExtra(FILTER_CATEGORY, binding.spCategory.selectedItemPosition)

            startActivity(myIntent)
        }
    }
}