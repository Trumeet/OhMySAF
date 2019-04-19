# Oh My SAF

Android [SAF](https://developer.android.com/guide/topics/providers/document-provider) helper.

# Implement

1. Add [maven.yuuta.moe](https://maven.yuuta.moe) repo to your module

2. Add dependency:

```grooxy
implementation 'moe.yuuta.ohmysaf:ohmysaf:<the latest version>'
```

# Main Features

* Faster migrate - If you have an old project which uses `java.io.File` a lot and you would like to migrate to SAF, OMG. By using OhMySAF, you can just continue using `File` with a little hack.

# Faster Migrate

Imagine that you have an old project and its file operation codes look like:

```java
File theFxxkFile = new File("xxx/xxx");
if (theFxxkFile.isFile()) {
    theFxxkFile.delete();
    theFxxkFile.mkdir(); // In fact, SAF does not support that.
}
```

If you want to switch to SAF, the normal code will look like:

```java
// Assume that you've got the URI
DocumentFile theFxxkFile = DocumentFile.fromTreeUri(context, uri);
if (theFxxkFile.isFile()) {
    theFxxkFile.delete();
    // .....
}
```

One day you discovered `OhMySAF`, your problem is solved:

```java
// Assume that you've got the URI

// Magic
File theFxxkFile = OhMySAF.ohMyTree(context, uri);

// Just KEEP these old codes!
if (theFxxkFile.isFile()) {
    theFxxkFile.delete();
    theFxxkFile.mkdir(); // In fact, SAF does not support that.
}
```

## Extend usages

Some APIs don't work with `SafFile`. In these cases, you should cast the returned `File` to `SafFile` and use extended APIs.

```java
SafFile theMagic = OhMySAF.ohMyUri(context, uri);
```

### Get Android Uri

It returns the Android `Uri` and you can do ANYTHING you want with it.

```java
final Uri uri = theMagic.getAndroidUri();
```

### Open input / output stream

The Java `FileInputStream` and `FileOutputStream` does not work with the file directly. Talk is cheap, let's show you the code:

```java
final OutputStream stream = theMagic.openOutputStream(this, "w");
stream.close();
```

# Licenses

Apache 2.0