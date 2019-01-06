package se.filtermap.admdev.filteredmapview.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.opencsv.CSVReader
import se.filtermap.admdev.filteredmapview.R
import java.io.InputStreamReader

class FilterMapViewModel(app: Application) : AndroidViewModel(app) {

    private lateinit var populations: MutableLiveData<List<Int>>

    fun getPopulations(): LiveData<List<Int>> {
        if (!::populations.isInitialized) {
            populations = MutableLiveData()
            loadCityData()
        }
        return populations
    }

    private fun loadCityData() {

        // TODO utilize data
        val inputStream = getApplication<Application>().resources.openRawResource(R.raw.unsd_citypopulation_year_both)
        val reader = CSVReader(InputStreamReader(inputStream))
        @Suppress("UNUSED_VARIABLE") val cities = reader.readAll()


        populations.value = mutableListOf(
            1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000,
            10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000,
            15000, 25000, 35000, 45000, 55000, 65000, 75000, 85000, 95000,
            100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000
        )
    }
}