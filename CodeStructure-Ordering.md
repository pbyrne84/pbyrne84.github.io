# Code Structure - Ordering for skimmability and easier PR processing

A lot of my ethos is built around trying to apply **Steve Krug's - Don't Make Me Think** to programming. We are the 
end users of each other's code, we can only expect to receive what we leave for others. What you do with easily in the
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

I think that one makes anxious people more anxious, which is not good. Possibly inhibit people with imposter syndrome
further. I do quite like it, but I also don't fear the psychopath. They are maligned, no sense of culpability does not
equal them having to do harm to others.

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
complexity due to depth of the system. Lack of experience leads to a lot of **accidental complexities** 
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

## All programming languages are not equal (unless you are really being glib about it to get the oxytocin going at a conference)
This is where I can appear contentious, and it can appear like a language war. What I promote is listening to your biology.
Noting what makes you feel tired and confused early as those things will likely just make you more tired.

Another mental exercise I used to do when starting out until things became second nature.

```
You have a project of X size, what would become problematic when it reaches 10x the size
```

Our thought processes have a cost to us, thinking about one thing blocks another thing, getting tired can both cause 
and miss mistakes from code blindness. Trying to run this mental simulation can help magnify the current twinges to the
point where re-organisation can be done early, the longer things are left, the harder it is.

All languages are not equal as they allow different concepts, concepts are what helps us communicate at speed. A pure 
function can elate when we say or hear **pure** but really a **pure** function communicates it can be trusted. ADT's
are a better version of <https://refactoring.guru/replace-type-code-with-subclasses>, also what attracted me to Scala.

Working on your own, these things are not as important as you can work from your own memory and memorization is less of a
bottleneck. Things become a problem when you start to cohabit with other engineers in a project. You could say all languages
are more or less equal if we all became a hive mind.


## Better practices are learnt from dealing with code that is not aging well




