package com.sarrafenterprises.app7_nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sarrafenterprises.app7_nav.databinding.FragmentCvFragementBinding

class cvFragement : Fragment() {

    private lateinit var binding: FragmentCvFragementBinding
    private var maxReading: Int = Int.MIN_VALUE
    private var minReading: Int = Int.MAX_VALUE
    private var value = 0
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCvFragementBinding.inflate(inflater, container, false)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("control_value")

        // Read value from Firebase and update TextView
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                value = dataSnapshot.getValue(Int::class.java)!!
                if (value != null) {
//                    binding.value.text = value.toString() + "mA"
                    updateTextView()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

                Toast.makeText(context,"Request Failed!!!", Toast.LENGTH_SHORT).show()
            }})

        binding.btnPlus.setOnClickListener {
            if (value < 20) { // Limit maximum value to 20
                value++
                databaseReference.setValue(value)
                updateTextView()
            }
        }
        binding.btnMinus.setOnClickListener {
            if (value > 4) { // Limit minimum value to 4
                value--
                databaseReference.setValue(value)
                updateTextView()
            }
        }

        return binding.root

    }

    private fun updateTextView() {
        binding.value.text = "${value} mA"
        var perValue = calculatePercentage(value)
        binding.textPer.text = perValue.toString()
        updateMinMax(perValue?: 0)
    }
    private fun calculatePercentage(value: Int): Int {
        // Linear interpolation formula
        return 100 - ((value - 4) * 100) / (20 - 4)
    }

    private fun updateMinMax(perValue: Int) {
        maxReading = maxOf(maxReading, perValue)
        minReading = minOf(minReading, perValue)

        binding.maxRead.text = "${maxReading}"
        binding.minRead.text = "${minReading}"
    }

}