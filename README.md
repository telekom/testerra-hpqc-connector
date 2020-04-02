# Testerra HPQC connector

Allows synchronizing of test results to Quality Center.

## Usage

Add to your `build.gradle`
```groovy
dependencies {
    testImplementation 'eu.tsystems.mms.tic.testerra:qc11-connector:1-SNAPSHOT'
}
```

## Publishing

Create a `gradle.properties` file with the following content.
```properties
systemProp.deployUrl=https://example.com
systemProp.deployUsername=user
systemProp.deployPassword=password
```
and run
```shell script
gradle publish
```
or pass then properties via. CLI
```shell script
gradle publish -DdeployUrl=https://example.com -DdeployUsername=user -DdeployPassword=password
```
