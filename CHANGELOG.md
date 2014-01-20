## 1.1.1 (unreleased)

DEPRECATIONS:

  - The `DwollaSdk.FundingSource.create` method with no `AccountType` parameter has been overloaded
  to take a parameter of `AccountType`.

NEW FUNCTIONALITY:

  - Add support for withdraw endpoint.
  - Overload `DwollaSdk.FundingSource.create` to take in a parameter of type `AccountType`.

BUG FIXES:

  - Specify the type of the `id` parameter for `DwollaSdk.FundingSource.retrieve` as `String` instead of `Int`.
  - Change return type of `DwollaSdk.FundingSource.all` from `Future[List[Transaction]]` to `Future[Seq[Transaction]]`.