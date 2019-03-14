/* When the user clicks on the navigation Documentation button,
toggle between hiding and showing the dropdown content */
function displayToogle() {
  document.getElementById("docVersionDropdown").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
function closeDropdown(event) {
  var dropdowns = document.getElementsByClassName("dropdown-content");
  var i;
  for (i = 0; i < dropdowns.length; i++) {
    var openDropdown = dropdowns[i];
    if (openDropdown.classList.contains("show")) {
      openDropdown.classList.remove("show");
    }
  }
};

/* Check the Arrow version and remark the version in wich we are.
Also initializes addEventListener ​​to handle the documentation
nav dropdown behaviour */
function initializeDocVersion() {

  document.getElementById("dropDownDiv").addEventListener("blur", closeDropdown);

  if( !sessionStorage.getItem('showTopWarning')) {

        document.getElementById("topWarning").style.display = "block";

        const currentUrl = "https://" + window.location.host + "/";

        if (currentUrl.includes(document.getElementById("previous").textContent)) {
          document.getElementById("top-previous").style.color = "white";
        }
        if (currentUrl.includes(document.getElementById("stable").textContent)) {
          document.getElementById("top-stable").style.color = "white";
        }
        if (currentUrl.includes(document.getElementById("next").textContent)) {
          document.getElementById("top-next").style.color = "white";
        }
  }
  else {
    document.getElementById("topWarning").remove();
  }
}

function closeTop() {
  document.getElementById("topWarning").remove();
  sessionStorage.setItem('showTopWarning', 1);
}

document.addEventListener("DOMContentLoaded", initializeDocVersion);
