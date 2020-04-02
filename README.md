# Testerra HPQC connector

Allows synchronizing of test results to Quality Center.

## Usage

_tdb_

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
