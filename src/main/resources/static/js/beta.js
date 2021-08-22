'use strict';

function loadMarkdownFile(s3key) {
    const editorDom = document.querySelector("#editor");
    fetch("/load-markdown?s3Key=" + encodeURI(s3key), {
        method: "GET"
    })
        .then(response => response.text()).then(data => updateMarkdownEditor(data));
}

function updateMarkdownEditor(data) {
    const updated = editor.setMarkdown(data);
    editor.moveCursorToStart();
    return updated;
}


function testingSavingForm() {
    let formData = new FormData();
    formData.append("title", document.getElementById("form-test-title").value);
    formData.append("slug", document.getElementById("form-test-slug").value);
    formData.keys()
    for (let key of formData.keys()) {
        console.log(key + "-> " + formData.get(key));
    }

    let json = formToJson(formData);
    console.log("Posting form data...");
    console.log(json);

    fetch("/save-test", {
        body: json,
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(response => {
        if (response.ok) {
            response.text().then(text => alert(text));
            // alert("It saved, apparently");
        } else {
            alert("Problem saving");
            console.log("Error saving: " + response.status);
        }
    });
}

/**
 * Update the HTML for the given element
 * @param domElement
 * @param html
 */
function updateElement(domElement, html) {
    domElement.innerHTML = html;
}

/**
 * Transform the formData object into a JSON string. Fields whose names end in [] are treated as an Array, even if there's only a single value
 * @param formData
 * @returns Json {string}
 */
function formToJson(formData) {
    /*  let object = {};
      formData.forEach((value, key) => {
          if (!Reflect.has(object, key)) {
              object[key] = value;
              return;
          }
          if (!Array.isArray(object[key])) {
              object[key] = [object[key]];
          }
          object[key].push(value);
      });
      let json = JSON.stringify(object);
      console.log(json);
      return json;*/
    let object = {};
    formData.forEach((value, key) => {
        if (key.endsWith("[]")) {
            if (!object[key]) {
                object[key] = [value];
            } else {
                object[key].push(value);
            }
        } else {
            // Reflect.has in favor of: object.hasOwnProperty(key)
            if (!Reflect.has(object, key)) {
                object[key] = value;
                return;
            }
            if (!Array.isArray(object[key])) {
                object[key] = [object[key]];
            }
            object[key].push(value);
        }
    });
    // console.log(JSON.stringify(object));
    return JSON.stringify(object);
}
