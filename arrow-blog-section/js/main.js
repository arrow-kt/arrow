// This initialization requires that this script is loaded with `defer`
const navElement = document.querySelector("#site-nav");

/**
 * Toggle an specific class to the received DOM element.
 * @param {string}	elemSelector The query selector specifying the target element.
 * @param {string}	[activeClass='active'] The class to be applied/removed.
 */
function toggleClass(elemSelector, activeClass = "active") {
  const elem = document.querySelector(elemSelector);
  if (elem) {
    elem.classList.toggle(activeClass);
  }
}

const scrollTop = Math.max(window.pageYOffset, document.documentElement.scrollTop, document.body.scrollTop)

// Navigation element modification through scrolling
function scrollFunction() {
  if (window.pageYOffset || document.documentElement.scrollTop > scrollTop) {
    navElement.classList.add("nav-scroll");
  } else {
    navElement.classList.remove("nav-scroll");
  }
}

// Init call
function loadEvent() {
  document.addEventListener("scroll", scrollFunction);

  checkActiveTag();
}

// Attach the functions to each event they are interested in
window.addEventListener("load", loadEvent);

// Clears the search input when clicking outside
document.onclick = function() {
  document.getElementById("search-input").value = "";
  document.getElementById("results-container").style.display = "none";
};