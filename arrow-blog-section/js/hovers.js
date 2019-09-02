const iconFilters = document.querySelectorAll("#icons-filter div");
const arrayiconFilters = Array.from(iconFilters);

// Auxiliar function to identify the current icon and span objects when mapping
function getCurrentId(id) {

  const iconId = id;
  const iconIdSpan = iconId + "-span";
  const iconObject = document.getElementById(iconId);
  const iconSpanObject = document.getElementById(iconIdSpan);

  return {
    icon: iconObject,
    span: iconSpanObject,
  };
}

function losingFocus(e) {
  arrayiconFilters.map(function(obj) {
    const {
      icon,
      span
    } = getCurrentId(obj.id);

    if (icon.className != "tab-item active") {
      if (obj.id == e.id) {
        icon.style.backgroundImage = `url('/img/icons/arrow-icons/${obj.id}.svg')`;
        icon.style.opacity = "0.4";
        span.style.opacity = "0.4";
      }
    } else {
      icon.style.opacity = "1";
      span.style.opacity = "1";
    }
  });
}

function checkActiveTag() {
  arrayiconFilters.map(function(obj) {
    const {
      icon,
      span
    } = getCurrentId(obj.id);

    if (icon.className == "tab-item active") {
      icon.style.backgroundImage = `url('/img/icons/arrow-icons/hovers/${obj.id}-hover.svg')`;
    } else {
      icon.style.opacity = "0.4";
      span.style.opacity = "0.4";
    }
  });

}

function applyingFocus(e) {

  arrayiconFilters.map(function(obj) {
    const {
      icon,
      span
    } = getCurrentId(obj.id);

    if (obj.id != e.id) {
      icon.style.opacity = "0.4";
      span.style.opacity = "0.4";
    } else {
      icon.style.backgroundImage = `url('/img/icons/arrow-icons/hovers/${obj.id}-hover.svg')`;
      icon.style.opacity = "1";
      span.style.opacity = "1";
    }
  });
}
