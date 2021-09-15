// base elements
const body = document.getElementById('arrow-main');
const baseArrowLogo = document.getElementById('base-arrow-animation');
const homeCodeBlock = document.querySelectorAll('.browser-content');
const arrayHomeCodeBlock = Array.from(homeCodeBlock);

// nav elements
const siteNav = document.getElementById('site-nav');
const navBrandWhite = document.getElementById('nav-brand-white');
const navBrandDark = document.getElementById('nav-brand-dark');
const footerBrandWhite = document.getElementById('footer-47-white');
const footerBrandDark = document.getElementById('footer-47-dark');
const navLinks = siteNav.querySelectorAll('a');
const arrayNavLinks = Array.from(navLinks);
const navMenuLinks = document.querySelectorAll('.nav-menu-item a');
const arrayNavMenuLinks = Array.from(navMenuLinks);

// animations containers
const headerAnimation = document.querySelectorAll('.header-image div');
const arrayHeaderAnimation = Array.from(headerAnimation);

// core elements
const coreArrowLogo = document.getElementById('core-arrow-animation');

// fx elements
const fxArrowLogo = document.getElementById('fx-arrow-animation');

// optics elements
const opticsArrowLogo = document.getElementById('optics-arrow-animation');

// meta elements
const metaArrowLogo = document.getElementById('meta-arrow-animation');

// Features elements
const headerCategoryRow = document.querySelectorAll('.item-header > p, .item-header > h2');
const arrayHeaderCategoryRow = Array.from(headerCategoryRow);
const categoryIconWhite = document.getElementsByClassName('cat-icon-white');
const arrayCategoryIconWhite = Array.from(categoryIconWhite);
const categoryIconDark = document.getElementsByClassName('cat-icon-dark');
const arrayCategoryIconDark = Array.from(categoryIconDark);
const categoryIconColor = document.getElementsByClassName('cat-icon-color');
const arrayCategoryIconColor = Array.from(categoryIconColor);
const headerText = document.getElementById('header-text');

// footer elements
const siteFooter = document.getElementById('site-footer');
const footerLinks = siteFooter.querySelectorAll('a');
const arrayFooterLinks = Array.from(footerLinks);

function checkActiveFeature(arrowFeature) {
  arrowFeature.map(function(el) {
    const current_id = el.id;
    const current_class = el.classList;
    if (current_class.contains('active')) {
      switch (current_id) {
        case 'core':
          commonHoverStyle(current_id);
          coreHoverStyle();
          break;
        case 'fx':
          commonHoverStyle(current_id);
          fxHoverStyle();
          break;
        case 'optics':
          commonHoverStyle(current_id);
          opticsHoverStyle();
          break;
        case 'meta':
          commonHoverStyle(current_id);
          metaHoverStyle();
          break;
        default:
          baseHoverStyle();
      }
    }
  });
}

function setOpacity(iconElements, id, opacity) {
  iconElements.map(function(obj) {
    if (obj.id.includes(id)) {
      const idWhite = `${id}-white`;
      return((obj.id != idWhite) ? obj.style.opacity = 1 : obj.style.opacity = 0);

    } else {
      obj.style.opacity = opacity;
    }
  });
}

function setCodeBlockHidden(elements, id, opacity) {
  elements.map(function(obj) {
    if (obj.id.includes(id)) {
      obj.style.visibility = 'unset';
      obj.classList.add('active');
    } else {
      obj.style.visibility = 'hidden';
    }
  });
}

function hideCodeBlock(elements) {
  elements.map(el => el.style.visibility = 'hidden');
}

function animationHoverControl(arrayHeaderAnimation, id) {
  arrayHeaderAnimation.map(function(obj) {
    return (obj.id.includes(id) ?
     obj.style.opacity = 1 : obj.style.opacity = 0);
  });
}

function addClassName(el, name) {
  if(!el.classList.contains(name)) {
    el.className += ` ${name}`;
  }
}

function animateCodeCSS(elements, animationName, id, callback) {
  elements.map(function(obj) {
    if(obj.id.includes(id) & !obj.classList.contains('active')) {
      obj.classList.add('animated', animationName);
    } else {
      obj.classList.remove('active');
    }
  });

  function handleAnimationEnd() {
    elements.map(obj => obj.classList.remove('animated', animationName));
    elements.map(obj => obj.removeEventListener('animationend', handleAnimationEnd));
    if (typeof callback === 'function') callback();
  }

  elements.map(function(obj) {
    if(obj.id.includes(id)) {
      obj.addEventListener('animationend', handleAnimationEnd);
    }
  });
}

