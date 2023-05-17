package com.home.app.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.home.app.databinding.FragmentMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.home.app.ShellyPlugJSON
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    enum class ButtonPowerSwitchStatus {
        POWER_IS_ON, POWER_IS_OFF
    }

    private var buttonPowerSwitchStatus = ButtonPowerSwitchStatus.POWER_IS_OFF

    private fun getInitialButtonPowerSwitchStatus() {
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
                        val initialStatus = responseJson.ison.toString()
//                        onResponseHandler(initialStatus)

                        buttonPowerSwitchStatus =
                            if (initialStatus == "false") ButtonPowerSwitchStatus.POWER_IS_OFF
                            else ButtonPowerSwitchStatus.POWER_IS_ON

                        // Show a snack-bar message with the initial power switch status
                        Snackbar.make(binding.root, "Is the switch ON?: $initialStatus", Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", null).show()
                    }
                }
            } catch (e: Exception) {
                //                e.printStackTrace()
                Snackbar.make(binding.root, "Could not connect", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null).show()
            }
        }
    }

    private fun togglePowerShellyPlugExtractorFan(status: String) {
        thread(true) {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Call the getInitialButtonPowerSwitchStatus function and pass the callback function as an argument
        getInitialButtonPowerSwitchStatus()

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        val buttonPowerSwitch: FloatingActionButton = binding.buttonPowerSwitch

        buttonPowerSwitch.setOnClickListener { view ->
            val status = if (buttonPowerSwitchStatus == ButtonPowerSwitchStatus.POWER_IS_OFF)
                "on" else "off"

            val message = if (status == "on") "uwu You turned me on" else "uwu You turned me off"

            togglePowerShellyPlugExtractorFan(status)
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", null).show()
        }

        val textView: TextView = binding.sectionLabel
        pageViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

