# Code Structure - Ordering for skimmability and easier PR processing

A lot of my ethos is built around trying to apply **Steve Krug's - Don't Make Me Think** to programming. We are the 
end users of each other's code, we can only expect to receive what we leave for others. What you do easily in the
morning can be a migraine for someone else in the evening. How we write software leaves a User Experience for the next
person, so we should take care of what causes unneeded thinking. The need for thinking may as well be actually endless 
on the timescales we live due to all the possibilities in the universe.

I use the term **skimmability** versus **readability** as **readability** can be interpreted as read with some degree of
effort. Reading code is not like reading a book, in a book the majority is important. When doing a change in an existing
project or verifying something someone else did (or you did in the past beyond memory limits), the majority of what is looked
at is likely not what you want. So we should organize things in a way we can say "That is not what I want" as easily as
possible and also clean enough that we can create mental markers when we hit something unrelated for future reference.

Memory is a contaminating factor when deciding something is skimmable or not. If we write something that comes from a 
mental journey, others will not have that mental journey. We organize things to communicate our mental journey. 
Communication via organization. Algebraic data types may not be needed, but they help communicate symmetry and the 
communication of symmetry helps memorization (for me anyway). Correlations help create paths to other pieces of information
and help with the rate things can be understood.

## The really really bad hangover programming technique
I use this instead of the **coding for violent psychopaths** technique

<https://blog.codinghorror.com/coding-for-violent-psychopaths/>

I think that one makes anxious people more anxious, which is not good. Possibly it can inhibit people with imposter syndrome
further. I do quite like it, but I also don't fear the psychopath. They are maligned, no sense of culpability does not
equal them having to do harm to others. Culpability can also rely on experience.

I do the mental exercise of 

```
If I woke up tomorrow with a really bad hangover/illness with no memory of what I had done the day before how fast
could I pick it up? How much of a greater headache would it cause. What would the effort be to rebuild the mental
models that allow me to work effectively?
```

That helps me empathize with the next person. They have no knowledge of my journey, they have to work it out, they
also may be having the worst day of their lives. I cannot expect thought if I do not give thought.


## We are limited by life span what we can learn and what we can communicate
Writing software is communication, we mediate from client to machine, we have to do it in a way that makes other engineers
life easy if they pop in and out of a project. A project becomes unknowable once the engineer churn rate is a shorter
period of time than the length of time it takes to understand the project. This is one of the things using microservices
is supposed to help, everyone just fixates on the technical aspects such as scaling, but it is also about being able to ring
fence-rot. 

Rot is perceived differently by different people, but monoliths have the problem of having much higher different types of
complexity due to the depth of the system. Lack of experience leads to a lot of **accidental complexities** 
<https://medium.com/background-thread/accidental-and-essential-complexity-programming-word-of-the-day-b4db4d2600d4>, trying
to clean that up in a service with a lot of engineers can become like climbing the north face of the Eiger. Microservices
keep things bite size, so there are hard boundaries things cannot bleed through.

The downside of discombobulating a system into APIs or worse side effecting AWS Lambdas is every network call breaks
the stacktrace/in memory boundary. With API's you can add Open Tracing and have structured logging quite easily depending
on the chosen technology. I have an examples for open tracing in ZIO here <https://pbyrne84.github.io/Zio2Playground.html>,
adds headers to all http calls and spans etc to the log entries. Lambda's have the problem of startup times for the JVM,
though with GraalVM we can try native lambdas with Scala demonstrated here <https://pbyrne84.github.io/NativeScalaLambdas.html>.
You also have another avenue you need to get the logs into Kibana or equivalent technologies. As logging is often 
 unloved and forgotten until things go wrong, then adding another barrier is not so good.

It sounds like I think Scala is the solution for all problems. It is based around Scala is my favourite language to write 
tests, enjoying writing tests promotes the writing of tests. It is a language where correctness can be a higher level of
fidelity easily. Higher easy fidelity, less long term headaches. I moved to Scala as much for ScalaTest as things like
functional purity due to how it allows test structuring. Tests are also communication, not just things that give us dopamine
when we get a green result.

## All programming languages are not equal, they come with different headaches
This is where I can appear contentious, and it can appear like a language war. What I promote is listening to your biology.
Noting what makes you feel tired and confused early as those things will likely just make you more tired. When we get
tired, it can negatively affect our interpersonal relations, so it is good to manage this.

Another mental exercise I used to do when starting out until things became second nature.

```
You have a project of X size, what would become problematic when it reaches 10x the size
```

Our thought processes have a cost to us, thinking about one thing blocks another thing, getting tired can both cause 
and also miss existing mistakes from code blindness. Trying to run this mental simulation can help magnify the current twinges to the
point where re-organisation can be done early, the longer things are left, the harder it is. Left long enough, the 
skill gap can be too high for a lot of people to be able to do the re-organisation.

All languages are not equal as they allow different concepts, concepts are what helps us communicate at speed. A pure 
function can elate when we say or hear the word **pure** but really a **pure** function communicates it can be trusted. ADT's
are a better version of <https://refactoring.guru/replace-type-code-with-subclasses>, also what attracted me to Scala.

