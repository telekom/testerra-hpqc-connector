@QCTestset("Root\Testerra\QCSyncResultTests\QcSyncResultTests")
Feature: tests related to @Fails tag

  @QCTestname("T01_QcSyncResultFailed")
  Scenario: basic failing scenario
    When the user does a step
    Then it fails

  @QCTestname("T02_QcSyncResultPassed")
  Scenario: T02_QcSyncResultPassed
    When the user does a step
    Then it doesn't fails unexpectedly

  @QCTestname("T04QcSyncResultPassedSpaces")
  Scenario: T04_QcSyncResultPassedSpaces
    When the user does a step
    Then it doesn't fails unexpectedly

#  @Fails
#  Scenario: failing scenario with fails tag and no fails on step
#    Then it fails
#
#  @Fails
#  Scenario: expected failing search with Fails Annotation
#    When the user does a step
#    Then it fails
#
#  Scenario: scenario with fails on failing step implementation
#    When the user does a step
#    Then it fails expectedly
#
#  @Fails
#  Scenario: successful expected failing search
#    When the user does a step
#    Then it doesn't fails unexpectedly
