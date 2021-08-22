package org.liamjd.kotless.html

import kotlinx.html.*

/**
 * Renders and HTML div element with the class "container"
 *
 * @param classes additional CSS classes to add to the div
 */
@HtmlTagMarker
inline fun FlowContent.container(classes: String? = null, crossinline block: DIV.() -> Unit = {}) {
	val classString = "container" + if (classes != null) {
		" $classes"
	} else {
		""
	}
	DIV(attributesMapOf("class", classString), consumer).visit(block)
}

/**
 * A navigation panel
 * @param panelTitle the name of the panel
 * @param panelContent the block of content to display in the panel
 */
fun FlowContent.panel(panelTitle: String, panelContent: NAV.() -> Unit) {
	nav(classes = "panel") {
		p(classes = "panel-heading") { +panelTitle }
		panelContent()
	}
}
