# Scala functional error handling

Exceptional is a ratio, with enough calls it happens. 

<https://dictionary.cambridge.org/dictionary/english/exceptional>

"unusual; not what happens regularly or is expected"

I worked in an environment where no exceptions were thrown as their definition of exceptional was way too stringent.

"Exceptions should be exceptional" just means the less common path.

An exception/error communicates that call had a problem but does not have the opinion of how it should be dealt with
so the error is caught somewhere it can be dealt with, for example, maybe it can be retried on certain error types.
Though not having the error in the signature means we have to reason about potentially complex call chains. Type signatures
are supposed to help with that and is one of the things that attracts people to functional programming, less just making things
happen, more can we reason about what can happen as simply as possible.

You are on a beach in the pacific, it is late evening, the ocean looks beautiful. Do you think "Lets go for a swim" or 
do you think this is the time sharks like to feed and better not go in the water. I think of sharks. Looking at code
can be a similar thing, what lies beneath?

## A few examples of types or Exception you will face

* Network - Very likely, can be DNS, routing, invalid json response structures (high chance as people are clumsy with contracts), etc.
* Disc - Less likely, though things like permissions can massively increase the chance.
* Processor/Memory - Unlikely, will come from hardware failure, solar flares, etc.

## Brief history or error handling

### Error codes/simple response type like Boolean

Error codes can simply be non-zero numbers which then can be referred to. Errors don't have the context or what caused them
so in a nice world, there is logging where the response was calculated with the context.

Sometimes a simple boolean is returned. Again, not informative, not nice to debug.


### Checked Exceptions

A lot of discussion conflates using typed errors in functional programming with checked exceptions. Checked exceptions
have the flaw of making the easy path the bad path. Checked exceptions are a nice idea, just too hard work in Java for most.

Here we just eat them in an empty try catch. A common example of what people faced.

```java
package io.github.pbyrne84.errorhandling;

import java.rmi.AccessException;
import java.rmi.ConnectException;

public class SloppyJavaCheckedExceptionHandling {


    // We have no control of what calls this method, we cannot change the exception signature. We have not stated there
    // is one. Everything that calls this assumes that there is likely RuntimeExceptions. Sharks swimming under the surface
    // of the sea.
    //
    // Java is very verbose, creating the ideal of having checked exceptions with class hierarchies ultimately fell short of
    // the nirvana fallacy. When discussing things like this, we always have to take into account the nuance of implementation
    // as it can be the implementation causing frustration negatively infecting the concept of the idea.
    public void run(){
        try {
            checkedExceptionMethod1();
            checkedExceptionMethod2();
        } catch (AccessException | ConnectException e) {
            // Do logging or something later. (Later never comes)
            // we could just throw a runtime exception at this point with the cause in it if we are nice.
            // We are nice, aren't we?
        }
    }

    // pretend we have no control of these signatures, so they have to be AccessException etc.
    public void checkedExceptionMethod1() throws AccessException {
        throw new AccessException("sss");
    }

    public void checkedExceptionMethod2() throws AccessException, ConnectException {
        if(true){
            throw new AccessException("sss");
        }else {
            throw new ConnectException("sass");
        }
    }
}

```

### Non-Checked Exceptions (extend of RuntimeException)

As detailed in the further reading, this can partly be linked to Robert C Martins clean code book. It is very good book
that goes into the reasoning. It is not really ideological, more effort-based.



```java
package io.github.pbyrne84.errorhandling;

import java.rmi.AccessException;
import java.rmi.ConnectException;

public class SloppyJavaRuntimeExceptionHandling {


    // Just wrap it all in RunTime
    public void run() {
        checkedExceptionMethod1();
        checkedExceptionMethod2();

    }

    // These are api calls that hide there errors from the signature
    public void checkedExceptionMethod1() {
        try {
            throw new AccessException("sss");
        } catch (AccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkedExceptionMethod2() {
        try {
            if (true) {
                throw new AccessException("sss");
            } else {
                throw new ConnectException("sass");
            }
        } catch (AccessException | ConnectException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Scala with Future[Either[A,B]] and EitherT, also ZIO without the cruft

EitherT creates a lot of noise due to the implementation requirements. It doesn't detect that exceptions fit a hierarchy
so requires explicit types added.

ZIO by design does not have this problem, so it is worth knowing how it would be done in an IO library that was designed
around having typed errors.


```scala
package io.github.pbyrne84.errorhandling

import cats.data.EitherT
import zio.ZIO

import scala.concurrent.Future

// Use an abstract class as it allows a constructor in scala 2.13, scala 3 has trait params.
// You can use traits in scala 2.12 but you have to override getMessage etc. and not doing
// it right breaks logging and people don't really notice until someone goes to the logs
// finding missing messages and causes whoever is supporting to be unable to resolve the issue easily.
//
// Always care about the people who support the application as it can get very sucky for them if we don't
sealed abstract class ExampleError(message: String, maybeCause: Option[Throwable] = None)
    extends RuntimeException(message, maybeCause.orNull)

case class ExampleNetworkError(message: String, cause: Throwable) extends ExampleError(message, Some(cause))
case class ExampleDatabaseError(message: String, cause: Throwable) extends ExampleError(message, Some(cause))

//
case class ExampleInvalidParamError(message: String) extends ExampleError(message)

class ErrorHandlngExample {

  // Don't use this one in production
  import scala.concurrent.ExecutionContext.Implicits.global

  import cats.implicits._

