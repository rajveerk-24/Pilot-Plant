package com.sarrafenterprises.app7_nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sarrafenterprises.app7_nav.databinding.FragmentTemperatureFragementBinding


class temperatureFragement : Fragment() {
    private lateinit var binding: FragmentTemperatureFragementBinding
    private lateinit var database: FirebaseDatabase

    private var maxTemperature: Double = Double.MIN_VALUE
    private var minTemperature: Double = Double.MAX_VALUE

    private lateinit var temperatureChart: ScatterChart
    private val entries = ArrayList<Entry>()
    private val timestamps = ArrayList<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTemperatureFragementBinding.inflate(inflater, container, false)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance()

        // Get reference to the "temperature" node in Firebase Realtime Database
        val temperatureRef = database.getReference("temperature")

        setupChart()

        // Set up ValueEventListener to listen for changes in temperature data
        val temperatureListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val temperature = dataSnapshot.getValue(Double::class.java)
                binding.textView.text = "${temperature ?: "N/A"}"

                updateMinMaxTemperatures(temperature ?: 0.0)

                updateChart(temperature ?: 0.0)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Toast.makeText(context,"Failed to read sensor data",Toast.LENGTH_SHORT).show()
            }
        }

        // Attach the listener to the "temperature" node in Firebase Realtime Database
        temperatureRef.addValueEventListener(temperatureListener)



        return binding.root
    }
    private fun updateMinMaxTemperatures(temperature: Double) {
        maxTemperature = maxOf(maxTemperature, temperature)
        minTemperature = minOf(minTemperature, temperature)

        binding.maxTemp.text = "${maxTemperature}"
        binding.minTemp.text = "${minTemperature}"
    }


    private fun setupChart() {
        binding.temperatureChart.apply {
            setTouchEnabled(true)
            description = Description().apply {
                text = "Temperature Data"
            }
            xAxis.apply {
                setDrawLabels(true)
                setDrawGridLines(false)
            }
            axisLeft.apply {
                setDrawGridLines(false)
            }
            axisRight.apply {
                setDrawGridLines(false)
            }
        }
    }

    private fun updateChart(temperature: Double) {
        val currentTime = System.currentTimeMillis()
        timestamps.add(currentTime)
        entries.add(Entry(timestamps.size.toFloat(), temperature.toFloat()))
        val dataSet = LineDataSet(entries, "Temperature")
        val lineData = LineData(dataSet)
        binding.temperatureChart.data = lineData
        binding.temperatureChart.notifyDataSetChanged()
        binding.temperatureChart.invalidate()
    }

}


