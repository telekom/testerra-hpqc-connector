# Testerra HPQC connector

Allows synchronizing of test results to Quality Center.

## Usage

Add to your `build.gradle`
```groovy
dependencies {
    testImplementation 'eu.tsystems.mms.tic.testerra:qc11-connector:1-SNAPSHOT'
}
```

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
