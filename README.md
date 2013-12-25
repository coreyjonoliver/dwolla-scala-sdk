dwolla-sdk-scala
================
[![Build Status](https://travis-ci.org/coreyjonoliver/dwolla-sdk-scala.png?branch=master)](https://travis-ci.org/coreyjonoliver/dwolla-sdk-scala)

> Scala [Dwolla API](http://developers.dwolla.com/dev) wrapper

## Installation
_dwolla-sdk-scala_ is available on the [Maven Central](http://www.sonatype.org/central) repository. There is no
current release version, but a SNAPSHOT for version `1.0.0` is currently available.

If you use SBT you can include _dwolla-sdk-scala_ with:

```scala
libraryDependencies += "com.dwolla" %%  "dwolla-sdk-scala" % "1.0.0"
```

## Usage
_dwolla-sdk-scala is easy to use.
Start by defining implicits of the following types:

```scala
implicit val system = ActorSystem()
implicit val timeout: Timeout = 1.minutes
implicit val ec = ExecutionContext.global
```

Next create an instance of `SprayClientDwollaSdk:

```scala
val dwollaClient = new SprayClientDwollaSdk()
```

Now call methods on the instance of `SprayClientDwollaSdk` as desired:

```scala
dwollaClient.getTransactionDetails(accessToken, 3983417)
```

## License
Apache 2 - See [LICENSE](http://github.com/coreyjonoliver/dwolla-sdk-scala/blob/master/LICENSE)