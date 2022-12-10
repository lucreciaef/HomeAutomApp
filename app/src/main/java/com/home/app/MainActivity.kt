package com.home.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.home.app.databinding.ActivityMainBinding
import com.home.app.ui.main.SectionsPagerAdapter
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    enum class ButtonPowerSwitchStatus {
        POWER_IS_ON, POWER_IS_OFF
    }

    private var buttonPowerSwitchStatus = ButtonPowerSwitchStatus.POWER_IS_OFF

    private fun getInitialButtonPowerSwitchStatus(onResponseCallback: (isOn:String) -> Unit) {
        var initialStatus = ""
        thread(true) {
            try {
                val requestURL = URL("http://192.168.0.111/relay/0")
                val httpMethod = "GET"

                with(requestURL.openConnection() as HttpURLConnection) {
                    requestMethod = httpMethod

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
                        // Parse the JSON response to retrieve the initial status of the power switch
                        val gson = Gson()
                        val responseJson =
                            gson.fromJson(response.toString(), ShellyPlugJSON::class.java)
                        initialStatus = responseJson.ison.toString()
                        onResponseCallback(initialStatus)
                    }
                }
            } catch (e: Exception) {
                //                e.printStackTrace()
                Snackbar.make(binding.root, "Could not connect", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the initial value for buttonPowerSwitchStatus
        val onResponseCallback = {isOn: String ->
            // Update the initial value of the buttonPowerSwitchStatus
            buttonPowerSwitchStatus =
                if (isOn == "false") ButtonPowerSwitchStatus.POWER_IS_OFF
                else ButtonPowerSwitchStatus.POWER_IS_ON

            // Show a snackbar message with the initial power switch status
            Snackbar.make(binding.root, "Is the switch ON?: $isOn", Snackbar.LENGTH_LONG)
                .setAction("Dismiss", null).show()
        }

        // Call the getInitialButtonPowerSwitchStatus function and pass the callback function as an argument
        getInitialButtonPowerSwitchStatus(onResponseCallback)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val buttonPowerSwitch: FloatingActionButton = binding.buttonPowerSwitch

        buttonPowerSwitch.setOnClickListener { view ->
            val status = if (buttonPowerSwitchStatus == ButtonPowerSwitchStatus.POWER_IS_OFF)
                "on" else "off"

            val message = if (status == "on") "uwu You turned me on" else "uwu You turned me off"

            togglePowerShellyPlugExtractorFan(status)
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", null).show()
        }
    }

    private fun togglePowerShellyPlugExtractorFan(status: String) {
        val thread = Thread {
            try {
                val requestURL = URL("http://192.168.0.111/relay/0?turn=$status")
                val httpMethod = "POST"

                with(requestURL.openConnection() as HttpURLConnection) {
                    requestMethod = httpMethod

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
                        buttonPowerSwitchStatus =
                            if (status == "on") ButtonPowerSwitchStatus.POWER_IS_ON
                            else ButtonPowerSwitchStatus.POWER_IS_OFF
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}

class ShellyPlugJSON {
    var ison: String? = null
    var has_timer: String? = null
    var overpower: String? = null
}