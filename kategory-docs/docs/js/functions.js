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
