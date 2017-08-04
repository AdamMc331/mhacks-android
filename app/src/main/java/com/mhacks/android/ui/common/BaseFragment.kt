package com.mhacks.android.ui.common

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * The base which Fragments in this project will extend.
 */

abstract class BaseFragment : Fragment() {

    private var callback: OnNavigationChangeListener? = null

    abstract var fragmentColor: Int set
    abstract var appBarTitle: Int set
    abstract var navigationColor: NavigationColor set
    abstract var layoutResourceID: Int set
    abstract var configureView: (view: View) -> Unit? set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callback = activity as OnNavigationChangeListener
    }

    override final fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        changeColors()
        val view = inflater!!.inflate(layoutResourceID, container, false)
        configureView(view)
        return view
    }

    private fun changeColors() {
        callback!!.setFragmentTitle(appBarTitle)
        callback!!.setActionBarColor(fragmentColor)
        callback!!.setBottomNavigationColor(navigationColor)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            callback!!.setStatusBarColor(android.R.color.transparent)
            callback!!.setTransparentStatusBar()
            if (fragmentColor == android.R.color.transparent) {
                callback!!.setTransparentStatusBar()
            } else {
//                callback!!.clearTransparentStatusBar()
//                callback!!.setStatusBarColor(fragmentColor)
                callback!!.setLayoutFullScreen()

            }
        }
    }

    interface OnNavigationChangeListener {

        fun setFragmentTitle(title: Int)

        fun setActionBarColor(color: Int)

        fun setTransparentStatusBar()

        fun clearTransparentStatusBar()

        fun setLayoutFullScreen()

        fun setStatusBarColor(color: Int)

        fun setBottomNavigationColor(color: NavigationColor)
    }
}