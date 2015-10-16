package com.BIDMach

//Scala
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

//Android
import android.os.Bundle
import android.app.Activity
import android.util.Log

//BIDMach
import BIDMat.Mat
import BIDMat.MatFunctions._
import BIDMach.Learner
import BIDMach.models.GLM

class MainActivity extends Activity {

  val dataPath = "/mnt/sdcard/bidmach_data/"
  val TAG = "BIDMach_Android"

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    // turn off native math libraries
    Mat.useMKL = false

    Log.d(TAG, "BIDMach_Android")

    // NOTE: weird runtime error if you remove this
    // ---
    // it appears that some step in the build process is pruning out the Scala
    // String type definition (possibly ProGuard), which causes a method "link"
    // error at runtime, because it says it can't find, for example,
    // loadSMat(name:java.lang.String) when in fact the actual method header is
    // loadSMat(name:scala.String), even though type String = java.lang.String
    //
    // doing `"foo " * 10` forces the scala String definition to be loaded,
    // preventing the method not found error
    println("foo " * 10)

    Log.d(TAG, "Loading Data...")

    // load the smaller, uncompressed dataset
    val scats = loadFMat(dataPath + "scats.fmat")
    val sdocs = loadSMat(dataPath + "sdocs.smat")

    // configure learner
    val (mm, mopts) = GLM.learner(sdocs, scats, 1)

    Log.d(TAG, "Starting Training..")
    mm.train
    Log.d(TAG, "Finished Training!")
  }
}
