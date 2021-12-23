## Import to project (Maven pom.xml)
1. Add repository
```
    <repositories>
        <repository>
            <id>eFastTask</id>
            <name>eFastTask Libraries by Larry</name>
            <url>https://github.com/larry-do/mvn-repo/raw/master/</url>
        </repository>
    </repositories>
```
2. Add dependency
```
        <dependency>
            <groupId>com.efasttask</groupId>
            <artifactId>google-api</artifactId>
            <version>1.0</version>
        </dependency>
```
## How to get Google credentials.json

https://developers.google.com/workspace/guides/create-credentials
https://openplanning.net/11917/tao-credentials-cho-google-drive-api

Just need to create OAuth consent screen and OAuth 2.0 Client IDs credentials. Enable Google Drive API (and others) also
## Common errors
1. Error 400 Bad Request -> Please remove old StoredCredentials.
