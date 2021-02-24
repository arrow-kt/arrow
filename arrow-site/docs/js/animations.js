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

