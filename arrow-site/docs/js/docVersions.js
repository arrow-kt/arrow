/* When the user clicks on the navigation Documentation button,
 * toggle between hiding and showing the dropdown content.
 */
function displayToggle(e) {
  e.preventDefault();
  document.querySelector("#version-dropdown > .dropdown-content").classList.toggle("show");
}

// Close the dropdown if the user clicks outside of it
function closeDropdown(e) {
  var dropdown = document.querySelector("#version-dropdown > .dropdown-content");
  var relatedTarget = e.relatedTarget || {};
  if (relatedTarget.parentNode !== dropdown) dropdown.classList.remove("show");
}

/* Check the Arrow version and remark the version in which we are. Also adds
 * blur event listener ​​to handle the documentation nav dropdown behaviour.
 */
function initializeDocVersion() {
  const currentHost = window.location.host;
  const subdomain = currentHost.split('.')[0];

  const stableVersionNode = document.head.querySelector("meta[name='stable-version']");
  const nextVersionNode = document.head.querySelector("meta[name='next-version']");

  const arrowVersionIndicator = document.querySelector("#arrow-version");

  // Returns numeric version based on received subdomain
  const detectedVersion = (function detectVersion(subdomain) {
    if (subdomain === 'arrow-kt') {
      return stableVersionNode.dataset.title.substring(1);
    }
    else if (subdomain === 'next') {
      return nextVersionNode.dataset.title.substring(1);
    }
    else {
      return subdomain.replace(/-/g, '.');
    }
  })(subdomain);

  if (arrowVersionIndicator && detectedVersion) arrowVersionIndicator.textContent = detectedVersion;
}


function closeTop() {
  document.getElementById("topWarning").remove();
  sessionStorage.setItem("showTopWarning", 1);
}

window.addEventListener("load", initializeDocVersion);
