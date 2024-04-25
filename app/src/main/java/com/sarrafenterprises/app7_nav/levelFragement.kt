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
import com.sarrafenterprises.app7_nav.databinding.FragmentLevelFragementBinding


class levelFragement : Fragment() {

    private lateinit var binding: FragmentLevelFragementBinding
    private lateinit var database: FirebaseDatabase

    private var maxLevel: Double = Double.MIN_VALUE
    private var minLevel: Double = Double.MAX_VALUE

    private lateinit var levelChart: ScatterChart
    private val entries = ArrayList<Entry>()
    private val timestamps = ArrayList<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLevelFragementBinding.inflate(inflater, container, false)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance()

        // Get reference to the "temperature" node in Firebase Realtime Database
        val levelRef = database.getReference("tank_level")

        setupChart()

        // Set up ValueEventListener to listen for changes in temperature data
        val levelListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val level = dataSnapshot.getValue(Double::class.java)
                binding.textView.text = "${level ?: "N/A"}"

                updateMinMaxLevel(level ?: 0.0)

                updateChart(level ?: 0.0)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Toast.makeText(context,"Failed to read sensor data", Toast.LENGTH_SHORT).show()
            }
        }

        // Attach the listener to the "level" node in Firebase Realtime Database
        levelRef.addValueEventListener(levelListener)



        return binding.root
    }

    private fun updateMinMaxLevel(level: Double) {
        maxLevel = maxOf(maxLevel, level)
        minLevel = minOf(minLevel, level)

        binding.maxlevel.text = "${maxLevel}"
        binding.minlevel.text = "${minLevel}"
    }


    private fun setupChart() {
        binding.levelChart.apply {
            setTouchEnabled(true)
            description = Description().apply {
                text = "Tank Level Data"
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

    private fun updateChart(level: Double) {
        val currentTime = System.currentTimeMillis()
        timestamps.add(currentTime)
        entries.add(Entry(timestamps.size.toFloat(), level.toFloat()))
        val dataSet = LineDataSet(entries, "Tank Level")
        val lineData = LineData(dataSet)
        binding.levelChart.data = lineData
        binding.levelChart.notifyDataSetChanged()
        binding.levelChart.invalidate()
    }



}