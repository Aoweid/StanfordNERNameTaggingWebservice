Feature: StanfordNERNameTaggingWebservice Acceptance

  Scenario: Successful Commit
   	  When StanfordNERNameTaggingWebservice CFT is launched
   	  Then I get CFT output
   	  Then I execute my RestFul service with empty input 
   	  Then I execute my RestFul service with 1000 transactions under 200 threads