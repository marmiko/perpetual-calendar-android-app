package com.mikolajczak.perpetualcalendar

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.mikolajczak.perpetualcalendar.databinding.ActivitySundaysBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SundaysActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySundaysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySundaysBinding.inflate(layoutInflater)
        val view = binding.root
        val extras = intent.extras ?: return
        val year = extras.getInt("Year")

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val easter = LocalDate.parse(extras.getString("Easter"), formatter)

        binding.sundaysInfoText.text = getString(R.string.sundaysInfoText, year)

        binding.leaveSundaysActivityButton.setOnClickListener {
            finish()
        }

        val listItems = getSundaysList(year, easter)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        binding.SundayItems.adapter = adapter

        setContentView(view)
    }

    private fun getSundaysList(year: Int, easter: LocalDate): Array<String> {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)

        val months = arrayOf(1, 4, 6, 8)
        val sundays = mutableListOf<LocalDate>()


        var tempDate: LocalDate

        for (month in months) {
            calendar.set(Calendar.MONTH, month - 1)
            tempDate = LocalDate.of(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

            while (tempDate.dayOfWeek.ordinal != 6) {
                tempDate = tempDate.minusDays(1)
            }
            sundays.add(tempDate)
        }

        tempDate = LocalDate.of(year, 12, 24)

        var found = 0
        while (found != 2) {
            if (tempDate.dayOfWeek.ordinal == 6 && tempDate.dayOfMonth != 24) {
                sundays.add(tempDate)
                found += 1
            }
            tempDate = tempDate.minusDays(1)
        }

        sundays.add(easter.minusWeeks(1))

        val sundaysStrings: MutableList<String> = mutableListOf()
        val formatter = DateTimeFormatter.ofPattern("dd MMM")

        for(sunday in sundays){
            sundaysStrings.add(sunday.format(formatter))
        }

        return sundaysStrings.toTypedArray()

    }

}