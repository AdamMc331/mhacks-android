package com.gelitenight.waveview.sample

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator

import com.mhacks.android.ui.common.WaveCountdownView

import java.util.ArrayList

class WaveHelper(private val mWaveCountdownView: WaveCountdownView) {

    private var mAnimatorSet: AnimatorSet? = null

    init {
        initAnimation()
    }

    fun start() {
        mWaveCountdownView.isShowWave = true
        if (mAnimatorSet != null) {
            mAnimatorSet!!.start()
        }
    }

    private fun initAnimation() {
        val animators = ArrayList<Animator>()

        // horizontal animation.
        // wave waves infinitely.
        val waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveCountdownView, "waveShiftRatio", 0f, 1f)
        waveShiftAnim.repeatCount = ValueAnimator.INFINITE
        waveShiftAnim.duration = 1000
        waveShiftAnim.interpolator = LinearInterpolator()
        animators.add(waveShiftAnim)

        // vertical animation.
        // water level increases from 0 to center of WaveView
        val waterLevelAnim = ObjectAnimator.ofFloat(
                mWaveCountdownView, "waterLevelRatio", 0f, 0.5f)
        waterLevelAnim.duration = 10000
        waterLevelAnim.interpolator = DecelerateInterpolator()
        animators.add(waterLevelAnim)

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        val amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveCountdownView, "amplitudeRatio", 0.0001f, 0.05f)
        amplitudeAnim.repeatCount = ValueAnimator.INFINITE
        amplitudeAnim.repeatMode = ValueAnimator.REVERSE
        amplitudeAnim.duration = 5000
        amplitudeAnim.interpolator = LinearInterpolator()
        animators.add(amplitudeAnim)

        mAnimatorSet = AnimatorSet()
        mAnimatorSet!!.playTogether(animators)
    }

    fun cancel() {
        if (mAnimatorSet != null) {
            //            mAnimatorSet.cancel();
            mAnimatorSet!!.end()
        }
    }
}
