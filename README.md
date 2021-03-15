# Testerra HPQC connector

<p align="center">
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/telekom/testerra-hpqc-connector?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/telekom/testerra-hpqc-connector?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#installation">Installation</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#development">Development</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## About this module

This module provides an automatic test result synchronization to HP Application Lifecycle Management, former called HP
QualityCenter. The module will register automatically by using `ModuleHook`.

----

## Requirements

* Testerra in Version `1.0-RC-30`

## Usage

Include the following dependency in your project.

Gradle:

````groovy
implementation 'eu.tsystems.mms.tic.testerra:qc11-connector:1.0-RC-6'
````

Maven:

````xml
<dependency>
    <groupId>eu.tsystems.mms.tic.testerra</groupId>
    <artifactId>qc11-connector</artifactId>
    <version>1.0-RC-6</version>
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

```java
@QCTestset("\\Root\\My\\Full\\Path\\TestSet")
public class CorrectClassAnnotationTest extends TesterraTest {

    @org.testng.annotations.Test
    public void testMethodPass() {
      Assert.assertTrue(true);
    }
```

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

#### ... to a Maven repo

_Preparation_

* All publish settings are located in ``publish.gradle``.
* All modules will be published.
* To prevent publishing, add the following line to the ``build.gradle`` of the module:
  ```groovy
  doNotPublish(this)
  ```

_Publishing to local repo_

```shell
gradle publishToMavenLocal
```

_Publishing to remote repo_

```shell
gradle publish -DdeployUrl=<repo-url> -DdeployUsername=<repo-user> -DdeployPassword=<repo-password>
```

_Set a custom version_
```shell
gradle publish -DttVersion=<version>
```

#### ... to GitHub

Some hints for using GitHub Packages as Maven repository

* Deploy URL is https://maven.pkg.github.com/OWNER/REPOSITRY
* As password generate an access token and grant permissions to ``write:packages`` (Settings -> Developer settings -> Personal access token)

## Contributing
Thank you for considering contributing to the Testerra framework! The contribution guide can be found here: [CONTRIBUTING.md](CONTRIBUTING.md).

## License
The Testerra framework is open-sourced software licensed under the [Apache License Version 2.0](LICENSE).

## Documentation

Check out our comprehensive [Testerra documentation](http://docs.testerra.io)!

## Code of Conduct

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.0 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

## Working Language

We decided to apply _English_ as the primary project language.  

Consequently, all content will be made available primarily in English. We also ask all interested people to use English as language to create issues, in their code (comments, documentation etc.) and when you send requests to us. The application itself and all end-user faing content will be made available in other languages as needed.


## Support and Feedback

The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **Issues**   | <a href="/../../issues/new/choose" title="Issues"><img src="https://img.shields.io/github/issues/telekom/testerra-hpqc-connector?style=flat"></a>  |
| **Other Requests**    | <a href="mailto:testerra@t-systems-mms.com" title="Email us"><img src="https://img.shields.io/badge/email-CWA%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |


## Repositories

| Repository          | Description                                                           |
| ------------------- | --------------------------------------------------------------------- |
| [testerra] | Testerra |

[testerra]: https://github.com/telekom/testerra

## How to Contribute

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors

At the same time our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (c) 2021 Deutsche Telekom AG.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
