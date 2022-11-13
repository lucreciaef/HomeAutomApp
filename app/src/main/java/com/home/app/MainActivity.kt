package com.home.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.home.app.databinding.ActivityMainBinding
import com.home.app.ui.main.SectionsPagerAdapter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    enum class ButtonPowerSwitchStatus {
        POWER_IS_ON, POWER_IS_OFF
    }

    private var buttonPowerSwitchStatus = ButtonPowerSwitchStatus.POWER_IS_OFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val buttonPowerSwitch: FloatingActionButton = binding.buttonPowerSwitch

        buttonPowerSwitch.setOnClickListener { view ->
            if (buttonPowerSwitchStatus==ButtonPowerSwitchStatus.POWER_IS_OFF) {
                turnOnShellyPlugExtractorFan()
                Snackbar.make(view, "uwu You turned me on", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null).show()
            }
            else if (buttonPowerSwitchStatus==ButtonPowerSwitchStatus.POWER_IS_ON) {
                turnOffShellyPlugExtractorFan()
                Snackbar.make(view, "uwu You turned me off", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null).show()
            }
        }
    }

    private fun turnOnShellyPlugExtractorFan() {

        val thread = Thread {
            try {
                val requestURL = URL("http://192.168.0.111/relay/0?turn=on")

                    with(requestURL.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"

                        println("URL : $url")
                        println("Response Code : $responseCode")

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            println("Response : $response")
                            buttonPowerSwitchStatus = ButtonPowerSwitchStatus.POWER_IS_ON
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun turnOffShellyPlugExtractorFan() {
        val thread = Thread {
            try {
                val requestURL = URL("http://192.168.0.111/relay/0?turn=off")

                with(requestURL.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"

                    println("URL : $url")
                    println("Response Code : $responseCode")

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        println("Response : $response")
                        buttonPowerSwitchStatus = ButtonPowerSwitchStatus.POWER_IS_OFF
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}