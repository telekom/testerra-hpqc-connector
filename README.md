# HPQC connector

This module provides an automatic test result synchronization to HP Application Lifecycle Management, former called HP
QualityCenter. The module will register automatically by using `ModuleHook`.

---- 

## Releases

* Latest Release: `1.0-RC-5`

## Requirements

* Testerra in Version `1.0-RC-15`

## Usage

Include the following dependency in your project.

Gradle:

````groovy
implementation 'eu.tsystems.mms.tic.testerra:qc11-connector:1.0-RC-5'
````

Maven:

````xml

<dependency>
    <groupId>eu.tsystems.mms.tic.testerra</groupId>
    <artifactId>qc11-connector</artifactId>
    <version>1.0-RC-5</version>
</dependency>
````

## Synchronization

The easiest way to setup your project for automatic synchronization is by adding a `qcconnection.properties` file to
your `/src/test/resources/` directory.

````properties
qc.connection.server=
qc.connection.domain=
qc.connection.project=
qc.connection.user=
qc.connection.password=
qc.sync.active=true
qc.test.failed.upload.screenshots=true
qc.upload.videos=true
qc.test.failed.upload.videos=true
````

Basically the synchronization will work by two explicit annotations that can be set.

### Annotated class mode

To enable synchronization you should add the annotation `QCTestset` to your class containing the test methods. The given value
should match the complete path of Quality Center or Application Lifecycle Management test set, for an example see code snipped
below:

````java

@QCTestset("\\Root\\My\\Full\\Path\\TestSet")
public class CorrectClassAnnotationTest extends TesterraTest {

    @org.testng.annotations.Test
    public void testMethodPass() {
        Assert.assertTrue(true);
    }
}
````

This little snippet will search for a test set called `\\Root\\My\\Full\\Path\\TestSet`. If found, the method name will be extracted
and searched as test name in QC/ALM. If found, the result will be synchronized to this test case.

### Annotated test method mode

While the class annotation is necessary, the method annotation is not. In case, you don't use the method names as test names you can
set the `QCTestname` at each test method. The hpqc-connector will then lookup the given value as test name instead of the method
name itself.

````java

@QCTestset("\\Root\\My\\Full\\Path\\TestSet")
public class CorrectClassAnnotationTest extends TesterraTest {

    @org.testng.annotations.Test
    @QCTestname("Pass_Test_01")
    public void testMethodPass() {
        Assert.assertTrue(true);
    }
}
````

## Properties

|Property|Default|Description|
|---|---|---|
|qc.sync.active|true|Enables synchronization fo test results|
|qc.connection.server| |URI of ALM / QC server|
|qc.connection.user| |User to use for synchronization|
|qc.connection.password| |Password of user used for synchronization|
|qc.connection.domain}| |Domain of user to log in|
|qc.connection.project| |Project of user to log in|
|qc.version|12|Version of Quality Center or ALM 11, 12 or higher|
|qc.field.mapping.testrun| |Customize field-value mapping for synchronize properties to the quality center testrun. Use the format key:value&#124;key2:value2 for multiple values.|
|qc.upload.screenshots.off|false|Disabled the upload of screenshots globally|
|qc.test.failed.upload.screenshots|false|Upload screenshots in case of test failure|
|qc.test.passed.upload.screenshots|false|Upload screenshots in case of test successful|
|qc.upload.videos|false|Enable video upload|
|qc.test.failed.upload.videos|false|Upload videos in case of test failure|
|qc.test.passed.upload.videos|false|Upload videos in case of test successful|
|qc.test.execution.filter|false|Filter test cases in QC/ALM test set by status to determine it should be executed. <br> Value can be <br>- exclude:status:passed <br> - include:status:failed <br>- or other in format "include/exclude" + "status" + <explicit name of status>|

---

## Publication

### ... to a Maven repo

```sh
gradle <module>:publishToMavenLocal
```

or pass then properties via. CLI

```sh
gradle <module>:publish -DdeployUrl=<repo-url> -DdeployUsername=<repo-user> -DdeployPassword=<repo-password>
```

Set a custom version

```shell script
gradle <module>:publish -DmoduleVersion=<version>
```

### ... to Bintray

Upload and publish this module to Bintray:

````sh
gradle bintrayUpload -DmoduleVersion=<version> -DBINTRAY_USER=<bintray-user> -DBINTRAY_API_KEY=<bintray-api-key>
```` 
