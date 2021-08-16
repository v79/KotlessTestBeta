package org.liamjd.kotless

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Routing.statics() {
	static {
		staticRootFolder = File("src/main/resources/static")
		default("index.html")

		static("css") {
			files("css")
		}

		static("js") {
			files("js")
		}
	}
}
