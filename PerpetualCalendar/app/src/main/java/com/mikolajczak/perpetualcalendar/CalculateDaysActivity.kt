package com.mikolajczak.perpetualcalendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mikolajczak.perpetualcalendar.databinding.ActivityCalculateDaysBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class CalculateDaysActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculateDaysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculateDaysBinding.inflate(layoutInflater)
        val view = binding.root

        binding.leaveDaysActivityButton.setOnClickListener {
            finish()
        }

        binding.calendarDaysText.text = getString(R.string.calendarDaysText, 0)
        binding.workingDaysText.text = getString(R.string.calendarDaysText, 0)

        initializeEndDatePicker()
        initializeStartDatePicker()

        setContentView(view)
    }


    private fun initializeStartDatePicker() {
        binding.startDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val startDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            val endDate = LocalDate.of(
                binding.endDatePicker.year,
                binding.endDatePicker.month + 1,
                binding.endDatePicker.dayOfMonth
            )

            val days: Long = ChronoUnit.DAYS.between(startDate, endDate)
            val workingDays = calculateWorkingDays(startDate, endDate)
            binding.calendarDaysText.text = getString(R.string.calendarDaysText, days)
            binding.workingDaysText.text = getString(R.string.workingDaysText, workingDays)
        }
    }

    private fun initializeEndDatePicker() {
        binding.endDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val endDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            val startDate = LocalDate.of(
                binding.startDatePicker.year,
                binding.startDatePicker.month + 1,
                binding.startDatePicker.dayOfMonth
            )

            val days: Long = ChronoUnit.DAYS.between(startDate, endDate)
            val workingDays = calculateWorkingDays(startDate, endDate)
            binding.calendarDaysText.text = getString(R.string.calendarDaysText, days)
            binding.workingDaysText.text = getString(R.string.workingDaysText, workingDays)
        }
    }


    private fun calculateEasters(startYear: Int, endYear: Int): MutableMap<Int, LocalDate> {

        val easterMap = mutableMapOf<Int, LocalDate>()
        for (year in startYear..endYear) {
            val easterDate = getEasterDate(year)
            easterMap[year] = easterDate
        }

        return easterMap
    }

    private fun calculateNumberOfWeekendDays(startDate: LocalDate, endDate: LocalDate): Long {
        //Zliczamy liczbę dni weekendowych w taki sposób:
        //od startDate idziemy do przodu aż do pierwszego poniedziałku, zliczając dni weekendowe po drodze
        //od endDate cofamy się do pierwszego poniedziałku, także zliczając dni weekendowe po drodze
        //Następnie liczmy liczbę tygodni pomiędzy skrajnymi poniedziałkami.
        //Wynik to 2 * liczba_tygodni + zliczone_po_drodze_weekendy

        var tempDate1 = startDate
        var tempDate2 = endDate

        var toAdd = 0

        while (tempDate2.dayOfWeek.ordinal != 0) {
            if (tempDate2.dayOfWeek.ordinal > 4) {
                toAdd += 1
            }
            tempDate2 = tempDate2.minusDays(1)
        }

        while (tempDate1.dayOfWeek.ordinal != 0) {
            if (tempDate1.dayOfWeek.ordinal > 4) {
                toAdd += 1
            }
            tempDate1 = tempDate1.plusDays(1)
        }

        val weeks = ChronoUnit.WEEKS.between(tempDate1, tempDate2)

        return weeks * 2 + toAdd

    }

    private fun isBetweenDates(
        date: LocalDate?,
        startDate: LocalDate,
        endDate: LocalDate
    ): Boolean {

        if (date != null) {
            return date.isEqual(startDate) or date.isEqual(endDate) or (date.isAfter(startDate) and date.isBefore(
                endDate
            ))
        }

        return false
    }

    private fun calculateWorkingDays(startDate: LocalDate, endDate: LocalDate): Long {

        val startDate1: LocalDate
        val endDate1: LocalDate
        var reverse = false

        if (startDate.isAfter(endDate)) { //Jeżeli startDate > endDate, to zamieniamy na potrzeby obliczeń i potem zwrócimy z minusem
            startDate1 = endDate
            endDate1 = startDate
            reverse = true
        } else {
            startDate1 = startDate
            endDate1 = endDate
        }

        //Liczymy wielkanoce we wszystkich latach [startDate.year, endDate.year]
        val easters = calculateEasters(
            startDate1.year,
            endDate1.year
        )
        var daysBetween = ChronoUnit.DAYS.between(startDate1, endDate1)

        //Święta w formacie (dzień, miesiąc)
        val holidays = mutableListOf(
            intArrayOf(1, 1), intArrayOf(6, 1), intArrayOf(1, 5), intArrayOf(3, 5),
            intArrayOf(15, 8), intArrayOf(1, 11), intArrayOf(25, 12), intArrayOf(26, 12)
        )
        var holidayDate: LocalDate

        for (year in startDate1.year..endDate1.year) {
            for (holiday in holidays) {
                holidayDate = LocalDate.of(
                    year,
                    holiday[1],
                    holiday[0]
                ) //Jeżeli świeto zawiera się między starDate a endDate i nie jest weekendem, to odejmujemy

                if (holidayDate.dayOfWeek.ordinal < 5) {
                    if (isBetweenDates(holidayDate, startDate1, endDate1)) {
                        daysBetween -= 1
                    }
                }

            }
            if (isBetweenDates(
                    easters[year]?.plusDays(1),
                    startDate1,
                    endDate1
                )
            ) { //Jeżeli poniedziałek wielkanocny zawiera się w okresie między datami, to odejmij
                daysBetween -= 1
            }

            if (isBetweenDates(
                    easters[year]?.plusDays(60),
                    startDate1,
                    endDate1
                )
            ) { //to samo dla Bożego Ciała
                daysBetween -= 1
            }
        }

        daysBetween -= calculateNumberOfWeekendDays(startDate1, endDate1)

        if (reverse) {
            daysBetween *= -1
        }

        return daysBetween
    }


}


