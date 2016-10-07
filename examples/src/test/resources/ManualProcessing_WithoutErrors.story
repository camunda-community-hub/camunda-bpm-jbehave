Scenario: Manual processing

GivenStories: ManualFallback_NotProcessable.story
When the contract is processed manually
Then the process is finished with event event_contract_processed
