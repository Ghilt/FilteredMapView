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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_filtered_map.*
import kotlinx.android.synthetic.main.settings_bottom_sheet_content.*
import se.filtermap.admdev.filteredmapview.extensions.log
import se.filtermap.admdev.filteredmapview.model.City
import se.filtermap.admdev.filteredmapview.model.CityMarker
import se.filtermap.admdev.filteredmapview.thread.CallbackLatch
import se.filtermap.admdev.filteredmapview.viewmodel.FilterMapViewModel

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

        bottom_sheet_max_pop_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(v: SeekBar?, maxPopulation: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(v: SeekBar?) {}

            override fun onStopTrackingTouch(v: SeekBar?) {
                bottom_sheet_max_pop_title.text = getString(R.string.population_max_title, bottom_sheet_max_pop_seek_bar.progress)
                onPopulationChanged(bottom_sheet_min_pop_seek_bar.progress, bottom_sheet_max_pop_seek_bar.progress)
            }
        })

        bottom_sheet_min_pop_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(v: SeekBar?, minPopulation: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(v: SeekBar?) {}

            override fun onStopTrackingTouch(v: SeekBar?) {
                bottom_sheet_min_pop_title.text = getString(R.string.population_min_title, bottom_sheet_min_pop_seek_bar.progress)
                onPopulationChanged(bottom_sheet_min_pop_seek_bar.progress, bottom_sheet_max_pop_seek_bar.progress)
            }
        })

        val model = ViewModelProviders.of(this).get(FilterMapViewModel::class.java)
        model.getPopulations().observe(this, Observer<List<City>>(mapAndDataLoaded.track(::onPopulationDataLoaded)))
    }

    private fun onPopulationChanged(minPopulation: Int, maxPopulation: Int) {
        val showAndHide = mMarkerManager.partition { marker -> marker.city.population in minPopulation..maxPopulation }
        val nowVisible = showAndHide.first.filter { !it.marker.isVisible }
        val nowHidden = showAndHide.second.filter { it.marker.isVisible }

        nowVisible.forEach { m -> m.marker.isVisible = true }
        nowHidden.forEach { m -> m.marker.isVisible = false }
    }

    private fun onCameraPositionChanged(cameraPosition: CameraPosition?) {
        //TODO
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val laos = LatLng(36.1075, 120.46889)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(laos))
        mMap.setOnCameraMoveListener {
            log("Zoom level: ${mMap.cameraPosition.zoom}")
            onCameraPositionChanged(mMap.cameraPosition)
        }

        log("Map ready")
    }

    private fun onPopulationDataLoaded(populations: List<City>?) {
        log("Population data loaded ${populations?.size}")
    }

    private fun onMapAndDataLoaded(map: GoogleMap, populations: List<City>?) {
        populations?.apply {
            for (city in this) {
                val marker = MarkerOptions().position(LatLng(city.lat, city.lng)).title(city.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                mMarkerManager.add(CityMarker(map.addMarker(marker), city))
            }
        }

        bottom_sheet_min_pop_seek_bar.setProgress(400000, true)
        bottom_sheet_max_pop_seek_bar.setProgress(500000, true)
        bottom_sheet_max_pop_title.text = getString(R.string.population_max_title, bottom_sheet_max_pop_seek_bar.progress)
        bottom_sheet_min_pop_title.text = getString(R.string.population_min_title, bottom_sheet_min_pop_seek_bar.progress)
        onPopulationChanged(bottom_sheet_min_pop_seek_bar.progress, bottom_sheet_max_pop_seek_bar.progress)
    }
}
