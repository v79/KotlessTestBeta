package org.liamjd.kotless

import io.ktor.http.content.*
import io.ktor.routing.*
import java.io.File

fun Routing.statics() {
	static {
		staticRootFolder = File("src/main/resources/static")

		static("css") {
			files("css")
		}

		static("js") {
			files("js")
		}
	}
}
