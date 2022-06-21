Feature: google search

  Scenario: search something on google
    When the user searches for "T-Systems MMS"
    Then an entry for "T-Systems Multimedia Solutions" is shown

  Scenario Outline: search many things
    When the user searches for "<searchterm>"
    Then an entry for "<searchresult>" is shown

    Examples:
      | searchterm | searchresult                                         |
      | Wikipedia  | Wikipedia, die freie Enzyklopädie                    |
      | tagesschau | Tagesschau                                           |
      | heise      | heise online - IT-News, Nachrichten und Hintergründe |
