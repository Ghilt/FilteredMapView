package se.filtermap.admdev.filteredmapview

import se.filtermap.admdev.filteredmapview.extensions.log
import se.filtermap.admdev.filteredmapview.model.CityMarker
import se.filtermap.admdev.filteredmapview.model.CountryMarker

class MarkerManager(var previousZoomLevel: Float) {

    //TODO possibly maintain list of city markers in two lists; one for visible one for invisible

    private val zoomThreshold: Float = 4F
    val cities = ArrayList<CityMarker>()
    val countries = ArrayList<CountryMarker>()

    private fun hideCities() {
        cities.forEach { it.marker.isVisible = false }
    }

    private fun showCountries() {
        countries.forEach { it.marker.isVisible = true }
    }

    private fun showCities(minPop: Int, maxPop: Int) {
        cities.forEach { it.marker.isVisible = it.city.population in minPop..maxPop }
    }

    private fun hideCountries() {
        countries.forEach { it.marker.isVisible = false }
    }

    fun updateZoomLevel(zoom: Float, minPop: Int, maxPop: Int) {
        if (!isZoomedOut(zoom) && isZoomedOut(previousZoomLevel)) {
            onCloseZoom(minPop, maxPop)
            log("Show all cities when zoom reached: $zoom")
        } else if (isZoomedOut(zoom) && !isZoomedOut(previousZoomLevel) ) {
            onFarZoom()
            log("Hide all cities when zoom reached: $zoom")
        }
        previousZoomLevel = zoom
    }

    private fun onCloseZoom(minPop: Int, maxPop: Int) {
        showCities(minPop, maxPop)
        hideCountries()
    }

    private fun onFarZoom() {
        hideCities()
        showCountries()
    }

    fun isZoomedOut(zoom: Float) = zoomThreshold >= zoom
}