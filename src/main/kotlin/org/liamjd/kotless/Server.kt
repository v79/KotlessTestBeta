package org.liamjd.kotless

import io.kotless.dsl.ktor.KotlessAWS
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

class Server : KotlessAWS() {
	override fun prepare(app: Application) {
		app.routing {
			statics()
			get("/fly") {
				call.respondText { "Fly me to the moon, let me sing among the stars" }
			}
		}
	}
}
