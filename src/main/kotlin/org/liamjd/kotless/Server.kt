package org.liamjd.kotless

import io.kotless.dsl.ktor.KotlessAWS
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.sessions.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.liamjd.kotless.aws.S3ServiceImpl
import org.slf4j.LoggerFactory

class Server : KotlessAWS() {
	private val logger = LoggerFactory.getLogger(Server::class.java)
	override fun prepare(app: Application) {


		val s3Service = S3ServiceImpl()
		app.install(CallLogging)
		app.install(ContentNegotiation) {
			json()
		}
		app.install(Sessions) {
			cookie<UserSession>("user_session") {
//				cookie.secure = true // app crashes if this is true, complains about https
			}
		}
		val httpClient = HttpClient(CIO) {
			install(JsonFeature) {
				serializer = KotlinxSerializer()
			}
		}

		app.install(Authentication) {
			oauth("aws-cognito") {
				urlProvider = { "https://kotlessbeta.liamjd.org/callback" }
				providerLookup = {
					OAuthServerSettings.OAuth2ServerSettings(
						name = "cognito",
						authorizeUrl = "https://kotless-beta-liamjd.auth.eu-west-2.amazoncognito.com/oauth2/authorize",
						accessTokenUrl = "https://kotless-beta-liamjd.auth.eu-west-2.amazoncognito.com/oauth2/token",
						requestMethod = HttpMethod.Get,
						defaultScopes = listOf("openid", "profile", "aws.cognito.signin.user.admin"),
						clientId = "49ja2064q2cne190e27ee6plnl",
						clientSecret = ""
					)
				}
				client = httpClient
			}
		}

		app.routing {
			statics()
			homepage()


			get("/logout") {
				call.sessions.clear<UserSession>()
				call.respondRedirect("/")
			}

			// the documentation suggests this should be inside the "authenticate" block, but the app fails if I do.
			// it works when not authenticated
			get("/callback") {
				println("Request for 'callback' received")
				val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
				println("principal: $principal")
				call.sessions.set(UserSession(principal?.accessToken.toString()))
				call.respondRedirect("/secret")
			}

			authenticate("aws-cognito") {
				get("/login") {
					println("Request for 'login' received")
					// Redirects to 'authorizeUrl' automatically
				}
			}


			/**
			 * Kotless doesn't support path parameters when deploying to AWS via terraform
			 * I'd have to use a PUT or POST rather than a GET, which is much less semantically pure.
			 * Or I just forget the named parameters feature and just use call.parameters. That works, I suppose.
			 */
			get("/load-markdown") {
				//TODO: secure this!
				val s3Key = call.parameters["s3Key"]
				if (s3Key != null) {
					println("Loading source file with key $s3Key")
					call.respondText { s3Service.loadTextFile("src.liamjd.org", s3Key) }
				} else {
					call.respondText { "Request for file ${s3Key} returned no result/" }
				}
			}


			post("/save-test") {
				println("Attempting to save")
//				val testForm = Json.parseToJsonElement(call.receiveText())
//				val testForm = call.receiveParameters()
				val testForm = call.receive<TestForm>()
				println(testForm)
				call.respond(status = HttpStatusCode.OK, message = "I haven't actually saved...$testForm")
			}
		}
	}
}

data class UserSession(val token: String)

@Serializable
data class UserInfo(
	val id: String,
	val name: String,
	@SerialName("given_name") val givenName: String,
	@SerialName("family_name") val familyName: String,
	val picture: String,
	val locale: String
)

@Serializable
data class TestForm(val title: String, val slug: String)
