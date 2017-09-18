// We should wait until all assets on page gets loaded to trigger animations,
// by this way avoiding any FOUC problems. Since jQuery is on the page we
// take advantage of it.
$(window).on("load", function() {
    // General injection duration
    var injectionDuration = 300;

    function setOpacity(elements, durationParam) {
      var duration = durationParam ? durationParam : injectionDuration;
      anime({
        targets: elements,
        opacity: 1,
        duration: duration,
        easing: 'easeInCubic',
      })
    }

    function bulgePath(elements) {
      anime.remove(elements);
      anime({
        targets: elements,
        strokeDasharray: '1',
        easing: 'easeInOutCubic',
        duration: 600,
        direction: 'alternate',
        loop: true,
      });
    }

    function bulgePathBack(elements) {
      anime.remove(elements);
      anime({
        targets: elements,
        strokeDasharray: '3',
        easing: 'easeInOutCubic',
        duration: 300,
      });
    }

    function emitPath(elements) {
      anime.remove(elements);
      anime({
        targets: elements,
        strokeDashoffset: 30,
        easing: 'linear',
        elasticity: 500,
        duration: 1000,
        loop: true,
      });
    }

    function rotate(elements) {
      anime.remove(elements);
      anime({
        targets: elements,
        rotateZ: [0, 360],
        easing: 'easeInOutCubic',
        elasticity: 600,
        duration: 1200,
        loop: true,
      });
    }

    function rotateBack(elements) {
      anime.remove(elements);
      anime({
        targets: elements,
        rotateZ: 360,
        easing: 'linear',
        elasticity: 600,
        duration: 600,
      });
    }

    function scale(elements) {
      anime.remove(elements);
      anime({
        targets: elements,
        scale: 1.2,
        easing: 'easeInOutCubic',
        duration: 550,
      });
    }

    function scaleBack(elements) {
      anime.remove(elements);
      anime({
        targets: elements,
        scale: 1,
        easing: 'linear',
        duration: 400,
      });
    }

    function undrawPath(elements) {
      anime.remove(elements);
      anime({
        targets: elements,
        strokeDashoffset: [0, anime.setDashoffset],
        easing: 'easeInOutCubic',
        duration: 1500,
        direction: 'alternate',
        loop: true,
        delay: function(el, i) { return i * 150 },
      });
    }

    function drawPath(elements, duration) {
      anime.remove(elements);
      anime({
        targets: elements,
        strokeDashoffset: 0,
        easing: 'linear',
        duration: duration,
      });
    }

    // This timeline is the Kategory logo, tag, and rest of the
    // page timed injection animation
    var pageInjectionTimeline = anime.timeline()
      .add({
        targets: '.indirect-injection-first',
        opacity: 1,
        duration: 150,
        easing: 'easeInCubic'
      })
      .add({
        targets: '.indirect-injection-next',
        opacity: 1,
        duration: 300,
        easing: 'easeInSine'
      })
      .add({
        targets: '.indirect-injection-final',
        opacity: 1,
        duration: 600,
        easing: 'easeInSine'
      });

      var pg = particleground(document.getElementById('masthead-background'), {
        dotColor: '#fff',
        lineColor: '#fff',
        density: 18000,
        parallaxMultiplier: 18,
        lineWidth: 0.5,
        proximity: 90,
        particleRadius: 5
      });

      // $("#masthead, #navigation").on("mousemove",function(e) {
      $(window).on("mousemove",function(e) {
        var ax = -($(window).innerWidth()/2- e.pageX)/120;
        var ay = ($(window).innerHeight()/2- e.pageY)/50;
        $(".masthead-inner").attr("style", "transform: rotateY("+ax+"deg) rotateX("+ay+"deg)");
      });

      $("#masthead").on("mouseleave",function(e) {
        $(".masthead-inner").attr("style", "transform: rotateY(0deg) rotateX(0deg); transition: all .5s ease;");
      });

});
