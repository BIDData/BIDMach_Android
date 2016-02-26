package com.BIDMach

import android.util.Log

import resource.{managed}

import edu.berkeley.bid.CBLAS.{sgemm, ORDER, TRANSPOSE}
import edu.berkeley.bid.SPBLAS.{smcscm}

import BIDMat.{Mat, FMat, SMat, CLMat}
import BIDMat.MatFunctions._
import BIDMat.SciFunctions._

import Files.{fileString}

object Benchmark {

  val TAG = "BIDMach_Android"

  val DEVICE = "QUALCOMM SNAPDRAGON 820 ADRENO 530"

  val CPU_BLAS = "OPENBLAS"

  // turn off intel math libraries
  Mat.useMKL = false

  def log(msg: String) = {
    Log.d(TAG, msg)
    
  }

  def cpus_status(): IndexedSeq[Boolean] = {
    val prefix = "/sys/devices/system/cpu/"
    (0 until 4) map {
      i =>
        val filename = prefix + "cpu" + i + "/online"
        val online = fileString(filename).trim()
        online == "1"
    }
  }

  def cpus_status_str(): String = s"( ${cpus_status mkString ", "} )"

  def cpus_online(): Boolean = cpus_status() forall (_ == true)

  def sgemm_helper(a: FMat, b: FMat, c: FMat):Unit = {
    Mat.nflops += 2L * a.nrows * a.ncols * b.ncols
    sgemm(ORDER.ColMajor, TRANSPOSE.NoTrans, TRANSPOSE.NoTrans, a.nrows, b.ncols, a.ncols, 1.0f,        a.data,       a.nrows,  b.data,       b.nrows,  0.0f,       c.data,       a.nrows)
  }

  def warmup(max_times: Int = 10, n: Int = 400, debug: Boolean = false): Unit = {
    val a = rand(n, n)
    val c = zeros(n, n)
    var i = 0
    log(s"Warmup: max_times = $max_times, n = $n")
    while (i < max_times && !cpus_online()) {
      sgemm_helper(a, a, c)
      if (debug && i % 5 == 0) {
        log(s"[$i]: cpus = ${cpus_status_str()}")
      }
      i += 1
    }
    log(s"done warming up. cpus = ${cpus_status_str()}")
  }

  def run_sgemm(min: Int, max: Int, times: Int): Unit = {
    log(s"$CPU_BLAS SGEMM - $DEVICE")
    log(s"min=$min, max=$max, times=$times")
    log("N\tIteration\tTime\tGFlops\tCPU0\tCPU1\tCPU2\tCPU3")

    var n = min
    while (n <= max) {
      val a = rand(n, n)
      val b = rand(n, n)
      val c = zeros(n, n)
      for (i <- 1 to times) {
        flip
        sgemm_helper(a, b, c)
        val (gflops, time) = gflop

        val cpus = cpus_status()
        val out = Array[Any](n, i, time, gflops, cpus(0), cpus(1), cpus(2), cpus(3))
        log(out.mkString("\t"))
      }
      n *= 2
    }
  }

  def smcscm_helper(a: FMat, b: SMat, c: FMat): Unit = {
    Mat.nflops += 2L * a.nrows * b.nnz
    smcscm(a.nrows, b.ncols, a.data, a.nrows, b.data, b.ir0, b.jc0, c.data, a.nrows);
  }

  def run_smcscm(min: Int, max: Int, times: Int, density: Double): Unit = {
    log(s"$CPU_BLAS SMCSCM - $DEVICE")
    log(s"min=$max, max=$max, times=$times, density=$density")
    log("N\tIteration\tTime\tGFlops\tCPU0\tCPU1\tCPU2\tCPU3")

    var n = min
    while (n <= max) {
      val a = rand(n, n)
      val b = sprand(n, n, density)
      val c = zeros(n, n)
      for (i <- 1 to times) {
        flip
        smcscm_helper(a, b, c)
        val (gflops, time) = gflop

        val cpus = cpus_status()
        val out = Array[Any](n, i, time, gflops, cpus(0), cpus(1), cpus(2), cpus(3))
        log(out.mkString("\t"))
      }
      n *= 2
    }
  }

  def run_cl_sgemm_naive(min: Int, max: Int, times: Int): Unit = {
    log(s"CL SGEMM NAIVE - $DEVICE")
    log(s"min=$min max=$max times=$times")
    log("N\tIteration\tTime\tGFlops")

    var n = min
    while (n <= max) {
      for {
        a <- managed(CLMat(rand(n, n)))
        b <- managed(CLMat(rand(n, n)))
        c <- managed(CLMat(n, n))
      } {
        for (i <- 1 to times) {
          flip
          a.mult_naive(b, c)
          val (gflops, time) = gflop

          val out = Array[Any](n, i, time, gflops)
          log(out.mkString("\t"))
        }
      }
      n *= 2
    }
  }

