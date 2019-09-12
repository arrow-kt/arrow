const categoryIconWhite = document.getElementsByClassName('cat-icon-white');
const arrayCategoryIconWhite = Array.from(categoryIconWhite);
const categoryIconColor = document.getElementsByClassName('cat-icon-color');
const arrayCategoryIconColor = Array.from(categoryIconColor);
const categorySpan = document.getElementsByClassName('cat-span');
const arrayCategorySpan = Array.from(categorySpan);

// Auxiliar function to identify the current icon and span objects when mapping
function getCurrentId(id) {

  const iconId = id;
  const iconIdSpan = iconId + "-span";
  const iconWhite = iconId + "-white";
  const iconColor = iconId + "-color";
  const iconWhiteObject = document.getElementById(iconWhite);
  const iconColorObject = document.getElementById(iconColor);
  const iconSpanObject = document.getElementById(iconIdSpan);

  return {
    iconWhite: iconWhiteObject,
    iconColor: iconColorObject,
    span: iconSpanObject,
  };
}

function checkActiveTag(arrowFeatures) {
  arrowFeatures.map(function(el) {
    const {
      iconWhite,
      iconColor,
      span
    } = getCurrentId(el.id);

    if(el.classList.contains('active')) {
      iconWhite.style.opacity = 0;
      iconColor.style.opacity = 1;
      span.style.opacity = 1;
    } else {
      iconWhite.style.opacity = 0.5;
      iconColor.style.opacity = 0;
      span.style.opacity = 0.5;
    }
  });
}

function applyingFocus(arrowFeatures, id) {

  arrowFeatures.map(function(el) {
    const {
      iconWhite,
      iconColor,
      span
    } = getCurrentId(el.id);

    if (el.id != id) {
      if(el.classList.contains('active')) {
        iconColor.style.opacity = 0.5;
        iconWhite.style.opacity = 0;
        span.style.opacity = 0.5;
      } else {
        iconWhite.style.opacity = 0.5;
        iconColor.style.opacity = 0;
        span.style.opacity = 0.5;
      }
    } else {
      iconColor.style.opacity = 1;
      iconWhite.style.opacity = 0;
      span.style.opacity = 1;
    }
  });
}
