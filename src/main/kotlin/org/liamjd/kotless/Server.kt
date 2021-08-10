package org.liamjd.kotless

import io.kotless.dsl.ktor.Kotless
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

class Server : Kotless() {
	override fun prepare(app: Application) {
		app.routing {
			get("/") {
				call.respondText { "Fly me to the moon, let me sing among the stars" }
			}
		}
	}
}
