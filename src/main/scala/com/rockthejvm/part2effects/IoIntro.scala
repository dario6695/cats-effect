package com.rockthejvm.part2effects

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import scala.io.StdIn

object IoIntro {


  // IO: something that embodies any kind of computation that has side effects
  val ourFirstIO : IO[Int] = IO.pure(42) // pure takes an argoument that should not have side effects

  val aDelayedIO :IO[Int] = IO.delay({ // this may produce some side effects
    println("I am producing an integer")
    54
  }) //running this will not print anything, it is not evaluated

//  val shouldNotDoThis :IO[Int] = IO.pure({ //this will be printed, since this will be evaluated eagerly
//    println("I am producing an integer")
//    54
//  })

  val aDelayedIO_v2 :IO[Int] = IO { //apply == delay in this case
    println("I am producing an integer")
    54
  }

  // how can we apply unsafe run?
  //1) unsafe runs

  // map flatmap
  val improvedMeaningOfLife = ourFirstIO.map(_ + 2)
  val printedMeaningOfLife = ourFirstIO.flatMap(mol => IO.delay(println(mol)))

  def smallProgram(): IO[Unit] = for {
    line1 <- IO(StdIn.readLine())
    line2 <- IO(StdIn.readLine())
    _ <- IO.delay(println(line1 + line2))
  } yield ()


  // mapN (useful to compose effects) - it combines IO effects as tuples
  import cats.syntax.apply._
  val combinedMeaningOfLife = (ourFirstIO, improvedMeaningOfLife).mapN(_ + _)
  def smallProgram_v2() : IO[Unit] =
    (IO(StdIn.readLine()), IO(StdIn.readLine())).mapN(_ + _).map(println)

  def main(args: Array[String]): Unit = {

    // "end of the world", our program will be a composition of effects
    // unsafeRunSync or unsafeRunAsync will be called once in the main class
    println(smallProgram.unsafeRunSync()) // it needs the context, the IO runtime import cats.effect.unsafe.implicits.global


  }

}
