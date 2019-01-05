package se.filtermap.admdev.filteredmapview

import com.google.android.gms.maps.GoogleMap

data class MapFilter(
    var popMin: Int,
    var popMax: Int
)

fun GoogleMap.applyFilter(mapFilter: MapFilter) {
    // TODO
    // Either make custom markers and store my relevant information in them and access them directly from the map
    // or store them outside of the map together with relevant info and iterate over them that way
}