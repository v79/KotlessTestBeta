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

Something I always struggle with. Trying to use AWS Cognito with a hosted UI for this, using the OAUTH standard.

- You need a Ktor HttpClient to make the authentication requests
- Install authentication and give it a name:
```kotlin
app.install(Authentication) {
    oauth("aws-cognito") {
        urlProvider = { "https://kotlessbeta.liamjd.org/callback" }
//				urlProvider = { "http://localhost:8080/callback" }
        providerLookup = {
            OAuthServerSettings.OAuth2ServerSettings(
                name = "cognito",
                authorizeUrl = "https://[[COGNITO AUTH URL]].auth.eu-west-2.amazoncognito.com/oauth2/authorize",
                accessTokenUrl = "https://[[COGITO AUTH URL]].auth.eu-west-2.amazoncognito.com/oauth2/token",
                requestMethod = HttpMethod.Get,
                defaultScopes = listOf("openid", "profile", "aws.cognito.signin.user.admin"),
                clientId = "[[AWS COGNITO APP CLIENT ID]]",
                clientSecret = "" // not sussed this out yet; I forgot to enable this when setting up the cognito user pool
            )
        }
        client = httpClient
    }
}
```
- Now, you add the `authenicate` call to the routes which _perform the authentication_, such as the `login` route:
```kotlin
authenticate("aws-cognito") {
      get("/login") {
        // Redirects to 'authorizeUrl' automatically
      }
  }
```
- Then once authenticated, Cogntio returns to the given 'callback' URL with the authorisation token and user details. Store these in a session cookie and then redirect to the "logged in" landing page (which could be /). **The callback URL should not be in an authenticate block, despite what I've seen in documentation!**
```kotlin
get("/callback") {
      val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
      call.sessions.set(UserSession(principal?.accessToken.toString()))
      call.respondRedirect("/secret")
  }
```

What all this does seem to mean is that I need to check the session for the authorization for every root which should be secure. This seems to be different from how [Osiris][https://github.com/cjkent/osiris] handles it, where each secure root is in an authentication block and the engine handles the auth checking.
