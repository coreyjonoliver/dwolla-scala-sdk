## 1.1.2 (unreleased)

NEW FUNCTIONALITY:

  - Add support for withdraw endpoint.

BUG FIXES:

  - Specify the type of the `id` parameter for `DwollaSdk.FundingSource.retrieve` as `String` instead of `Int`.
  - Change return type of `DwollaSdk.FundingSource.all` from `Future[List[Transaction]]` to `Future[Seq[Transaction]]`.