function commonHoverStyle(id) {
  body.style.setProperty('--color-primary', '#F5F7F8');
  headerText.style.opacity = 0;
  arrayNavLinks.map(obj => addClassName(obj, 'hover-mode'));
  arrayFooterLinks.map(obj => addClassName(obj, 'hover-mode'));
  animateCodeCSS(arrayHomeCodeBlock, 'fadeInDown', id);
  navBrandDark.style.opacity = 0;
  navBrandWhite.style.opacity = 1;
  footerBrandDark.style.opacity = 0;
  footerBrandWhite.style.opacity = 1;
  baseArrowLogo.style.opacity = 0;
  siteNav.classList.remove('core', 'fx', 'meta', 'optics');
  arrayCategoryIconDark.map(obj => obj.style.opacity = 0);
  setOpacity(arrayCategoryIconWhite, id, 0.5);
  setOpacity(arrayHeaderCategoryRow, id, 0.5);
  setCodeBlockHidden(arrayHomeCodeBlock, id);
  setOpacity(arrayCategoryIconColor, id, 0);
  animationHoverControl(arrayHeaderAnimation, id);

  siteFooter.style.background = "url('img/home/hover-lines-footer.svg') repeat-x";
}

function coreHoverStyle() {
  body.style.background = "#354755 url('img/home/hover-lines-header.svg') repeat-x";
  coreArrowLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'core');
  }
  arrowFxAnimation.stop();
  arrowOpticsAnimation.stop();
  arrowMetaAnimation.stop();
}

function fxHoverStyle() {
  body.style.background = "#33393f url('img/home/hover-lines-header.svg') repeat-x";
  fxArrowLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'fx');
  }
  arrowCoreAnimation.stop();
  arrowOpticsAnimation.stop();
  arrowMetaAnimation.stop();
}

function opticsHoverStyle() {
  body.style.background = "#35565F url('img/home/hover-lines-header.svg') repeat-x";
  opticsArrowLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'optics');
  }
  arrowCoreAnimation.stop();
  arrowFxAnimation.stop();
  arrowMetaAnimation.stop();
}

function metaHoverStyle() {
  body.style.background = "#2E3B44 url('img/home/hover-lines-header.svg') repeat-x";
  metaArrowLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'meta');
  }
  arrowCoreAnimation.stop();
  arrowFxAnimation.stop();
  arrowOpticsAnimation.stop();
}

// find the way to do it just the first time it comes from mobile
function resetHovers() {
  let activeFeature = false;

  arrayArrowFeatures.map(function(el) {
    if (el.className.includes('active')) {
      activeFeature = true;
    }
  })

  if (!activeFeature) {
    body.style.setProperty('--color-primary', '#263238');
    body.style.background = "#F5F7F8 url('img/home/lines-header-bg.svg') repeat-x";
    navBrandWhite.style.opacity = 0;
    navBrandDark.style.opacity = 1;
    footerBrandWhite.style.opacity = 0;
    footerBrandDark.style.opacity = 1;
    headerText.style.opacity = 1;
    arrayCategoryIconDark.map(el => el.style.opacity = 1);
    arrayCategoryIconWhite.map(el => el.style.opacity = 0);
    arrayCategoryIconColor.map(el => el.style.opacity = 0);
    arrayHomeCodeBlock.map(el => el.style.visibility = 'hidden');
    arrayNavLinks.map(obj => obj.classList.remove('hover-mode'));
    arrayFooterLinks.map(obj => obj.classList.remove('hover-mode'));
    siteNav.classList.remove('core');
    arrayHeaderAnimation.map(el => el.id.includes('base-arrow-animation') ? el.style.opacity = 1 : el.style.opacity = 0);
    arrowBaseAnimation.play();
  }
}

function mobileMode() {
  body.style.setProperty('--color-primary', '#F5F7F8');
  body.style.background = "#354755 url('img/home/hover-lines-header.svg') repeat-x";
  siteFooter.style.background = "url('img/home/hover-lines-footer.svg') repeat-x";
  headerText.style.opacity = 1;
  navBrandWhite.style.opacity = 1;
  navBrandDark.style.opacity = 0;
  arrayArrowFeatures.map(el => el.classList.remove('active'));
  arrayNavLinks.map(obj => addClassName(obj, 'hover-mode'));
  arrayFooterLinks.map(obj => addClassName(obj, 'hover-mode'));
  arrayHeaderCategoryRow.map(el => el.style.opacity = 1);
  arrayCategoryIconColor.map(el => el.style.opacity = 1);
  arrayCategoryIconWhite.map(el => el.style.opacity = 0);
  arrayCategoryIconDark.map(el => el.style.opacity = 0);
  arrowBaseAnimation.stop();
  siteNav.classList.remove('fx', 'meta', 'optics');
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'core');
  }
}
