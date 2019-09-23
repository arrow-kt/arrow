// Animation instances
// Arrow base animation
const arrowBaseAnimation = lottie.loadAnimation({
  container: document.getElementById('base-arrow-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: false,
  autoplay: true,
  path: 'js/json/arrow-base.json'
});

// core animation
const arrowCoreAnimation = lottie.loadAnimation({
  container: document.getElementById('core-arrow-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: false,
  autoplay: false,
  path: 'js/json/arrow-core.json'
});

// fx animation
const arrowFxAnimation = lottie.loadAnimation({
  container: document.getElementById('fx-arrow-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: false,
  autoplay: false,
  path: 'js/json/arrow-fx.json'
});

// optics animation
const arrowOpticsAnimation = lottie.loadAnimation({
  container: document.getElementById('optics-arrow-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: false,
  autoplay: false,
  path: 'js/json/arrow-optics.json'
});

// meta animation
const arrowMetaAnimation = lottie.loadAnimation({
  container: document.getElementById('meta-arrow-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: false,
  autoplay: false,
  path: 'js/json/arrow-meta.json'
});

// incubator base animation
const incubatorBaseAnimation = lottie.loadAnimation({
  container: document.getElementById('incubator-base-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: true,
  autoplay: false,
  path: 'js/json/incubator-base.json'
});

// incubator core animation
const incubatorCoreAnimation = lottie.loadAnimation({
  container: document.getElementById('incubator-core-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: true,
  autoplay: false,
  path: 'js/json/incubator-core.json'
});

// incubator fx animation
const incubatorFxAnimation = lottie.loadAnimation({
  container: document.getElementById('incubator-fx-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: true,
  autoplay: false,
  path: 'js/json/incubator-fx.json'
});

// incubator optics animation
const incubatorOpticsAnimation = lottie.loadAnimation({
  container: document.getElementById('incubator-optics-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: true,
  autoplay: false,
  path: 'js/json/incubator-optics.json'
});

// incubator meta animation
const incubatorMetaAnimation = lottie.loadAnimation({
  container: document.getElementById('incubator-meta-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: true,
  autoplay: false,
  path: 'js/json/incubator-meta.json'
});

// incubator hover animation
const incubatorHoverAnimation = lottie.loadAnimation({
  container: document.getElementById('incubator-hover-animation'),
  renderer: 'svg' / 'canvas' / 'html',
  loop: true,
  autoplay: false,
  path: 'js/json/incubator-hover.json'
});
