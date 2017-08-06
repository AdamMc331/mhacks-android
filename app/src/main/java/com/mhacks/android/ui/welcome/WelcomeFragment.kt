package com.mhacks.android.ui.welcome


import android.view.View
import com.gelitenight.waveview.sample.WaveHelper
import com.mhacks.android.ui.common.BaseFragment
import com.mhacks.android.ui.common.NavigationColor
import com.mhacks.android.ui.common.WaveCountdownView
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.mhacks.android.R

/**
 * This Fragment is the entry fragment and contains information about where the user is.
 */

class WelcomeFragment : BaseFragment() {

    override var FragmentColor: Int = R.color.colorPrimary
    override var AppBarTitle: Int = R.string.welcome
    override var LayoutResourceID: Int = R.layout.fragment_welcome

    private lateinit var waveHelper: WaveHelper

    override var configureView: (view: View) -> Unit? = fun(view: View) {
        welcome_wave_countdown.setShapeType(WaveCountdownView.ShapeType.CIRCLE)
        waveHelper = WaveHelper(welcome_wave_countdown)

    }
    override var NavigationColor: NavigationColor = NavigationColor(R.color.colorPrimary, R.color.colorPrimaryDark)

    override fun onResume() {
        super.onResume()
        waveHelper.start()
    }

    override fun onPause() {
        super.onPause()
        waveHelper.cancel()
    }

    companion object {
        val instance get() = WelcomeFragment()
    }

}

