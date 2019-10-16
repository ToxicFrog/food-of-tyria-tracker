function updateAllStyles() {
  for (let input of document.getElementsByClassName("cooked-toggle")) {
    updateStyle(input);
  }
}

function updateStyle(input) {
  const id = input.id;
  const label = document.getElementById("label:" + id);
  const button = document.getElementById("button:" + id);

  if (input.checked) {
    button.classList.add("cooked-recipe");
    label.classList.add("cooked-recipe");
  } else {
    button.classList.remove("cooked-recipe");
    label.classList.remove("cooked-recipe");
  }
}

function toggleCooked(input) {
  const url = "/items/" + input.id;
  if (input.checked) {
    fetch(url + "/set-cooked");
  } else {
    fetch(url + "/unset-cooked");
  }
  updateStyle(input);
}
