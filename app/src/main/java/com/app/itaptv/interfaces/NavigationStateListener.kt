package com.app.itaptv.interfaces

interface NavigationStateListener {
    fun onStateChanged(expanded: Boolean, lastSelected: String?)
}