package com.BIDMach

//Scala
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

//Android
import android.os.Bundle
import android.app.Activity
import android.util.Log

// jocl.org JOCL
import org.jocl.CL._
import org.jocl._

//BIDMach
//import edu.berkeley.bid.HelloCL
//import BIDMat.Mat
//import BIDMat.MatFunctions._
//import BIDMach.Learner
//import BIDMach.models.GLM

class MainActivity extends Activity {

  val TAG = "BIDMach_Android"

  private val program_source =
    """
    |__kernel void matrixAdd(__global const float *a,
    |                           __global const float *b,
    |                           __global float *c) {
    |  int gid = get_global_id(0);
    |  c[gid] = a[gid] + b[gid];
    |}
    """.stripMargin

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    // turn off native math libraries
    //Mat.useMKL = false

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
    Log.d(TAG, "=" * 30)
    Log.d(TAG, "BIDMach_Android")

    //val foo = new HelloCL()
    //foo.foo()

    // Enable fine grained OpenCL logging
    setLogLevel(LogLevel.LOG_DEBUGTRACE)

    // Enable OpenCL exceptions
    setExceptionsEnabled(true)

    val platform = getPlatform()
    val device = getDevice(platform, CL_DEVICE_TYPE_GPU)
    val context = createContext(platform, Array(device))
    val queue = clCreateCommandQueue(context, device, 0, null)

    val program = clCreateProgramWithSource(context, 1, Array(program_source), null, null)
    clBuildProgram(program, 0, null, null, null, null)

    val kernel = clCreateKernel(program, "matrixAdd", null)

    val sizeX, sizeY = 4
    val n = sizeX * sizeY
    val A, B, C = Array.ofDim[Float](n)
    for (i <- 0 until n) {
      A(i) = i
      B(i) = i
    }
    val A_buf = clCreateBuffer(context,
      CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
      Sizeof.cl_float * n,
      Pointer.to(A),
      null)

    val B_buf = clCreateBuffer(context,
      CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
      Sizeof.cl_float * n,
      Pointer.to(B),
      null)

    val C_buf = clCreateBuffer(context,
      CL_MEM_READ_WRITE,
      Sizeof.cl_float * n,
      null,
      null)

    clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(A_buf))
    clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(B_buf))
    clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(C_buf))

    val global_work_size = Array(n.toLong)
    val local_work_size = Array(1L)

    clEnqueueNDRangeKernel(queue, kernel, 1, null, global_work_size, local_work_size, 0, null, null)

    clEnqueueReadBuffer(queue, C_buf, CL_TRUE, 0, Sizeof.cl_float * n, Pointer.to(C), 0, null, null)

    clReleaseMemObject(A_buf)
    clReleaseMemObject(B_buf)
    clReleaseMemObject(C_buf)

    clReleaseKernel(kernel)
    clReleaseProgram(program)
    clReleaseCommandQueue(queue)
    clReleaseContext(context)
    
    // verify the result
    val epsilon = 1e-7f
    val expected = A.zip(B).map(p => p._1 + p._2)
    val passed = C.zip(expected).forall {
      case(x, y) => Math.abs(x - y) <= epsilon * Math.abs(x)
    }
    Log.d(TAG, s"Test ${if (passed) "PASSED" else "FAILED"}")
    
    // print the matrix
    C.grouped(sizeX).map { r => r.mkString(" ") }.foreach { r => Log.d(TAG, r) }

    //Log.d(TAG, "Loading Data...")

    // load the smaller, uncompressed dataset
    //val scats = loadFMat(dataPath + "scats.fmat")
    //val sdocs = loadSMat(dataPath + "sdocs.smat")

    // configure learner
    //val (mm, mopts) = GLM.learner(sdocs, scats, GLM.logistic)

    //Log.d(TAG, "Starting Training..")
    //mm.train
    //Log.d(TAG, "Finished Training!")
  }

  private def getPlatformInfo(platform: cl_platform_id, info_type: Long) = {
    val size_ptr = Array(0L)
    clGetPlatformInfo(platform, info_type.toInt, 0, null, size_ptr)
    val size = size_ptr(0)

    val buffer = Array.ofDim[Byte](size.toInt)
    clGetPlatformInfo(platform, info_type.toInt, size, Pointer.to(buffer), null)
    new String(buffer, 0, (size - 1).toInt)
  }

  private def getDeviceInfo(device: cl_device_id, info_type: Long) = {
    val size_ptr = Array(0L)
    clGetDeviceInfo(device, info_type.toInt, 0, null, size_ptr)
    val size = size_ptr(0)

    val buffer = Array.ofDim[Byte](size.toInt)
    clGetDeviceInfo(device, info_type.toInt, size, Pointer.to(buffer), null)
    new String(buffer, 0, (size - 1).toInt)
  }

  private def getPlatform() = {
    val num_platforms_ptr = Array(0)
    clGetPlatformIDs(0, null, num_platforms_ptr)
    val num_platforms = num_platforms_ptr(0)
    val platforms = Array.ofDim[cl_platform_id](num_platforms)
    clGetPlatformIDs(platforms.length, platforms, null)

    Log.d(TAG, s"Available platforms: $num_platforms")
    Log.d(TAG, "---")

    // log the available platforms
    for (platform <- platforms) {
      val name = getPlatformInfo(platform, CL_PLATFORM_NAME)
      val vendor = getPlatformInfo(platform, CL_PLATFORM_VENDOR)
      val exts = getPlatformInfo(platform, CL_PLATFORM_EXTENSIONS)

      Log.d(TAG, s"name: $name, vendor: $vendor, exts: $exts")
    }

    // return the first platform
    // TODO: make this smarter...
    platforms(0)
  }

  private def getDevice(platform: cl_platform_id, device_type: Long) = {
    val num_devices_ptr = Array(0)
    clGetDeviceIDs(platform, device_type.toInt, 0, null, num_devices_ptr)
    val num_devices = num_devices_ptr(0)

    val devices = Array.ofDim[cl_device_id](num_devices)
    clGetDeviceIDs(platform, device_type.toInt, num_devices, devices, null)

    Log.d(TAG, s"Available devices: $num_devices")
    Log.d(TAG, "---")

    for (device <- devices) {
      val name = getDeviceInfo(device, CL_DEVICE_NAME)

      Log.d(TAG, s"name: $name")
    }

    devices(0)
  }

  private def createContext(platform: cl_platform_id, devices: Array[cl_device_id]) = {
    val properties = new cl_context_properties()
    properties.addProperty(CL_CONTEXT_PLATFORM, platform)
    clCreateContext(properties, devices.length, devices, null, null, null)
  }
}
