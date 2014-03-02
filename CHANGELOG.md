## 1.1.0 (unreleased)

DEPRECATIONS:

  - The `DwollaSdk.FundingSource.create` method with no `AccountType` parameter has been overloaded.
  to take a parameter of `AccountType`.

NEW FUNCTIONALITY:

  - Add support for withdraw endpoint.
  - Overload `DwollaSdk.FundingSource.create` to take in a parameter of type `AccountType`.
  - Add optional parameters to `Dwolla.Transaction.all` which allow filtering of transactions by `sinceDate`, `endDate`, `types`, `limit`, 
  `skip`, and `groupId`.
  - Add optional parameters to `DwollaSdk.FundingSource.all` which allow filtering of funding sources by `destinationId` and `destinationType`.

BUG FIXES:

  - Specify the type of the `id` parameter for `DwollaSdk.FundingSource.retrieve` as `String` instead of `Int`.
  - Change return type of `DwollaSdk.FundingSource.all` from `Future[List[Transaction]]` to `Future[Seq[Transaction]]`.

## 2.0.0 (unreleased)

BACKWARDS INCOMPATIBILITY:

  - The `DwollaSdk.FundingSource.create` method with no `AccountType` parameter is removed.
