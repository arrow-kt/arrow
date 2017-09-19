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

        function activate (el, speed) {
          if (!el.parent().hasClass('active')) {
              $('.sidebar-nav li ul').slideUp(speed);
              el.next().slideToggle(speed);
              $('.sidebar-nav li').removeClass('active');
              el.parent().addClass('active');
          } else {
              el.next().slideToggle(speed);
              $('.sidebar-nav li').removeClass('active');
          }
        }

        $('.sidebar-nav > li > a').click(function(e) {
            e.preventDefault();
            activate($(this), 300);
        });

        var current = location.pathname;
          $('.sidebar-nav > li > ul a').each(function(){
              var $this = $(this);
              // if the current path is like this link, make it active
              if($this.attr('href') === current){
                  $this.addClass('active');
                  activate($this.closest('.sidebar-nav > li').children('a'), 0);
              }
        })
    });
})(jQuery);
