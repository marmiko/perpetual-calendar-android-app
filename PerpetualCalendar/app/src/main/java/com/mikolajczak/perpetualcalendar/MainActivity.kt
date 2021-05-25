package com.mikolajczak.perpetualcalendar


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mikolajczak.perpetualcalendar.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initializeYearPicker()
        initializeSundaysButton()
        initializeCalculateButton()
        getAndShowDates(binding.yearPicker.value)

    }

    private fun initializeSundaysButton() {
        binding.sundaysButton.setOnClickListener {
            if (binding.yearPicker.value < 2020) { //Jeżeli wybrany rok jest przed 2020, to nie obliczamy niedzieli handlowych
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage(R.string.sundaysAlertText)

                builder.setPositiveButton(R.string.leaveActivityButton) { _, _ ->
                }

                val dialog = builder.create()
                dialog.show()

            } else {
                val intent = Intent(this, SundaysActivity::class.java)
                intent.putExtra("Year", binding.yearPicker.value)
                intent.putExtra("Easter", binding.easterDateText.text)
                startActivity(intent)
            }
        }
    }

    private fun initializeCalculateButton() {
        binding.calculateDaysButton.setOnClickListener {
            val intent = Intent(this, CalculateDaysActivity::class.java)
            startActivity(intent)
        }
    }


    private fun initializeYearPicker() {
        binding.yearPicker.minValue = 1990
        binding.yearPicker.maxValue = 2200
        binding.yearPicker.wrapSelectorWheel = true

        binding.yearPicker.setOnValueChangedListener { _, _, newVal ->
            getAndShowDates(newVal)
        }
    }


    private fun getAndShowDates(year: Int) {

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val easter = getEasterDate(year) //Wielkanoc
        val christmas = LocalDate.of(year, 12, 25)
        val adventDate: LocalDate

        adventDate = if (christmas.dayOfWeek.ordinal == 6) { //Jeżeli niedziela, to po prostu odejmujemy 4 tygodnie
            christmas.minusWeeks(4)
        } else {
            //Jeżeli Boże Narodzenie wypada w inny dzień, to cofamy do niedzieli i odejmujemy 3 tygodnie
            christmas.minusDays(christmas.dayOfWeek.ordinal.toLong() + 1).minusWeeks(3)
        }

        val corpusCrispi = easter.plusDays(60) //Boże Ciało
        val ashTuesday = easter.minusDays(46) //Środa Popielcowa

        binding.adventDateText.text = adventDate.format(formatter)
        binding.ashDateText.text = ashTuesday.format(formatter)
        binding.corpusDateText.text = corpusCrispi.format(formatter)
        binding.easterDateText.text = easter.format(formatter)

    }


}