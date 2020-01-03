// This initialization requires that this script is loaded with `defer`
const navElement = document.querySelector('#site-nav');
const arrowFeatures = document.querySelectorAll('.feature');
const arrayArrowFeatures = Array.from(arrowFeatures);
const mobileModeBreakPoint = 992;

/**
 * Toggle an specific class to the received DOM element.
 * @param {string}	elemSelector The query selector specifying the target element.
 * @param {string}	[activeClass='active'] The class to be applied/removed.
 */
function toggleClass(elemSelector, activeClass = 'active') {
  const elem = document.querySelector(elemSelector);
  if (elem) {
    elem.classList.toggle(activeClass);
  }
  if (navElement.className.includes('nav-scroll')) {
    addClassName(elem, 'top-scroll');
  } else {
    elem.classList.remove('top-scroll');
  }
}

function checkActiveFeatureScroll(arrowFeatures, top) {
    top = top || false;
    arrowFeatures.map(function(el) {
    const current_id = el.id;
    const current_class = el.classList;

    if (top) {
      navElement.classList.remove(`${current_id}`);
    } else {
      if (current_class.contains('active')) {
      addClassName(navElement, `${current_id}`);
      }
    }
  });
}

const scrollTop = Math.max(window.pageYOffset, document.documentElement.scrollTop, document.body.scrollTop)

// Navigation element modification through scrolling
function scrollFunction() {
  if (window.pageYOffset || document.documentElement.scrollTop > scrollTop) {
    navElement.classList.add('nav-scroll');
    if (screenSize() < mobileModeBreakPoint) {
      navElement.classList.add('core');
    }
    checkActiveFeatureScroll(arrayArrowFeatures);
  } else {
    navElement.classList.remove('nav-scroll');
    checkActiveFeatureScroll(arrayArrowFeatures, top);
  }
}

function screenSize() {
  let size = 0;
  if (typeof window.innerWidth != 'undefined')
  {
    size = window.innerWidth;
  }
  else if (typeof document.documentElement != 'undefined'
      && typeof document.documentElement.clientWidth !=
      'undefined' && document.documentElement.clientWidth != 0)
  {
    size = document.documentElement.clientWidth;
  }
  else   {
    size = document.getElementsByTagName('body')[0].clientWidth;
  }
  return size;
}

function mobileViewControl() {
  screenSize() < mobileModeBreakPoint ? mobileMode() : resetHovers();
}

window.addEventListener("resize", mobileViewControl);

// Init call
function loadEvent() {
  mobileViewControl();

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
      if (screenSize() > mobileModeBreakPoint) {
        arrayArrowFeatures.map(obj => obj.classList.remove('active'));
        corePlayHover.classList.add('active');
        checkActiveFeature(arrayArrowFeatures);
        arrowCoreAnimation.play();
        incubatorCoreAnimation.play();
        arrowBaseAnimation.stop();
      }
  });

  // fx elements
  const fxPlayHover = document.getElementById('fx');

  fxPlayHover.addEventListener('mouseenter', () => {
    if (screenSize() > mobileModeBreakPoint) {
      arrayArrowFeatures.map(obj => obj.classList.remove('active'));
      fxPlayHover.classList.add('active');
      checkActiveFeature(arrayArrowFeatures);
      arrowFxAnimation.play();
      incubatorFxAnimation.play();
      arrowBaseAnimation.stop();
    }
  });

  // meta elements
  const metaPlayHover = document.getElementById('meta');

  metaPlayHover.addEventListener('mouseenter', () => {
    if (screenSize() > mobileModeBreakPoint) {
      arrayArrowFeatures.map(obj => obj.classList.remove('active'));
      metaPlayHover.classList.add('active');
      checkActiveFeature(arrayArrowFeatures);
      arrowMetaAnimation.play();
      incubatorMetaAnimation.play();
      arrowBaseAnimation.stop();
    }
  });

  // optics elements
  const opticsPlayHover = document.getElementById('optics');

  opticsPlayHover.addEventListener('mouseenter', () => {
    if (screenSize() > mobileModeBreakPoint) {
      arrayArrowFeatures.map(obj => obj.classList.remove('active'));
      opticsPlayHover.classList.add('active');
      checkActiveFeature(arrayArrowFeatures);
      arrowOpticsAnimation.play();
      incubatorOpticsAnimation.play();
      arrowBaseAnimation.stop();
    }
  });

  // incubator elements
  const incubatorPlayHover = document.getElementById('incubator');

  incubatorPlayHover.addEventListener('mouseenter', () => {
    if (screenSize() > mobileModeBreakPoint) {
      arrayArrowFeatures.map(obj => obj.classList.remove('active'));
      incubatorPlayHover.classList.add('active');
      checkActiveFeature(arrayArrowFeatures);
      incubatorHoverAnimation.play();
      arrowBaseAnimation.stop();
    }
  });
}

// Attach the functions to each event they are interested in
window.addEventListener("load", loadEvent);
