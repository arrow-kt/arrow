// This initialization requires that this script is loaded with `defer`
const navElement = document.querySelector("#site-nav");
const arrowFeatures= document.querySelectorAll('#features a');
const arrayArrowFeatures = Array.from(arrowFeatures);

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

  checkActiveTag(arrayArrowFeatures);

  // allposts
  const allPostsHover = document.getElementById('allposts');

  allPostsHover.addEventListener('mouseenter', () => {
    const curent_id = allPostsHover.id;
    applyingFocus(arrayArrowFeatures, curent_id);
  });
  allPostsHover.addEventListener('mouseleave', () => {
    checkActiveTag(arrayArrowFeatures);
  });

  // core
  const coreHover = document.getElementById('core');

  coreHover.addEventListener('mouseenter', () => {
    const curent_id = coreHover.id;
    applyingFocus(arrayArrowFeatures, curent_id);
  });
  coreHover.addEventListener('mouseleave', () => {
    checkActiveTag(arrayArrowFeatures);
  });

  // fx
  const fxHover = document.getElementById('fx');

  fxHover.addEventListener('mouseenter', () => {
    const curent_id = fxHover.id;
    applyingFocus(arrayArrowFeatures, curent_id);
  });
  fxHover.addEventListener('mouseleave', () => {
    checkActiveTag(arrayArrowFeatures);
  });

  // optics
  const opticsHover = document.getElementById('optics');

  opticsHover.addEventListener('mouseenter', () => {
    const curent_id = opticsHover.id;
    applyingFocus(arrayArrowFeatures, curent_id);
  });
  opticsHover.addEventListener('mouseleave', () => {
    checkActiveTag(arrayArrowFeatures);
  });

  // meta
  const metaHover = document.getElementById('meta');

  metaHover.addEventListener('mouseenter', () => {
    const curent_id = metaHover.id;
    applyingFocus(arrayArrowFeatures, curent_id);
  });
  metaHover.addEventListener('mouseleave', () => {
    checkActiveTag(arrayArrowFeatures);
  });

  // incubator
  const incubatorHover = document.getElementById('incubator');

  incubatorHover.addEventListener('mouseenter', () => {
    const curent_id = incubatorHover.id;
    applyingFocus(arrayArrowFeatures, curent_id);
  });
  incubatorHover.addEventListener('mouseleave', () => {
    checkActiveTag(arrayArrowFeatures);
  });

}

// Attach the functions to each event they are interested in
window.addEventListener("load", loadEvent);

// Clears the search input when clicking outside
document.onclick = function() {
  document.getElementById("search-input").value = "";
  document.getElementById("results-container").style.display = "none";
};
