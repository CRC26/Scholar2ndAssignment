package ie.wit.scholar.main

import android.app.Application
import timber.log.Timber

class ScholarApp : Application() {

    //lateinit var scholarsStore: ScholarStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
      //  scholarsStore = ScholarManager()
        Timber.i("Scholar Application Started")
    }
}