package com.home.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Tap on the Button to turn on/off the office fan"
    }

    fun setIndex(index: Int) {
        _index.value = index
    }
}