  /**
    * EitherT with future example
    *
    * From the signature we can determine we are only going to get 3 categories of failure.
    */
  def futureExample1(): Future[Either[ExampleError, Boolean]] = {
    // EitherT's is not very good at working out that the two errors are related
    // ExampleNetworkError
    // ExampleDatabaseError
    // Are children off ExampleError hence we can cast noisily
    (for {
      _ <- EitherT[Future, ExampleError, Boolean](Future(call1())) // boilerplate tastic, every line?
      _ <- Future(call2()).asEitherT
      result <- eitherT(Future(call3(3)))
    } yield result).value // Flips from EitherT[Future,ExampleError, _] to Future[Either[ExampleError,_]]
  }

  // Extension method approach -
  // A <: ExampleError means B will be child of ExampleError
  // B - result type
  implicit class FutureOps[A <: ExampleError, B](value: Future[Either[A, B]]) {

    def asEitherT: EitherT[Future, ExampleError, B] =
      EitherT[Future, ExampleError, B](value)

  }

  // method call approach, give a better name :)
  private def eitherT[A <: ExampleError, B](value: Future[Either[A, B]]) = {
    EitherT[Future, ExampleError, B](value)
  }

  private def call1: Either[ExampleNetworkError, Boolean] = {
    ???
  }

  private def call2: Either[ExampleDatabaseError, Boolean] = {
    ???
  }
  
  private def call3(value: Int): Either[ExampleInvalidParamError, Boolean] = {
    if (value < 4) {
      // Have error with message signifying the value that caused the issue
      // Built in library exceptions have low guarantee of doing this as it could
      // leak security info in exceptions. We need the value to repeat the error and resolve the issue
      // easily.
      Left(ExampleInvalidParamError(s"value $value is below 4"))
    } else {
      Right(true)
    }
  }

  /**
    * Example to show that the EitherT noise is not an inherit Scala thing but we have to dance around the limitations
    * of Future[_] or Cats Task[_] not having typed errors in their design, this leads to a lot of noise, the following
    * code can detect the inheritance of the errors correctly so no monkey dancing.
    * @return
    */
  def zioExample(): ZIO[Any, ExampleError, Boolean] = {
    for {
      _ <- ZIO.fromEither(call1())
      _ <- ZIO.fromEither(call2())
      _ <- ZIO
        .attempt(throw new RuntimeException("eeek")) // returns the equivalent of Task[_], Future[_]
        .mapError(error => ExampleDatabaseError("eek caused an error", error)) // Make the error type safe easily
      result <- ZIO.fromEither(call3(4))
    } yield result
  }
}


```



## Further reading

It is worth knowing where the things we talk about come from. This allows us to dig into the nuance later, so we don't fall into 
a cargo cult and treat things glibly. Mental flow states rely on internal flow charting/self automation. We can improve this self-automation 
by better understanding, which allows us to create much better software in the same or faster amount of time.

Quite often we are slowed down more by thinking than typing. Ideally, we can get to the point where we can think about concepts fast
as we get more experienced and teach people those concepts in a way they can also do it fast. 

For example, quite often just getting used to things like different types of mental collation such as ADT/Error Structures before we start typing.
Thinking about error cases also helps us design systems better if we care about highly communicative failure. Engineering is about
how and when things fail, else not fun things happen to people who have to interact with what we create.

Knowledge, when handed down to people can mutate from the original intent. Much like a game of telephone.

### JOEL ON SOFTWARE—Exceptions (from 2003)
(I like Joel a lot, though I don't agree with everything as he was against developers writing tests, hence StackOverflow has likely only just started 
adding them <https://stackoverflow.blog/2022/07/04/how-stack-overflow-is-leveling-up-its-unit-testing-game/>)

Points 1 and 2 are dealt with by Either. Future.failed behaves like he examples
https://www.joelonsoftware.com/2003/10/13/13/#:~:text=The%20reasoning%20is%20that%20I,invisible%20in%20the%20source%20code.

Unchecked exceptions do behave like a GOSUB return more than a GOTO. A GOTO has knowledge of destination, GOSUB returns to caller 
destination much like going to a matching catch statement :)

The Joel test is still pretty relevant today <https://www.joelonsoftware.com/2000/08/09/the-joel-test-12-steps-to-better-code/>. Having to 
ask about whether the company uses version control. Can you imagine turning up on your first day and they don't?

### Robert C Martin - Clean Code
In the clean code book by Robert C Martin (<https://www.goodreads.com/book/show/25806438-clean-code?from_search=true&from_srp=true&qid=7NgnBntSxs&rank=1>)
goes into the journey of how checked exceptions were mostly given up on in Java due to the effort. The effort to ideological benefit
meant people cut corners/just wrapped everything in RuntimeExceptions to get around it. 

Also, there is some argument that checked exceptions break the open closed principle as the exception has to be added to 
each method above where it is thrown until it is caught. Though working with IO there is always going to be errors raised,
I personally believe having them in a signature helps with reasoning about what can go wrong and communicating the context
when things go wrong. We can also organise retry strategies based on certain types of errors. 

Doing a catch/recover action multiple levels above on a certain type of exception appearing out of nowhere is quite fragile, 
we have actually tied ourselves to implementation across multiple levels of call and if we want to prove that handling 
we have to try and trigger that event. The deeper the call chain, the more difficult setup this may require and with
that flakiness of the test.

The definition of robust changes when we move out of pure computation and an unsatisfying amount of other
people's time can be wasted if we do not care more about the unhappy cases. There are some good notes in that chapter 
of the book about error messages etc.

### John De Goes - BiFunctor IO

(BiFunctor - left.map and map. Bifunctor has a very complex description, but we can just think of as Either)

<https://degoes.net/articles/bifunctor-io>

ZIO by design favours allowing concise errors in signatures. It also has a lot of convenience methods that cut the cruft. It also makes 
open telemetry very easy so also worth using for that.

### Alexandru Nedalcu - A conflation of checked exceptions and typed errors in IO
<https://alexn.org/blog/2018/05/06/bifunctor-io/>





