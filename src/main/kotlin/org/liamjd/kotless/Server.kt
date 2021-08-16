package org.liamjd.kotless

import io.kotless.dsl.ktor.KotlessAWS
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.html.*
import kotlinx.html.dom.document
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.liamjd.kotless.html.heading
import org.slf4j.LoggerFactory

class Server : KotlessAWS() {
	private val logger = LoggerFactory.getLogger(Server::class.java)
	override fun prepare(app: Application) {

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

		app.install(ContentNegotiation)

		app.routing {
			statics()
			get("/fly") {
				println("Request for 'fly' received")
				val flyText = "Fly me to the moon"
				call.respondHtml(HttpStatusCode.OK) {
					document {
						heading(flyText)
						body {
							div(classes = "container") {
								h1 {
									+flyText
								}
							}
						}
					}
				}
			}
			get("/wibble") {
				println("Request for 'wibble' received")
				call.respondText { "You said wibble, I say wobble" }
			}

			// it would be a shame if I had to do this check for every route
			get("/secret") {
				val userSession = call.sessions.get<UserSession>()
				if (userSession != null) {
					println("Request for 'secret' received")
					call.respondText("You've found my secret")
				} else {
					call.respondText("Oh no you don't!")
				}
			}

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
