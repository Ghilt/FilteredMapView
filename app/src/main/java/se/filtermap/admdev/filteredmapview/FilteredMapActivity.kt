package se.filtermap.admdev.filteredmapview

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_filtered_map.*
import kotlinx.android.synthetic.main.settings_bottom_sheet_content.*
import se.filtermap.admdev.filteredmapview.extensions.log
import se.filtermap.admdev.filteredmapview.thread.CallbackLatch
import se.filtermap.admdev.filteredmapview.viewmodel.FilterMapViewModel
import se.filtermap.admdev.filteredmapview.model.CityMarker
import kotlin.random.Random

class FilteredMapActivity : AppCompatActivity(), OnMapReadyCallback {

    //TODO Utilize sheet callback or remove bottomSheetBehavior
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var mMap: GoogleMap
    private val mMarkerManager = CityMarkerManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtered_map)
        val mapAndDataLoaded = CallbackLatch(2) {
            val model = ViewModelProviders.of(this).get(FilterMapViewModel::class.java)
            onMapAndDataLoaded(mMap, model.getPopulations().value)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.filtered_map) as SupportMapFragment
        mapFragment.getMapAsync(mapAndDataLoaded.track(::onMapReady))

        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        bottom_sheet_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(v: SeekBar?, maxPopulation: Int, fromUser: Boolean) {
                log("progress $maxPopulation")
                val showAndHide = mMarkerManager.partition { city -> city.population < maxPopulation }
                showAndHide.first.forEach { it.marker.isVisible = true }
                showAndHide.second.forEach { it.marker.isVisible = false }
            }

            override fun onStartTrackingTouch(v: SeekBar?) {}

            override fun onStopTrackingTouch(v: SeekBar?) {}

        })

        val model = ViewModelProviders.of(this).get(FilterMapViewModel::class.java)
        model.getPopulations().observe(this, Observer<List<Int>>(mapAndDataLoaded.track(::onPopulationDataLoaded)))

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val laos = LatLng(36.1075, 120.46889)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(laos))
        log("Map ready")

    }

    private fun onPopulationDataLoaded(populations: List<Int>?) {
        log("Population data loaded ${populations?.size}")
    }

    private fun onMapAndDataLoaded(map: GoogleMap, populations: List<Int>?) {
        populations?.apply {
            for (cityPop in this) {
                val lat = Random.nextDouble(-30.0, 30.0)
                val lng = Random.nextDouble(0.0, 150.0)
                val city = MarkerOptions().position(LatLng(lat, lng)).title("city $lng")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                mMarkerManager.add(CityMarker(map.addMarker(city), cityPop))
            }
        }
    }
}
