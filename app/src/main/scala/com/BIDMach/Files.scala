package com.BIDMach

import java.io.{FileInputStream, InputStreamReader, BufferedReader}
import resource.{managed, ManagedResource}

object Files {

  def fileString(file: String, newline: Boolean = false): String = {
    if (newline)
      fileLines(file).mkString("\n")
    else
      fileLines(file).mkString
  }

  def fileLines(file: String): Traversable[String] = {
    fileReader(file).map(makeBufferedReaderLineTraverser).toTraversable
  }

  def fileReader(file: String): ManagedResource[BufferedReader] = {
    managed(new BufferedReader(new InputStreamReader(new FileInputStream(file))))
  }

  private def makeBufferedReaderLineTraverser(reader: BufferedReader): TraversableOnce[String] = {
    object traverser extends Traversable[String] {
      def foreach[U](f: String => U): Unit = {
        def read(): Unit =
          reader.readLine match {
            case null => ()
            case line => f(line); read()
          }
        read()
      }
      override def toString = s"BufferedReaderLineIterator($reader)"
    }
    traverser
  }

}
