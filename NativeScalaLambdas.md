# Scala Native Lambdas Using Graal, SQS and Pulumi
Full readme can be found here <https://github.com/pbyrne84/scala_native_lambda_test>

A project demonstrating how to get around the reflection issues when using graal. Java libraries can use reflection which graal cannot
calculate usage of which stops them being included in the build. Building native images helps with cold start times. This is interesting
reading on lambda memory size and startup times <https://arnoldgalovics.com/java-cold-start-aws-lambda-graalvm/>. For a 512MB image
I get the same cold start of about .5 seconds.

The project demonstrates setting up an SQS queue with the messages being passed to the lambda. The lambda then decodes the message and
logs it. It sounds simple but the trick is we can run the native agent when running the tests to get the reflection calls and then trim
the junk out after the test finishes. The native image builder will complain about the junk we need to remove versus getting no such 
method exceptions and other errors at run time.  This does mean we need to test to some degree of complete exercising but that is 
not a bad thing. Scala doesn't have a culture of using reflection as much.

The project includes :-

1. Circe for the json decoding
2. A generic SQS message decoder with examples in the tests. This allows for objects to be in the message body with little fuss.
3. Pulumi <https://www.pulumi.com/> to manage the infrastructure. I could have used terraform, I have done a lot of that so decided to try
   something more programmatic. It was easiest to use Typescript in this case.
4. A [scala_native_lambda_test_cleanser](https://github.com/pbyrne84/scala_native_lambda_test/tree/main/scala_native_lambda_test_cleanser)
   subproject that handles the cleaning of the graal configs after the tests have run. This should be published local for the other build
   sbt to work.
5. A [scala_native_lambda_test_builder](https://github.com/pbyrne84/scala_native_lambda_test/tree/main/scala_native_lambda_test_builder)
   subproject that has all the juicy stuff such as the lambda code and sqs message decoder etc.
6. The GitHub action runs tests and creates a local image which is then ran with a message passed to it as an arg.

