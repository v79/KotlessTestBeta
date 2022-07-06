package org.liamjd.kotless

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.routing.*
import kotlinx.html.*
import org.liamjd.kotless.aws.S3File
import org.liamjd.kotless.aws.S3ServiceImpl
import org.liamjd.kotless.html.container
import org.liamjd.kotless.html.heading
import org.liamjd.kotless.html.navigation
import org.liamjd.kotless.html.panel
import org.slf4j.LoggerFactory

fun Routing.homepage() {
	val logger = LoggerFactory.getLogger(this::class.java)
	val s3Service = S3ServiceImpl()

	get("/") {
		logger.info("Rendering homepage")
		val fileList = s3Service.getBucketListing("src.liamjd.org", "sources/2020/")

		call.respondHtml {
			heading("Pylon - Bascule Online")
			body {
				navigation()
				section(classes = "section") {
					container {
						div(classes = "columns") {
							div(classes = "column is-one-quarter") {
								fileListingPanel(fileList)
							}
							div(classes = "column is-three-quarters") {
								div {
									id = "metadata"
									form {
										id = "saveFormTest"
										name = "saveFormTest"
										fieldSet {
											textField(
												"form-test-title",
												"Title",
												"Page title",
												"The page title can be distinct from the file name"
											)
											textField(
												"form-test-slug",
												"Slug",
												"URL path for the page",
												"This forms part of the URL for the published web page"
											)
										}
									}
									div(classes = "control") {
										button(classes = "button is-primary") {
											onClick = "testingSavingForm();"
											+"Save"
										}
									}
								}
								div {
									id = "editor"
								}
							}
						}
					}
				}
				footer {
					p { + "Pylon Editor" }
				}

				script(
					type = ScriptType.textJavaScript,
					src = "https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js",
					content = ""
				)
				script(type = ScriptType.textJavaScript) {
					unsafe {
						raw(
							"""const editor = new toastui.Editor({
						el: document.querySelector('#editor'),
						height: '500px',
						initialEditType: 'markdown',
						previewStyle: 'tab',
						toolbarItems: [
						['heading', 'bold', 'italic']
						]
					});

					editor.getMarkdown();
					"""
						)
					}
				}
			}
		}
	}
}

private fun FlowContent.textField(
	fieldID: String,
	fieldTitle: String,
	placeholderText: String? = null,
	helpText: String? = null
) {
	div(classes = "field") {
		label(classes = "label") {
			+fieldTitle
		}
		div(classes = "control") {
			textInput(classes = "input") {
				id = fieldID
				if (placeholderText != null) {
					placeholder = placeholderText
				}
			}
		}
		if (helpText != null) {
			p(classes = "help") {
				+helpText
			}
		}
	}
}

private fun FlowContent.fileListingPanel(fileList: List<S3File>) {
	panel("Source files") {
		if (fileList.isNotEmpty()) {
			fileList.forEach { s3obj ->
//				println(s3obj)
				if (s3obj.isVirtualFolder) {
					div(classes = "panel-block has-background-light") {
						a {
							+s3obj.fileName
						}
					}
				} else {
					div(classes = "panel-block") {
						p {
							title = "${s3obj.fileSize} bytes"
							onClick = "loadMarkdownFile('${s3obj.s3key}');"
							+s3obj.fileName
						}
					}
				}
			}
		} else {
			div(classes = "panel-block") {
				p(classes = "is-warning") {
					+"No files found"
				}
			}
		}
	}
}

