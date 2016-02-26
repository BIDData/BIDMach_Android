package com.BIDMach

//Scala
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

//Android
import android.os.Bundle
import android.app.Activity
import android.util.Log

//BIDMach
//import BIDMat.Mat
//import BIDMat.MatFunctions._
//import BIDMach.Learner
//import BIDMach.models.GLM

import BIDMat.Mat
//import BIDMat.{Mat, FMat, CLMat}
//import BIDMat.SciFunctions.{rand}
//import edu.berkeley.bid.CBLAS.{sgemm, ORDER, TRANSPOSE}

//import resource.{managed}

class MainActivity extends Activity {

  val TAG = "BIDMach_Android"

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
  val foo = "foo"*10

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    Log.d(TAG, "=" * 30)
    Log.d(TAG, "BIDMach_Android")


    try {
      Mat.useOpenCL = true
      Mat.checkOpenCL(true)

      //Benchmark.run_sgemm(1024, 4096, 4)

      //Benchmark.run_smcscm(1024, 4096, 4, 0.05)
      //Benchmark.run_smcscm(1024, 4096, 4, 0.1)
      //Benchmark.run_smcscm(1024, 4096, 4, 0.2)
      //Benchmark.run_smcscm(1024, 4096, 4, 0.3)

      //Benchmark.run_cl_sgemm_naive(1024, 2048, 4)
      //Benchmark.run_cl_sgemm_tiled(1024, 2048, 4, 16)

      //Benchmark.run_cl_sgemm_tiled_vectorized(1024, 2048, 4, 8, 8)
      //Benchmark.run_cl_sgemm_tiled_vectorized(1024, 2048, 4, 16, 8)
      //Benchmark.run_cl_sgemm_tiled_vectorized(1024, 2048, 4, 32, 8)
      //Benchmark.run_cl_sgemm_tiled_vectorized(1024, 2048, 4, 8, 4)
      //Benchmark.run_cl_sgemm_tiled_vectorized(1024, 2048, 4, 16, 4)
      //Benchmark.run_cl_sgemm_tiled_vectorized(1024, 2048, 4, 32, 4)

      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 16, 4, 4)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 16, 4, 8)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 32, 4, 4)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 32, 4, 8)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 32, 4, 16)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 64, 4, 8)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 64, 4, 16)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 64, 4, 32)

      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 16, 8, 8)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 32, 8, 8)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 32, 8, 16)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 64, 8, 8)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 64, 8, 16)
      //Benchmark.run_cl_sgemm_register_blocking(1024, 2048, 4, 64, 8, 32)


      //Benchmark.run_cl_sgemm_clblast(1024, 4096, 4, Map(
        //"MWG" -> 64, "NWG" -> 64, "KWG" -> 16,
        //"MDIMC" -> 8, "NDIMC" -> 8, "MDIMA" -> 16, "NDIMB" -> 16,
        //"KWI" -> 8, "VWM" -> 8, "VWN" -> 8,
        //"STRM" -> 0, "STRN" -> 0,
        //"SA" -> 1, "SB" -> 1,
        //"USE_VECTOR_MAD" -> 0,
        //"USE_CL_MAD" -> 1
      //))


      //Benchmark.run_cl_sgemm_adreno(1024, 2048, 4, 32)
      //Benchmark.run_cl_sgemm_adreno(1024, 2048, 4, 64)
      //Benchmark.run_cl_sgemm_adreno(1024, 2048, 4, 128)

      //Benchmark.run_cl_transpose(1024, 4096, 3, 1)
      //Benchmark.run_cl_transpose(1024, 4096, 3, 2)
      //Benchmark.run_cl_transpose(1024, 4096, 3, 4)
      //Benchmark.run_cl_transpose(1024, 4096, 3, 8)
      //Benchmark.run_cl_transpose(1024, 4096, 3, 16)
      //Benchmark.run_cl_transpose(1024, 4096, 3, 32)
      //Benchmark.run_cl_transpose(1024, 4096, 3, 64)
      //Benchmark.run_cl_transpose(1024, 4096, 3, 128)
      
      //Benchmark.run_cl_create_free_buffer(1024, 4096, 3)
    } catch {
      case e: Error => println(e)
    } finally {
      Mat.freeOpenCL()
    }

    //Log.d(TAG, s"cpus online: ( ${Benchmark.cpus_status_str()} )")

    //Benchmark.warmup(10, 400, true)
    //Benchmark.run_sgemm(1000, 5)
    //Benchmark.run_smcscm(3000, 0.25, 5)
  }

}
