dwolla-scala-sdk
================
[![Build Status](https://travis-ci.org/coreyjonoliver/dwolla-scala-sdk.png?branch=master)](https://travis-ci.org/coreyjonoliver/dwolla-scala-sdk)

> Scala [Dwolla API](http://developers.dwolla.com/dev) wrapper

## Using with SBT
_dwolla-scala-sdk_ is available on the [Maven Central](http://www.sonatype.org/central) repository. Simply use the following:

```scala
libraryDependencies += "com.dwolla" % "dwolla-scala-sdk" % "1.0.0"
```

A snapshot is also available:

```scala
resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.dwolla" % "dwolla-scala-sdk" % "1.0.1-SNAPSHOT"
```

## Usage
_dwolla-scala-sdk_ uses Akka and Spray to make asynchronous requests. This requires defining a few
implicits of the following types:

```scala
implicit val system = ActorSystem()
implicit val timeout: Timeout = 1.minutes
implicit val ec = ExecutionContext.global
```

Next create an instance of `DwollaSdk`:

```scala
val dwollaSdk = new DwollaSdk()
```

Now call methods on the instance of `DwollaSdk` as desired:

```scala
val createTransactionFuture = dwollaSdk.Transaction.create(accessToken, pin, "812-713-9234", .01)
```
## Versioning
_dwolla-scala-sdk_ uses [Semantic Versioning](http://semver.org/).

## License
Apache 2 - See [LICENSE](http://github.com/coreyjonoliver/dwolla-scala-sdk/blob/master/LICENSE)