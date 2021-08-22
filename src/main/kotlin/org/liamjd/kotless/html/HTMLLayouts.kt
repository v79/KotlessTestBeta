package org.liamjd.kotless.html

import kotlinx.html.*

fun HTML.heading(pageTitle: String) {
	head {
		meta(charset = "UTF-8")
		meta(name = "viewport", content="width=device-width, initial-scale=1")
		link()
		script(type = ScriptType.textJavaScript, src="/js/beta.js", content = "")
		styleLink("https://uicdn.toast.com/editor/latest/toastui-editor.min.css")
		styleLink("css/beta.css")

		title {
			+"$pageTitle : KotlessBeta"
		}
	}
}

fun FlowContent.navigation() {
	container {
		id = "navbar-container"
		nav(classes = "navbar") {
			role = "navbar"
			div(classes = "navbar-brand") {
				a(classes = "navbar-item") {
					href = "/"
					img(alt="logo") {
						src = "https://bulma.io/images/bulma-logo.png"
						width = "112"
						height = "28"
					}
				}
			}
		}
	}
}
