# AWS Serverless Java Archetype
This is an Apache Maven Archetype to create a simple template for Serverless Java Application. It also creates AWS Serverless Application Model (AWS SAM) template to deploy the application to AWS.

## Usage

### Install Archetype on your machine (One time operation)
```
git clone https://github.com/awslabs/aws-serverless-java-archetype
cd aws-serverless-java-archetype
mvn clean install
```

### Create a new application
You can use normal way to create a project from Maven archetype. CLI example is shown below:

```
cd /path/to/project_home
mvn archetype:generate \
  -DarchetypeGroupId=com.amazonaws.serverless.archetypes \
  -DarchetypeArtifactId=aws-serverless-java-archetype \
  -DarchetypeVersion=1.0.0 \
  -DarchetypeRepository=local \ # Forcing to use local maven repository
  -DinteractiveMode=false \ # For batch mode
  
  # You can also specify properties below interactively
  -DgroupId=YOUR_GROUP_ID \
  -DartifactId=YOUR_ARTIFACT_ID \
  -Dversion=YOUR_VERSION \
  -DclassName=YOUR_CLASSNAME
```

### Verify, Build and Deploy your first application
The generated `pom.xml` is configured to use AWS SAM Local to verify, and AWS CLI to build and deploy the application. In order to do them, you need to install tools below:

- [AWS Command Line Interface (AWS CLI)](https://docs.aws.amazon.com/cli/latest/userguide/installing.html)
- (Optional) [AWS SAM Local](https://github.com/awslabs/aws-sam-local#installation)
    - Also, you need to install Docker as well. Follow the instruction above.

First, but optionally, you can try to invoke your function locally by the following command:

```
mvn -P invoke verify

[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------------< com.riywo:foo >----------------------------
[INFO] Building foo 1.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
...
[INFO]
[INFO] --- maven-jar-plugin:3.0.2:jar (default-jar) @ foo ---
[INFO] Building jar: /private/tmp/foo/target/foo-1.0.jar
[INFO]
[INFO] --- maven-shade-plugin:3.1.0:shade (default) @ foo ---
[INFO] Including com.amazonaws:aws-lambda-java-core:jar:1.2.0 in the shaded jar.
[INFO] Minimizing jar com.riywo:foo:jar:1.0
[INFO] Minimized 20 -> 16 (80%)
[INFO] Replacing /private/tmp/foo/target/lambda.jar with /private/tmp/foo/target/foo-1.0-shaded.jar
[INFO]
[INFO] --- exec-maven-plugin:1.6.0:exec (sam-local-invoke) @ foo ---
2018/04/04 14:20:52 Successfully parsed template.yaml
2018/04/04 14:20:52 Connected to Docker 1.37
2018/04/04 14:20:52 Fetching lambci/lambda:java8 image for java8 runtime...
java8: Pulling from lambci/lambda
Digest: sha256:14df0a5914d000e15753d739612a506ddb8fa89eaa28dcceff5497d9df2cf7aa
Status: Image is up to date for lambci/lambda:java8
2018/04/04 14:20:54 Invoking com.riywo.Example (java8)
2018/04/04 14:20:54 Decompressing /tmp/foo/target/lambda.jar
2018/04/04 14:20:54 Mounting /private/var/folders/x5/ldp7c38545v9x5dg_zmkr5kxmpdprx/T/aws-sam-local-1522819254980958624 as /var/task:ro inside runtime container
START RequestId: 31c4cfb1-8d5a-4058-b3de-4c92573f0484 Version: $LATEST
END RequestId: 31c4cfb1-8d5a-4058-b3de-4c92573f0484
REPORT RequestId: 31c4cfb1-8d5a-4058-b3de-4c92573f0484	Duration: 86.56 ms	Billed Duration: 100 ms	Memory Size: 128 MB	Max Memory Used: 7 MB

{"greetings":"Hello Tim Wagner."}


[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 10.034 s
[INFO] Finished at: 2018-04-04T14:20:57+09:00
[INFO] ------------------------------------------------------------------------
```

The maven goal invokes `sam local invoke -e event.json`, so you can see the sample output to greet Tim Wagner. You might want to modify `event.json` for a sample event to your application.

To deploy this application to AWS, you need an Amazon S3 bucket to upload your package. You can use the following command to create a bucket if you want:

```
aws s3 mb s3://YOUR_BUCKET --region YOUR_REGION
```

Now, you can deploy your application by just one command!

```
mvn deploy \
  -DawsRegion=YOUR_REGION \
  -Ds3Bucket=YOUR_BUCKET \
  -DstackName=YOUR_STACK_NAME

[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------------< com.riywo:foo >----------------------------
[INFO] Building foo 1.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
...
[INFO]
[INFO] --- exec-maven-plugin:1.6.0:exec (sam-package) @ foo ---
Uploading to aws-serverless-java/com.riywo:foo:1.0/7f77d676053976356674b90a5575f6c3  9552 / 9552.0  (100.00%)
Successfully packaged artifacts and wrote output template to file target/sam.yaml.
Execute the following command to deploy the packaged template
aws cloudformation deploy --template-file /private/tmp/foo/target/sam.yaml --stack-name <YOUR STACK NAME>
[INFO]
[INFO] --- maven-deploy-plugin:2.8.2:deploy (default-deploy) @ foo ---
[INFO] Skipping artifact deployment
[INFO]
[INFO] --- exec-maven-plugin:1.6.0:exec (sam-deploy) @ foo ---

Waiting for changeset to be created..
Waiting for stack create/update to complete
Successfully created/updated stack - archetypeTest
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 39.332 s
[INFO] Finished at: 2018-04-04T14:37:33+09:00
[INFO] ------------------------------------------------------------------------
```

You can also specify these properties by modifying `pom.xml`.

The model of your application is AWS SAM. See more details on [AWS SAM documentation](https://docs.aws.amazon.com/lambda/latest/dg/serverless_app.html).

## Options

This archetype has some options for creating variants of project.

### Handler
Lambda Java 8 runtime has some types of handlers: POJO, Simple type and Stream. The default option of this archetype is POJO style, which requires to create request and response classes, but they are baked by the archetype by default. If you want to use other type of handlers, you can use `handlerType` property like below:

```
## POJO type (default)
mvn archetype:generate \
 ...
 -DhandlerType=pojo

## Simple type - String
mvn archetype:generate \
 ...
 -DhandlerType=simple

### Stream type
mvn archetype:generate \
 ...
 -DhandlerType=stream
```

See [documentation](https://docs.aws.amazon.com/lambda/latest/dg/java-programming-model-req-resp.html) for more details about handlers.

### Logging
Also, Lambda Java 8 runtime supports two types of Logging class: Log4j 2 and LambdaLogger. This archetype creates LambdaLogger implementation by default, but you can use Log4j 2 if you want:

```
## LambdaLogger (default)
mvn archetype:generate \
 ...
 -Dlogger=lambda

## Log4j 2
mvn archetype:generate \
 ...
 -Dlogger=log4j2
```

If you use LambdaLogger, you can delete `./src/main/resources/log4j2.xml`. See [documentation](https://docs.aws.amazon.com/lambda/latest/dg/java-logging.html) for more details.

## Development

You can run an Integration Test fot this archetype by:

```
mvn clean install archetype:integration-test
```
