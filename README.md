# Experiments with Ktor and Kotless

https://kotlessbeta.liamjd.org/

I had to build Kotless 0.2.0 locally, as I had real problems getting `terraform` to download and execute properly in Windows.

###Before you start

- you need an S3 bucket (`kotless.liamjd.org`) already created
- should have an IAM profile set up, I've just used 'default' below. It'll need appropriate permissions. It's saved in ~/.aws/credentials file. Without this, `terraform` can't execute.
- Kotless error handling isn't great, often gets lost behind stack traces

###build.gradle.kts

```kotlin
    config {
        aws {
            prefix = "kotless-beta"
            storage {
                bucket = "kotless.liamjd.org"
            }
            terraform {
                profile = "default" // IAM AWS profile name
                region = "eu-west-2"
            }
        }
  }
  ```
  
###At Amazon AWS

- The functions are accessed via an API Gateway. There's a default stage created, called **1** (can this be renamed?).
- You need the `Invoke URL` from the stage editor - format is https://_something_.execute-api._region_.amazonaws.com/1 . - can also be found as `application_url` at the end of the terrform deployment logs
- Set up a Cloudfront distribution - the origin domain is this Invoke URL, minus the https:// and the stage - AWS won't offer this as a dropdown option; it only offers the S3 buckets. Don't choose the bucket!
- Set the origin path to be the stage (e.g. `/1`)
- To tie this to your own domain, go to Route 53 and add a Simple A record directing traffic to the cloudfront domain (_something_.cloudfront.net)
  - I couldn't get the Kotless `dns` instruction to work; complained of duplicate records

###Ktor

I used this for static routes:
```kotlin
fun Routing.statics() {
	static {
		staticRootFolder = File("src/main/resources/static")
		default("index.html")
	}
}
```

###Authentication

Something I always struggle with. Trying to use AWS Cognito with a hosted UI for this.
