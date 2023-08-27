package io.github.pbyrne84

object FlatOrganisation {
  trait DbActions {
    def setupUser(user: String): Unit = ???
    def getUser(user: String): Unit = ???
    def deleteUser(user: String): Unit = ???
  }

  trait WiremockActionsForServer1 {
    def resetServer1: Unit = ???
    def stubServer1Get(status: Int): Unit = ???
  }

  trait WiremockActionsForServer2 {
    def resetServer2: Unit = ???
    def stubServer2Get(status: Int): Unit = ???
  }

  trait WiremockActionsForServer3 {
    def resetServer3: Unit = ???
    def stubServer3Get(status: Int): Unit = ???
  }

  trait AwsS3Actions {
    def resetAws: Unit = ???
    def uploadToBucket(value: String) = ???
    def getEntriesInBucket: List[String] = ???
  }

  abstract class MessyBaseSpec
      extends DbActions
      with WiremockActionsForServer1
      with WiremockActionsForServer2
      with WiremockActionsForServer3
      with AwsS3Actions

  class MessyTest extends MessyBaseSpec {}

}

object BranchedOrganisation {
  class DbActions {
    def setupUser(user: String): Unit = ???
    def getUser(user: String): Unit = ???
    def deleteUser(user: String): Unit = ???
  }

  class WiremockActionsForServer1 {
    def resetServer: Unit = ???
    def stubServerGet(status: Int): Unit = ???
  }

  class WiremockActionsForServer2 {
    def resetServer: Unit = ???
    def stubServerGet(status: Int): Unit = ???
  }

  class WiremockActionsForServer3 {
    def resetServer: Unit = ???
    def stubServerGet(status: Int): Unit = ???
  }

  class AwsS3Actions {
    def resetAws: Unit = ???
    def uploadToBucket(value: String) = ???
    def getEntriesInBucket: List[String] = ???
  }

  abstract class CleanerBaseSpec {
    protected val dbActions = new DbActions
    protected val wiremockActionsForServer1 = new WiremockActionsForServer1
    protected val wiremockActionsForServer2 = new WiremockActionsForServer2
    protected val wiremockActionsForServer3 = new WiremockActionsForServer3
    protected val awsS3Actions = new AwsS3Actions
  }

  class CleanerTest extends CleanerBaseSpec {}

}
