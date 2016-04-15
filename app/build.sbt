import android.Keys._
import android.Dependencies.{LibraryDependency, aar}

android.Plugin.androidBuild

platformTarget in Android := "android-21"

name := "BIDMach_Android"

scalaVersion := "2.11.6"

run <<= run in Android

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jcenter" at "http://jcenter.bintray.com"
)

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
scalacOptions ++= Seq("-feature", "-deprecation", "-target:jvm-1.7")

libraryDependencies ++= Seq(
  aar("com.android.support" % "support-v4" % "21.0.3"),
  "com.jsuereth" %% "scala-arm" % "1.4",
  "org.apache.commons" % "commons-math3" % "3.2"
)

proguardScala in Android := true

proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic",
  "-keep class scala.Predef",
  "-keep class scala.scala.collection.immutable.StringOps",
  "-keep class org.jocl.** { *; }",
  "-keep class com.BIDMach.** { *; }",
  "-keep class BIDMat.** { *; }"
)
