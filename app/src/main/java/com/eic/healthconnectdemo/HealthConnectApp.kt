package com.eic.healthconnectdemo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Health Connect Demo.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class HealthConnectApp : Application()
