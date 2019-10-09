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
    button.style.fontWeight = "bold";
    button.style.textShadow = "0 0 5px #F00";
    label.style.textDecoration = "line-through";
  } else {
    button.style.fontWeight = null;
    button.style.textShadow = null;
    label.style.textDecoration = null;
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