Working on your own, these things are not as important as you can work from your own memory and memorization is less of a
bottleneck. Things become a problem when you start to cohabit with other engineers in a project. You could say all languages
are more or less equal if we all became a hive mind.


## Better practices are learnt from dealing with code that is not aging well
We like to experience things first hand. Dealing with code that is not aging well is a good experience, it allows the 
experience of the negative outcome without the effort needed to create the negative outcome. Usually, this takes a while,
so we may never see the negative outcomes of our approaches, and we keep repeating those approaches to the negative benefit 
of those who come after. Dealing with mud helps us not to create more mud. This is why it is important never to get into
a panel beating state of mind. Always question, taking into account easy can become a lot harder at scale. 

Ask yourself
"If I could change anything, what would I change to help me and my team go consistently faster?"

Once you know what you would like to do, if applicable, then you can start to work out how.


## Vertical ordering of code

<https://www.baeldung.com/cs/clean-code-formatting> (All of this in detail)

This comes from Clean Code by Robert C Martin. I call it "How to organise code, so you don't feel like a cat chasing a
laser pointer". This is my **number one approach to help reduce cognitive load and sore neck**. 

There is a pattern called composed method. You actually have seen it many times as like most patterns, it is naturally 
evolving, so we do it without knowing. It is based on breaking something down using private methods, so we have a concept
of overview and implementation. With Scala, doing this can help with type signatures as **EitherT** can be a bit picky
about compatibility with signatures.

The premise is that there should be an overview method (commonly public) that calls well-named private methods, so we 
can tell the business logic before deep implementation logic. If the business logic is not what concerns us, we do 
not have to look at the implementation logic. We can discard the irrelevant path we are on easily.


### Done well, we can read vertically

```scala
def add(element:Any): Either[Throwable,true] = {
  //Overview, private methods are detail. We should be able to work out if this is the thing we want from this method
  //If not we can skim past validate and grow
 for{
  _ <- validate(element)
  result <- grow(element)
 } yield result
}

// Content of these is just under so they can be read using normal vertical reading/referencing. Control click to navigate is not
// really needed, PR's are easier as we have reduced chaotic scrolling.

//Called first, so goes first
private def validate(element:Any): Either[Throwable,true] = {
 ???
}

//Called second, so goes second
private def grow(element:Any): Either[Throwable,true] = {
 ???
}

```

### Done chaotically, our neck will hurt over time, we will become more tired.

Eyes have to move all over the place to work out what is happening. **validate** just seems to float on its own meaning
you have to skim past and then maybe revisit. Cat and laser pointer.

```scala

// Above add method so we are going to have to bounce back up to read
private def validate(element:Any) : Either[Throwable,true] = {
 ???
}

def add(element:Any): Either[Throwable,true] = {
 for{
  _ <- validate(element)
  result <- grow(element)
 } yield result
}


private def grow(element:Any) : Either[Throwable,true] = {
 ???
}
```

### Using this approach aids refactoring

As the private methods are kept close and within range of each other, a lot of extract class refactorings become a lot 
easier. The first thing I do before any refactoring is re-organise to make things easier, things like to hide in the
chaos, those things can block any refactoring.


### Ordering of methods can communicate other things
What is rarely taken into account is that there is a skill barrier to cleaning up our mess, when pushed for time 
we then leave more mess. The unfortunate fact is the more experienced you become, the more problems that can be perceived
and things like formatting and code ordering help the mess be clear. A mess has to be made clear to be cleaned up.

The skill barrier for cleaning up a mess comes from not cleaning up messes. There is a concept called refactoring in 
functionality, for that any one of these refactorings can be used <https://refactoring.guru/refactoring/catalog> without 
permission as they are not re-engineering, time you take to help me save time helps me take time saving other people time.
I will do a refactoring thing later but feature envy is one major cause of mess <https://refactoring.guru/smells/feature-envy>.


#### Example of using ordering to communicate


```scala
// There is a triangle shape in calls that can be used to visually model
//
//  call1
//        childAction1
//        childAction2
//  call2
class Example {
  
  def call1() ={
    for{
      _ <- childAction1()
      _ <- childAction2()
    } yield ()
  }
  
  // childAction1 and childAction2 are closely related, shared by call1 and call2
  // childAction1 and childAction2 may be able to be extracted to their own class that can be tested
  // (are a separate responsibility requiring a bulk of dependencies),
  // especially if they have all the meat of the action as it makes testing it easier. Don't test private methods or make private
  // methods public for testing as it hinders refactoring (things can get very complicated).
  //
  // Ordered like this, just cut the methods out into a new class and add dependencies to the constructor until it stops
  // complaining.
  private def childAction1(): Option[Boolean] = {
    ???
  }

  private def childAction2(): Option[Boolean] = {
    ???
  }

  def call2() = {
    for {
      _ <- childAction1() // childAction1 is above so is a shared concept
      _ <- childAction2() // childAction2 is above so is a shared concept
    } yield ()
  }

}

```