// base elements
const body = document.getElementById('arrow-main');
const baseArrowLogo = document.getElementById('base-arrow-animation');

// nav elements
const siteNav = document.getElementById('site-nav');
const navIconOpenWhite = document.getElementById('nav-icon-open-white');
const navIconOpenDark = document.getElementById('nav-icon-open-dark');
const navBrandWhite = document.getElementById('nav-brand-white');
const navBrandDark = document.getElementById('nav-brand-dark');
const navLinks = siteNav.querySelectorAll('a');
const arrayNavLinks = Array.from(navLinks);

// animations containers
const headerAnimation = document.querySelectorAll('.header-image div');
const arrayHeaderAnimation = Array.from(headerAnimation);
const incubatorAnimation = document.querySelectorAll('.incubator-image div');
const arrayIncubatorAnimation = Array.from(incubatorAnimation);

// core elements
const coreArrowLogo = document.getElementById('core-arrow-animation');
const incubatorCoreLogo = document.getElementById('incubator-core-animation');

// fx elements
const fxArrowLogo = document.getElementById('fx-arrow-animation');
const incubatorFxLogo = document.getElementById('incubator-fx-animation');

// optics elements
const opticsArrowLogo = document.getElementById('optics-arrow-animation');
const incubatorOpticsLogo = document.getElementById('incubator-optics-animation');

// meta elements
const metaArrowLogo = document.getElementById('meta-arrow-animation');
const incubatorMetaLogo = document.getElementById('incubator-meta-animation');

// incubator elements
const incubatorBaseLogo = document.getElementById('incubator-base-animation');
const incubatorArrowLogo = document.getElementById('logo-white-lines');
const incubatorLinks = document.querySelectorAll('.incubator-items a');
const arrayIncubatorLinks = Array.from(incubatorLinks);
const incubatorHoverLogo = document.getElementById('incubator-hover-animation');
const incubatorList = document.getElementById('incubator-list');

// Features elements
const headerCategoryRow = document.querySelectorAll('.item-header h2, p');
const arrayHeaderCategoryRow = Array.from(headerCategoryRow);
const buttonCategoryRow = document.querySelectorAll('.main-item a');
const arrayButtonCategoryRoww = Array.from(buttonCategoryRow);
const categoryIconWhite = document.getElementsByClassName('cat-icon-white');
const arrayCategoryIconWhite = Array.from(categoryIconWhite);
const categoryIconDark = document.getElementsByClassName('cat-icon-dark');
const arrayCategoryIconDark = Array.from(categoryIconDark);
const categoryIconColor = document.getElementsByClassName('cat-icon-color');
const arrayCategoryIconColor = Array.from(categoryIconColor);

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
        case 'incubator':
          commonHoverStyle(current_id, true);
          incubatorHoverStyle();
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

function animationHoverControl(arrayHeaderAnimation, arrayIncubatorAnimation, id) {
  arrayHeaderAnimation.map(function(obj) {
    return (obj.id.includes(id) ?
     obj.style.opacity = 1 : obj.style.opacity = 0);
  });

  if(id != 'incubator') {
    incubatorArrowLogo.style.opacity = 0;
    arrayIncubatorAnimation.map(function(obj) {
      return (obj.id.includes(id) ?
       obj.style.opacity = 1 : obj.style.opacity = 0);
    });
  } else {
    arrayIncubatorAnimation.map(function(obj) {
      return (obj.id.includes('hover') ?
       obj.style.opacity = 1 : obj.style.opacity = 0);
    });
  }
}

function addClassName(el, name) {
  if(!el.classList.contains(name)) {
    el.className += ` ${name}`;
  }
}

function commonHoverStyle(id, incubatorHover) {
  body.style.setProperty('--color-primary', '#F5F7F8');
  arrayNavLinks.map(obj => addClassName(obj, 'hover-mode'));
  arrayFooterLinks.map(obj => addClassName(obj, 'hover-mode'));
  navBrandDark.style.opacity = 0;
  navBrandWhite.style.opacity = 1;
  baseArrowLogo.style.opacity = 0;
  navIconOpenDark.style.opacity = 0;
  navIconOpenWhite.style.opacity = 1;
  incubatorBaseLogo.style.opacity = 0;
  incubatorHover = incubatorHover || false;
  siteNav.classList.remove('core', 'fx', 'meta', 'optics', 'incubator');
  arrayCategoryIconDark.map(obj => obj.style.opacity = 0);
  if (incubatorHover == false) {
    setOpacity(arrayCategoryIconWhite, id, 0.5);
    setOpacity(arrayHeaderCategoryRow, id, 0.5);
    setOpacity(arrayButtonCategoryRoww, id, 0.5);
  } else {
    setOpacity(arrayCategoryIconWhite, id, 1);
    setOpacity(arrayHeaderCategoryRow, id, 1);
    setOpacity(arrayButtonCategoryRoww, id, 1);
  }
  setOpacity(arrayCategoryIconColor, id, 0);
  animationHoverControl(arrayHeaderAnimation, arrayIncubatorAnimation, id);

  siteFooter.style.background = "url('../img/home/hover-lines-footer.svg') repeat-x";
}

function coreHoverStyle() {
  body.style.background = "#354755 url('../img/home/hover-lines-header.svg') repeat-x";
  coreArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/core/core-bullet.svg')";
  incubatorCoreLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'core');
  }
  arrowFxAnimation.stop();
  arrowOpticsAnimation.stop();
  arrowMetaAnimation.stop();
}

function fxHoverStyle() {
  body.style.background = "#33393f url('../img/home/hover-lines-header.svg') repeat-x";
  fxArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/fx/fx-bullet.svg')";
  incubatorFxLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'fx');
  }
  arrowCoreAnimation.stop();
  arrowOpticsAnimation.stop();
  arrowMetaAnimation.stop();
}

function opticsHoverStyle() {
  body.style.background = "#35565F url('../img/home/hover-lines-header.svg') repeat-x";
  opticsArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/optics/optics-bullet.svg')";
  incubatorOpticsLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'optics');
  }
  arrowCoreAnimation.stop();
  arrowFxAnimation.stop();
  arrowMetaAnimation.stop();
}

function metaHoverStyle() {
  body.style.background = "#2E3B44 url('../img/home/hover-lines-header.svg') repeat-x";
  metaArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/meta/meta-bullet.svg')";
  incubatorMetaLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'meta');
  }
  arrowCoreAnimation.stop();
  arrowFxAnimation.stop();
  arrowOpticsAnimation.stop();
}

function incubatorHoverStyle() {
  body.style.background = "#354755 url('../img/home/hover-lines-header.svg') repeat-x";
  incubatorArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/incubator/incubator-bullet.svg')";
  incubatorHoverLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    addClassName(siteNav, 'incubator');
  }
  arrayIncubatorLinks.map(obj => addClassName(obj, 'hover-mode'));
}
