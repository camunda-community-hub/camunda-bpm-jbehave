Scenario: Manual processing with errors

GivenStories: ManualFallback_NotProcessable.story
When the contract is processed manually with errors
Then the contract processing is cancelled
And the process is finished with event event_processing_cancelled
