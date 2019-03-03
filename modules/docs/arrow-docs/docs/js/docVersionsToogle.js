function displayToogle() {
  document.getElementById("docVersionDropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
  if (!event.target.matches('.dropbutton')) {
    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}

function showTopWarning() {
  const currentUrl = 'https://'+ window.location.host +'/'
  if (!currentUrl.includes(document.getElementById("stable").textContent)){
    document.getElementById("topWarning").style.display = 'flex';
  }
}

window.onload = showTopWarning;
