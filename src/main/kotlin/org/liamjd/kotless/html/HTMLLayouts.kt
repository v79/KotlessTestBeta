package org.liamjd.kotless.html

import kotlinx.html.*

fun HTML.heading(pageTitle: String) {
	head {
		meta(charset = "UTF-8")
		meta(name = "viewport", content="width=device-width, initial-scale=1")
		styleLink("css/beta.css")
		title {
			+"$pageTitle : KotlessBeta"
		}
	}
}
