package com.rockthejvm.part2effects

import cats.effect.IO
import cats.implicits._

import scala.util.{Failure, Success, Try}


object IOErrorHandling {

  // IO: pure, delay, defer
  // further details at the end of the IoIntro class

  //create failed effects
  val aFailedCompute: IO[Int] = IO.delay(throw new RuntimeException("A FAILURE"))
  // more explicit  `
  val aFailure: IO[Int] = IO.raiseError(new RuntimeException("a proper fail"))

  // treat errors as values and store them into a failed computation (instead of throwing them)

  //handle exceptions
  val dealWithIt = aFailure.handleErrorWith{
    case _: RuntimeException => IO.delay(println("I'm still here"))
      // add more cases
  }

  // turn into an Either
  val effectAsEither: IO[Either[Throwable, Int]] = aFailure.attempt
  //redeem: transform fallure and success in one go
  val resultAsString: IO[String] = aFailure.redeem(ex => s"FAIL $ex", value => s"SUCCESS $value")
  //redeemWIth
  val resultAsEffect: IO[Unit] = aFailure.redeemWith(ex => IO(println(s"FAIL $ex")), value => IO(println(s"SUCCESS $value")))


  /**
   * Exercisex
   */
  // 1 - Construct potentially failed IOs from standard data types (Option, Try, Either)
  def option2IO[A](option: Option[A])(ifEmpty: Throwable): IO[A] = {
    option match {
      case Some(value) => IO(value)
      case None => IO.raiseError(ifEmpty)
    }
  }

  def try2IO[A](aTry: Try[A]): IO[A] = {
    aTry match {
      case Failure(exception) => IO.raiseError(exception)
      case Success(value) => IO(value)
    }
  }

  def either2IO[A](anEither: Either[Throwable, A]): IO[A] = {
    anEither match {
      case Left(error) => IO.raiseError(error)
      case Right(value) => IO(value)
    }
  }


  // 2 -  create handleError and handleErrorWith
  def handleIOError[A](io: IO[A])(handler: Throwable => A): IO[A] = {
   // io.redeem(ex => handler(ex), value => value)
    io.redeem(handler, identity)
  }
  def handleIOErrorWith[A](io: IO[A])(handler: Throwable => IO[A]): IO[A] = {
    //io.redeemWith(ex => handler(ex), value => IO.pure(value))
    io.redeemWith(handler, IO.pure)
  }

  def main(args: Array[String]): Unit = {

    import cats.effect.unsafe.implicits.global

    dealWithIt.unsafeRunSync()


  }

}
