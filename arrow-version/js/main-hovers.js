// base elements
const body = document.getElementById('arrow-main');
const baseArrowLogo = document.getElementById('base-arrow-animation');
const incubatorBaseLogo = document.getElementById('incubator-base-animation');
const navBrandWhite = document.getElementById('nav-brand-white');
const navBrandDark = document.getElementById('nav-brand-dark');
const siteFooter = document.getElementById('site-footer');
const siteNav = document.getElementById('site-nav');
const navIconOpenWhite = document.getElementById('nav-icon-open-white');
const navIconOpenDark = document.getElementById('nav-icon-open-dark');

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
const incubatorArrowLogo = document.getElementById('logo-white-lines');
const incubatorLinks = document.querySelectorAll('.incubator-items a');
const arrayIncubatorLinks = Array.from(incubatorLinks);
const incubatorHoverLogo = document.getElementById('incubator-hover-animation');
const incubatorList = document.getElementById('incubator-list');

// features/categories elements
const categoryRow = document.querySelectorAll('#main-flex div');
const arrayCategoryRow = Array.from(categoryRow);
const categoryIconWhite = document.getElementsByClassName('cat-icon-white');
const arrayCategoryIconWhite = Array.from(categoryIconWhite);
const categoryIconDark = document.getElementsByClassName('cat-icon-dark');
const arrayCategoryIconDark = Array.from(categoryIconDark);

const catIconCoreColor = document.getElementById('cat-icon-color-core');
const catIconFxColor = document.getElementById('cat-icon-color-fx');
const catIconOpticsColor = document.getElementById('cat-icon-color-optics');
const catIconMetaColor = document.getElementById('cat-icon-color-meta');


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

    if (obj.className.includes('active') | obj.id.includes(id)) {
      const currentWhite = document.getElementById(`${id}-white`);
      const currentDark = document.getElementById(`${id}-dark`);
      currentWhite.style.opacity = 0;
      currentDark.style.opacity = 0;
    } else {
      obj.style.opacity = opacity;
    }
  });
}

function resetOpacity(iconElements, opacity) {
  iconElements.map(obj => obj.style.opacity = opacity);
}

function commonHoverStyle(id, incubatorHover) {
  body.style.setProperty('--color-primary', '#F5F7F8');
  navBrandDark.style.opacity = 0;
  navBrandWhite.style.opacity = 1;
  baseArrowLogo.style.opacity = 0;
  navIconOpenDark.style.opacity = 0;
  navIconOpenWhite.style.opacity = 1;
  incubatorBaseLogo.style.opacity = 0;
  incubatorHover = incubatorHover || false;
  if (incubatorHover == false) {
    setOpacity(arrayCategoryRow, id, 0.5);
    setOpacity(arrayCategoryIconWhite, id, 0.5);
  } else {
    setOpacity(arrayCategoryIconWhite, id, 1);
  }
  setOpacity(arrayCategoryIconDark, id, 0);
  siteFooter.style.background = "url('../img/home/hover-lines-footer.svg') repeat-x";
}

function coreHoverStyle() {
  body.style.background = "#354755 url('../img/home/hover-lines-header.svg') repeat-x";
  coreArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/core/core-bullet.svg')";
  catIconCoreColor.style.opacity = 1;
  incubatorCoreLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    siteNav.className += ' core'
  }
}

function fxHoverStyle() {
  body.style.background = "#33393f url('../img/home/hover-lines-header.svg') repeat-x";
  fxArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/fx/fx-bullet.svg')";
  catIconFxColor.style.opacity = 1;
  incubatorFxLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    siteNav.className += ' fx'
  }
}

function opticsHoverStyle() {
  body.style.background = "#35565F url('../img/home/hover-lines-header.svg') repeat-x";
  opticsArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/optics/optics-bullet.svg')";
  catIconOpticsColor.style.opacity = 1;
  incubatorOpticsLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    siteNav.className += ' optics'
  }
}

function metaHoverStyle() {
  body.style.background = "#2E3B44 url('../img/home/hover-lines-header.svg') repeat-x";
  metaArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/meta/meta-bullet.svg')";
  catIconMetaColor.style.opacity = 1;
  incubatorMetaLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    siteNav.className += ' meta'
  }
}

function incubatorHoverStyle() {
  body.style.background = "#354755 url('../img/home/hover-lines-header.svg') repeat-x";
  incubatorArrowLogo.style.opacity = 1;
  incubatorList.style.listStyleImage = "url('../img/incubator/incubator-bullet.svg')";
  incubatorHoverLogo.style.opacity = 1;
  if (siteNav.className.includes('nav-scroll')) {
    siteNav.className += ' incubator'
  }
  arrayIncubatorLinks.map(obj => obj.className += ' incubator-hover');
}