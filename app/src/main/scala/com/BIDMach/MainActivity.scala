package com.BIDMach

import android.os.Bundle
import android.app.Activity
import android.util.Log

import BIDMat.{Mat, FMat, CLMat}
import BIDMat.SciFunctions.{rand}

import resource.{managed}

class MainActivity extends Activity {

  val TAG = "BIDMach_Android"

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    Log.d(TAG, "BIDMach_Android")

    Mat.checkMKL(true)
    Mat.checkOpenCL(true)

    val a = rand(5, 5)
    val b = rand(5, 5)

    for {
      a_ <- managed(CLMat(a))
      b_ <- managed(CLMat(b))
      c_ <- managed(a_ * b_)
    } {
      println("a:")
      println(a)
      println("b:")
      println(a)
      println("c:")
      println(c_)
      println("a * b:")
      println(a * b)
    }
  }

}
