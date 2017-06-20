Chop
====

An easy and efficient logging library for Java & Android (inspired by [JakeWharton's Timber](https://github.com/JakeWharton/timber))

## Installation?
```groovy
buildscript {
  repositories { jcenter() }
  dependencies {
    compile 'com.episode6.hackit.chop:chop-core:0.1.9'
  }
}
```

Optionally, add a dependency on one of the specialized chop modules
- `chop-android`
  - Provides the `AndroidDebugTree` that spits out logs to the appropriate android `Log.*()` method
- `chop-groovy`
  - Provides the `GroovyDebugTagger` that should replace the default Tagger when chopping from a groovy project or library.
- `chop-junit`
  - Provides the `ChopRule` TestRule to add logging to your unit tests.

## How do I use it?
This should feel very familiar if you've used [Timber](https://github.com/JakeWharton/timber)

Plant a `Chop.Tree` as early as possible in (the debug build of) your application...
```java
// Android example
public MyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Chop.plantTree(new AndroidDebugTree());
  }
}
```

Chop logs...
```java
// simple log (tags are auto-created based on class name)
Chop.v("My Log");

// throwables
Chop.e(myThrowable, "My Log");

// formatting
Chop.d("My Formatted Log: %s, %d", myString, myInt);

// both
Chop.w(myThrowable, "My Formatted Log: %s, %d", myString, myInt);

// custom tags
Chop.withTag("CUSTOMTAG").i("My Log With Custom Tag");

// refactor-friendly custom tags
Chop.withTag(MyClass.class).w("My Class-Tagged Log");
```

By default, your log messages will be tagged with the name of the class from which they originated, along with the line number where the log is fired.

## What makes it Special?
Nothing. It just fixes some things I didn't like about Timber (which is still great btw).

## What does it "fix"?
It has a better name.

## OK I'm sold! But what else?
Most importantly it makes Trees simpler. In Timber a Tree is responsible for doing absolutely everything, from creating tags, to formatting messages, to printing the logs. A Timber tree also must implement this for all 10 log methods (2 for each log level). This was too much copy-pasting for my tastes.

The `Chop.Tree` interface has only two methods to implement...

```java
public interface Tree {
  boolean supportsLevel(Chop.Level level);
  void chopLog(Chop.Level level, String tag, String message);
}
```

...making custom Trees much simpler (note: Chop.Level is an enum representing the 5 log levels). Also the `supportsLevel()` method allows Trees to declare support for just a small subset of log-levels (lets say you wanted a way to collect only error logs), and chop will do no work at all if a log method is fired without a tree planted to support that level.

The duties of creating tags and formatting messages are left to a `Chop.Tagger` and `Chop.Formatter`, respectively (I couldn't think of good names that fit the analogy).

```java
public interface Tagger {
  String createTag();
}

public interface Formatter {
  String formatLog(String message, Object... args);
  String formatThrowable(Throwable throwable);
}
```

These objects are independent of the trees and there is only one (static) default instance for each of them. They decide the tags & messages that will be delivered to the Trees. You can change the defaults or use custom instances on the fly as shown below. (If you have a whole bunch of logging AND you log in production, first of all STOP, but also you may want to use a more light weight Tagger than the default)

```java
// Change default
Chop.withTagger(myTagger)
    .andFormatter(myFormatter)
    .byDefault();

// Use custom tools on the fly
Chop.withTagger(myTagger)
    .andFormatter(myFormatter)
    .e("My Log Message");

// Create you're own adapter and use it throughout your library
public static final ChoppingToolsAdapter myChop = Chop.withTagger(myTagger)
    .andFormatter(myFormatter);

myChop.e("My custom formatted log message");
```

## License
MIT: https://github.com/episode6/chop/blob/master/LICENSE
