package com.BIDMach

import scala.language.postfixOps

import android.os.Bundle
import android.app.Activity

import scala.concurrent.ExecutionContext.Implicits.global

class MainActivity extends Activity {

  override def onCreate(savedInstanceState: Bundle) = {
	println("Hello, world!")
  }
}