  def run_cl_sgemm_tiled(min: Int, max: Int, times: Int, tileSize: Int): Unit = {
    log(s"CL SGEMM TILED - $DEVICE")
    log(s"min=$min max=$max times=$times tileSize=$tileSize")
    log("N\tIteration\tTime\tGFlops")

    var n = min
    while (n <= max) {
      for {
        a <- managed(CLMat(rand(n, n)))
        b <- managed(CLMat(rand(n, n)))
        c <- managed(CLMat(n, n))
      } {
        for (i <- 1 to times) {
          flip
          a.mult_tiled(b, c, tileSize)
          val (gflops, time) = gflop

          val out = Array[Any](n, i, time, gflops)
          log(out.mkString("\t"))
        }
      }
      n *= 2
    }
  }

  def run_cl_sgemm_tiled_vectorized(min: Int, max: Int, times: Int, tileSize: Int, width: Int): Unit = {
    log(s"CL SGEMM TILED VECTORIZED - $DEVICE")
    log(s"min=$min max=$max times=$times tileSize=$tileSize width=$width")
    log("N\tIteration\tTime\tGFlops")

    var n = min
    while (n <= max) {
      for {
        a <- managed(CLMat(rand(n, n)))
        b <- managed(CLMat(rand(n, n)))
        c <- managed(CLMat(n, n))
      } {
        for (i <- 1 to times) {
          flip
          a.mult_tiled_vectorized(b, c, tileSize, width)
          val (gflops, time) = gflop

          val out = Array[Any](n, i, time, gflops)
          log(out.mkString("\t"))
        }
      }
      n *= 2
    }
  }

  def run_cl_sgemm_register_blocking(min: Int, max: Int, times: Int,
    tileSize: Int, width: Int, workPerThread: Int): Unit = {

      log(s"CL SGEMM TILED VECTORIZED REGISTER BLOCKING - $DEVICE")
      log(s"min=$min max=$max times=$times tileSize=$tileSize width=$width workPerThread=$workPerThread")
      log("N\tIteration\tTime\tGFlops")

      var n = min
      while (n <= max) {
        for {
          a <- managed(CLMat(rand(n, n)))
          b <- managed(CLMat(rand(n, n)))
          c <- managed(CLMat(n, n))
        } {
          for (i <- 1 to times) {
            flip
            a.mult_2d_register_blocking_vectorized(b, c, tileSize, width, workPerThread)
            val (gflops, time) = gflop

            val out = Array[Any](n, i, time, gflops)
            log(out.mkString("\t"))
          }
        }
        n *= 2
      }
  }

  def run_cl_sgemm_clblast(min: Int, max: Int, times: Int, opts: Map[String, Int]): Unit = {
    log(s"CL SGEMM CLBLAST - $DEVICE")
    val opts_str = opts map { case (k, v) => s"$k=$v" } mkString " "
    log(s"min=$min max=$max times=$times $opts_str")
    log("N\tIteration\tTime\tGFlops")

    var n = min
    while (n <= max) {
      for {
        a <- managed(CLMat(rand(n, n)))
        b <- managed(CLMat(rand(n, n)))
        c <- managed(CLMat(n, n))
      } {
        for (i <- 1 to times) {
          flip
          a.mult_clblast(b, c, opts)
          val (gflops, time) = gflop

          val out = Array[Any](n, i, time, gflops)
          log(out.mkString("\t"))
        }
      }
      n *= 2
    }
  }

  def run_cl_sgemm_adreno(min: Int, max: Int, times: Int, localSize: Int): Unit = {
    log(s"CL SGEMM ADRENO - $DEVICE")
    log(s"min=$min max=$max times=$times localSize=$localSize")
    log("N\tIteration\tTime\tGFlops")

    var n = min
    while (n <= max) {
      for {
        a <- managed(CLMat(rand(n, n)))
        b <- managed(CLMat(rand(n, n)))
        c <- managed(CLMat(n, n))
      } {
        for (i <- 1 to times) {
          flip
          a.mult_adreno(b, c, localSize)
          val (gflops, time) = gflop

          val out = Array[Any](n, i, time, gflops)
          log(out.mkString("\t"))
        }
      }
      n *= 2
    }
  }

  def run_cl_transpose(min: Int, max: Int, times: Int, tileSize: Int): Unit = {
    log(s"CL TRANSPOSE - $DEVICE")
    log(s"min=$min max=$max times=$times tileSize=$tileSize")
    log("N\tIteration\tTime\tGFlops")

    var n = min
    while (n <= max) {
      for {
        a <- managed(CLMat(rand(n, n)))
        a_t <- managed(CLMat(n, n))
      } {
        for (i <- 1 to times) {
          flip
          Mat.nflops += 1L * n * n
          a.t(a_t, tileSize)
          val (gflops, time) = gflop

          val out = Array[Any](n, i, time, gflops)
          log(out.mkString("\t"))
        }
      }
      n *= 2
    }
  }

  def run_cl_create_free_buffer(min: Int, max: Int, times: Int): Unit = {
    log(s"CL CREATE FREE BUFFER - $DEVICE")
    log(s"min=$min max=$max times=$times")
    log("N\tIteration\tTime\tGFlops")

    var n = min
    while (n <= max) {
      val a = rand(n, n)
      for (i <- 1 to times) {
        flip
        for {
          a_ <- managed(CLMat(a))
        } {
          Mat.nflops += 1L * n * n
        }
        val (gflops, time) = gflop

        val out = Array[Any](n, i, time, gflops)
        log(out.mkString("\t"))
      }
      n *= 2
    }
  }

}
