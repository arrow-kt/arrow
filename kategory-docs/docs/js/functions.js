(function($) {
    "use strict";

    // Scroll
    $(window).scroll(function() {
        if ($("#navigation").offset().top > 70) {
            $("#navigation").addClass("navigation-scroll");
        } else {
            $("#navigation").removeClass("navigation-scroll");
        }
    });

    // Sidebar toggle
    $(".sidebar-toggle").click(function(e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });

    // Sidebar nav
    $(document).ready(function() {
        var speed = 300;

        // Touch interactions
        var sidebarWrapperEl = document.getElementById('sidebar-wrapper');
        // create a simple instance, by default it only adds horizontal recognizers
        if (sidebarWrapperEl) {
          var sidebarWrapperTouch = new Hammer(sidebarWrapperEl);
          // listen to events...
          sidebarWrapperTouch.on("swiperight", function(ev) {
              ev.preventDefault()
              $("#wrapper").addClass("toggled");
          });
          sidebarWrapperTouch.on("swipeleft", function(ev) {
              ev.preventDefault()
              $("#wrapper").removeClass("toggled");
          });
        }

        $('.sidebar-nav > li > a').click(function(e) {
            e.preventDefault();
            if (!$(this).parent().hasClass('active')) {
                $('.sidebar-nav li ul').slideUp(speed);
                $(this).next().slideToggle(speed);
                $('.sidebar-nav li').removeClass('active');
                $(this).parent().addClass('active');
            } else {
                $(this).next().slideToggle(speed);
                $('.sidebar-nav li').removeClass('active');
            }
        });
    });
})(jQuery);
