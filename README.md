dwolla-scala-sdk
================
[![Build Status](https://travis-ci.org/coreyjonoliver/dwolla-scala-sdk.png?branch=master)](https://travis-ci.org/coreyjonoliver/dwolla-scala-sdk)

> Scala [Dwolla API](http://developers.dwolla.com/dev) wrapper

## Installation
_dwolla-scala-sdk_ is available on the [Maven Central](http://www.sonatype.org/central) repository. There is no
current release version, but a SNAPSHOT for version `1.0.0` is currently available.

If you use SBT you can include _dwolla-scala-sdk_ with:

```scala
resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.dwolla" % "dwolla-scala-sdk" % "1.0.0-SNAPSHOT"
```

## Usage
_dwolla-scala-sdk_ is easy to use.
Start by defining implicits of the following types:

```scala
implicit val system = ActorSystem()
implicit val timeout: Timeout = 1.minutes
implicit val ec = ExecutionContext.global
```

Next create an instance of `SprayClientDwollaSdk`:

```scala
val dwollaClient = new SprayClientDwollaSdk()
```

Now call methods on the instance of `SprayClientDwollaSdk` as desired:

```scala
dwollaClient.getTransactionDetails(accessToken, 3983417)
```

## License
Apache 2 - See [LICENSE](http://github.com/coreyjonoliver/dwolla-scala-sdk/blob/master/LICENSE)