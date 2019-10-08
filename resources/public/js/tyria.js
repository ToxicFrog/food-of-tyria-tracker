function toggleCooked(input) {
  if (input.checked) {
    input.parentNode.style = "font-weight:bold; text-shadow:0 0 5px #F00";
    fetch(document.baseURI + "/set-cooked");
  } else {
    input.parentNode.style = "";
    fetch(document.baseURI + "/unset-cooked");
  }
}
