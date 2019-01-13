package se.filtermap.admdev.filteredmapview.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.opencsv.CSVReader
import se.filtermap.admdev.filteredmapview.R
import se.filtermap.admdev.filteredmapview.model.City
import java.io.InputStreamReader

class FilterMapViewModel(app: Application) : AndroidViewModel(app) {

    private lateinit var populations: MutableLiveData<List<City>>

    fun getPopulations(): LiveData<List<City>> {
        if (!::populations.isInitialized) {
            populations = MutableLiveData()
            loadCityData()
        }
        return populations
    }

    private fun loadCityData() {

        val inputStream = getApplication<Application>().resources.openRawResource(R.raw.worldcities)
        val reader = CSVReader(InputStreamReader(inputStream))
        val cities = reader.readAll()
            .drop(1)
            .filter { csvRow -> csvRow[9].toIntOrNull() != null }
            .map { csvRow -> City(csvRow[0], csvRow[9].toInt(), csvRow[2].toDouble(), csvRow[3].toDouble()) }
        populations.value = cities

    }
}