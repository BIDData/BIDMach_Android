package com.BIDMach

//Scala
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

//Android
import android.os.Bundle
import android.app.Activity

//Java
import java.io.File

//BIDMach
import BIDMach.Learner
import BIDMat.{FMat, SMat}
import BIDMat.MatFunctions._
import BIDMach.models.GLM

class MainActivity extends Activity {

	val dataPath = "data/BIDMach_Data/"

	override def onCreate(savedInstanceState: Bundle) = {
		println("In MainActivity for BIDMach.")
		/*
		println("Loading Data..")
		val a = loadSMat(dataPath + "docs.smat.lz4")
		println("Loading Labels..")
		val c = loadFMat(dataPath + "cats.fmat.lz4")
		println("Creating GLM..")
		val (mm, mopts) = GLM.learner(a, c, 1)
		println("Starting Training..")
		mm.train
		println("Finished Training!")*/
	}
}
