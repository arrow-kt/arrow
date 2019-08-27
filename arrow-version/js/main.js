// This initialization requires that this script is loaded with `defer`
const navElement = document.querySelector("#site-nav");
const arrowFeatures = document.querySelectorAll(".feature");
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

function checkActiveFeatureScroll(arrowFeatures) {
  arrowFeatures.map(function(el) {
    const current_id = el.id;
    const current_class = el.classList;

    if (current_class.contains('active')) {
      navElement.className += ` ${current_id}`
    }
  });
}

const scrollTop = Math.max(window.pageYOffset, document.documentElement.scrollTop, document.body.scrollTop)

// Navigation element modification through scrolling
function scrollFunction() {
  if (window.pageYOffset || document.documentElement.scrollTop > scrollTop) {
    navElement.classList.add("nav-scroll");
    checkActiveFeatureScroll(arrayArrowFeatures);
  } else {
    navElement.classList.remove("nav-scroll");
  }
}

// Init call
function loadEvent() {

  document.addEventListener("scroll", scrollFunction);

  function baseAnimation() {
    arrowBaseAnimation.play();
    incubatorBaseAnimation.play();
  }

  // base elements
  const baseArrowLogo = document.getElementById('base-arrow-animation');
  baseArrowLogo.addEventListener('load', baseAnimation());

  const incubatorBaseLogo = document.getElementById('incubator-base-animation');
  incubatorBaseLogo.addEventListener('load', baseAnimation());

  // core elements
  const corePlayHover = document.getElementById('core');

  corePlayHover.addEventListener('mouseenter', () => {
    corePlayHover.classList.add('active');
    checkActiveFeature(arrayArrowFeatures);
    arrowCoreAnimation.play();
    incubatorCoreAnimation.play();
    arrowBaseAnimation.stop();
  });

  corePlayHover.addEventListener('mouseleave', () => {
    corePlayHover.classList.remove('active');
    siteNav.classList.remove('core');
    baseHoverStyle();
    arrowCoreAnimation.stop();
    incubatorCoreAnimation.stop();
    arrowBaseAnimation.play();
  });

  // fx elements
  const fxPlayHover = document.getElementById('fx');

  fxPlayHover.addEventListener('mouseenter', () => {
    fxPlayHover.classList.add('active');
    checkActiveFeature(arrayArrowFeatures);
    arrowFxAnimation.play();
    incubatorFxAnimation.play();
    arrowBaseAnimation.stop();
  });

  fxPlayHover.addEventListener('mouseleave', () => {
    fxPlayHover.classList.remove('active');
    siteNav.classList.remove('fx');
    baseHoverStyle();
    arrowFxAnimation.stop();
    incubatorFxAnimation.stop();
    arrowBaseAnimation.play();
  });

  // meta elements
  const metaPlayHover = document.getElementById('meta');

  metaPlayHover.addEventListener('mouseenter', () => {
    metaPlayHover.classList.add('active');
    checkActiveFeature(arrayArrowFeatures);
    arrowMetaAnimation.play();
    incubatorMetaAnimation.play();
    arrowBaseAnimation.stop();
  });

  metaPlayHover.addEventListener('mouseleave', () => {
    metaPlayHover.classList.remove('active');
    siteNav.classList.remove('meta');
    baseHoverStyle();
    arrowMetaAnimation.stop();
    incubatorMetaAnimation.stop();
    arrowBaseAnimation.play();
  });

  // optics elements
  const opticsPlayHover = document.getElementById('optics');

  opticsPlayHover.addEventListener('mouseenter', () => {
    opticsPlayHover.classList.add('active');
    checkActiveFeature(arrayArrowFeatures);
    arrowOpticsAnimation.play();
    incubatorOpticsAnimation.play();
    arrowBaseAnimation.stop();
  });

  opticsPlayHover.addEventListener('mouseleave', () => {
    opticsPlayHover.classList.remove('active');
    siteNav.classList.remove('optics');
    baseHoverStyle();
    arrowOpticsAnimation.stop();
    incubatorOpticsAnimation.stop();
    arrowBaseAnimation.play();
  });

  // incubator elements
  const incubatorPlayHover = document.getElementById('incubator');

  incubatorPlayHover.addEventListener('mouseenter', () => {
    incubatorPlayHover.classList.add('active');
    checkActiveFeature(arrayArrowFeatures);
    incubatorHoverAnimation.play();
    arrowBaseAnimation.stop();
  });

  incubatorPlayHover.addEventListener('mouseleave', () => {
    incubatorPlayHover.classList.remove('active');
    siteNav.classList.remove('incubator');
    baseHoverStyle();
    incubatorHoverAnimation.stop();
    incubatorArrowLogo.style.opacity = 0;
    arrowBaseAnimation.play();
  });

}

function baseHoverStyle() {
  baseArrowLogo.style.opacity = 1;
  incubatorBaseLogo.style.opacity = 1;
  body.style.setProperty('--color-primary', '#263238');
  body.style.background = "#F5F7F8 url('../img/home/lines-header-bg.svg') repeat-x";
  navBrandWhite.style.opacity = 0;
  navBrandDark.style.opacity = 1;
  navIconOpenWhite.style.opacity = 0;
  navIconOpenDark.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/home/bullet.svg')";
  incubatorCoreLogo.style.opacity = 0;
  incubatorFxLogo.style.opacity = 0;
  incubatorOpticsLogo.style.opacity = 0;
  incubatorMetaLogo.style.opacity = 0;
  incubatorHoverLogo.style.opacity = 0;
  catIconCoreColor.style.opacity = 0;
  catIconFxColor.style.opacity = 0;
  catIconOpticsColor.style.opacity = 0;
  catIconMetaColor.style.opacity = 0;
  resetOpacity(arrayCategoryIconWhite, 0);
  resetOpacity(arrayCategoryIconDark, 1);
  resetOpacity(arrayCategoryRow, 1);
  siteFooter.style.background = "url('../img/home/lines-footer-bg.svg') repeat-x";
}

// Attach the functions to each event they are interested in
window.addEventListener("load", loadEvent);