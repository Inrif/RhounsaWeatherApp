package com.rhounsaweatherapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by rhounsa on 17/07/2022.
 */

@HiltAndroidApp
@Suppress("unused")
class AppClass : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .build()
    }
}
