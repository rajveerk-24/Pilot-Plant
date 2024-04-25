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
import com.sarrafenterprises.app7_nav.databinding.FragmentFlowFragementBinding


class flowFragement : Fragment() {

private lateinit var binding: FragmentFlowFragementBinding
    private lateinit var database: FirebaseDatabase

    private var maxFlow: Double = Double.MIN_VALUE
    private var minFlow: Double = Double.MAX_VALUE

    private lateinit var flowChart: ScatterChart
    private val entries = ArrayList<Entry>()
    private val timestamps = ArrayList<Long>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFlowFragementBinding.inflate(inflater, container, false)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance()

        // Get reference to the "temperature" node in Firebase Realtime Database
        val flowRef = database.getReference("flow_rate")

        setupChart()

        // Set up ValueEventListener to listen for changes in temperature data
        val flowListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val flow = dataSnapshot.getValue(Double::class.java)
                binding.textView.text = "${flow ?: "N/A"}"

                updateMinMaxFlow(flow ?: 0.0)

                updateChart(flow ?: 0.0)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Toast.makeText(context,"Failed to read sensor data", Toast.LENGTH_SHORT).show()
            }
        }

        // Attach the listener to the "level" node in Firebase Realtime Database
        flowRef.addValueEventListener(flowListener)



        return binding.root
    }


    private fun updateMinMaxFlow(flow: Double) {
        maxFlow = maxOf(maxFlow, flow)
        minFlow = minOf(minFlow, flow)

        binding.maxFlow.text = "${maxFlow}"
        binding.minFlow.text = "${minFlow}"
    }


    private fun setupChart() {
        binding.flowChart.apply {
            setTouchEnabled(true)
            description = Description().apply {
                text = "Flow rate Data"
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

    private fun updateChart(flow: Double) {
        val currentTime = System.currentTimeMillis()
        timestamps.add(currentTime)
        entries.add(Entry(timestamps.size.toFloat(), flow.toFloat()))
        val dataSet = LineDataSet(entries, "Flow Rate")
        val lineData = LineData(dataSet)
        binding.flowChart.data = lineData
        binding.flowChart.notifyDataSetChanged()
        binding.flowChart.invalidate()
    }


}