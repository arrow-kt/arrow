/* When the user clicks on the navigation Documentation button,
toggle between hiding and showing the dropdown content */
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

// Check the Arrow version and show a warning on top if we are not in the stable version
function showTopWarning() {
  const currentUrl = 'https://'+ window.location.host +'/'
  if (!currentUrl.includes(document.getElementById("stable").textContent)){
    document.getElementById("topWarning").style.display = 'flex';
  }
}

window.onload = showTopWarning;
