$(document).ready(function(){

    //Loads the global header menu
	var serviceURL="http://home.drf.com/drf-homepage-service/headerMenu";
	var staticURL = '/resources/staticHeaderMenu.json';
	_headerMenus(serviceURL,"menuNav",staticURL);	

	_searchBox();
	_init();
	_menuChangeEvent();
	_subMenuPos();
	_userNav();
	_formatDates();
	_expandResults();
	_loggedInDropdown();
	_logout();
	_videoLinks();
	_hideUnsupportedVidLinks();
});

/* Search onclick open and close */
var _searchBox = function() {
	function close() {
		$('.menuNav .subMenu').removeClass('subMenuActive');
		$('.searchBox').animate({
			height: '0'
		}, function(){
			$('.searchWrap').removeClass('searchOpen');
		});
	}

	function open() {
		$('.menuNav .subMenu').removeClass('subMenuActive');
		$('.searchWrap').addClass('searchOpen');
		$('.searchBox').animate({
			height: '42px'
		});
	}

    $('#searchBox').submit(function(event){
        event.preventDefault();
        var searchTerms = encodeURIComponent($('#searchTerms').val());
        var searchUrl = "http://www.drf.com/search/apachesolr_search/"+searchTerms;
        window.open(searchUrl);
        $('#searchTerms').val("");
        close();
    });

	$('.icon-search').click(function() {
		if ($('.searchWrap').hasClass('searchOpen')) {
			close();
		} else {
			open();
		}
	});

	$('body').click(function() {
		if ($('.searchWrap').hasClass('searchOpen')) {
			$('.searchBox').animate({
				height: '0'
			}, function(){
				$('.searchWrap').removeClass('searchOpen');
			});
		}
	});

	$('.searchWrap').click(function(event) {
		event.stopPropagation();
	});

	$("body").on("click",".searchWrap .icon-search",function(e){
    		$(this).next(".searchBox").stop().slideToggle("slow");
    		e.stopPropagation();
    		e.preventDefault();
    	});
};

var _init = function() {
	var touchBtn = $('.responsiveButton'),
		body = $('body'),
		vsMenu = $('.vsMenu'),
		html = $('html'),
		vsMenuPosition = parseInt(vsMenu.css('right')),
		vsMenuWidth = parseInt(vsMenu.width()),
		windowWidth = parseInt($(window).width());

	var _open = function(this_) {
		this_.addClass('vsMenuOpen');
		vsMenu.scrollTop(0);
		vsMenu.animate({
			right: '0'
		});
		body.animate({
			right: vsMenuWidth
		});
		body.addClass('vsMenuBody');
		body.css({
			position: 'fixed',
			overflow: 'hidden',
			width: windowWidth
		});
		$('.headerWrap').css({
			width: windowWidth
		});
	}
	var _close = function(this_) {
		this_.removeClass('vsMenuOpen');
		vsMenu.animate({
			right: -vsMenuWidth
		}, function() {
			vsMenu.removeAttr('style');
		});
		body.stop().animate({
			right: '0'
		}, function() {
			body.removeClass('vsMenuBody');
			body.removeAttr('style');
			$('.headerWrap').removeAttr('style');
		});

	}

	touchBtn.click(function(e) {
		e.preventDefault();
		vsMenuPosition = parseInt(vsMenu.css('right'));
		if (vsMenuPosition == -vsMenuWidth) {
			_open($(this));
		} else {
			_close($(this));
		}
	});

	$('body').click(function() {
		vsMenuPosition = parseInt(vsMenu.css('right'));
		if (vsMenuPosition === 0) {
			_close(touchBtn);
		}
	});
	$('body').on("click", ".vsMenu", function(event) {
		event.stopPropagation();
	});
	$(window).resize(function() {
		windowWidth = parseInt($(window).width());
		vsMenuWidth = parseInt(vsMenu.width());
		touchBtn = $('.responsiveButton');
		if (windowWidth > 767) {
			if (touchBtn.hasClass('vsMenuOpen')) {
				touchBtn.trigger('click');
			}
		}
	});
};

var _userNav = function() {
 	$(document).on("click", ".userName", function() {
 		if ($(".userNav").hasClass("userNavActive")) {
			$(".userNav").removeClass("userNavActive");
 		} else {
 			$(".userNav").addClass("userNavActive");
 		}
 		return false;
 	});
 	/*onbody click close DRF.com sub menu*/
 	$(document).on("click", "body", function() {
 		if ($(".userNav").hasClass("userNavActive")) {
 			$(".userNav").removeClass("userNavActive");
 		}
 	});
 };

//Formats the daily results date ex/ Sunday, February 21
var _formatDates = function() {
    $(".dateStr").each(function() {
        var rawDateStr = $(this).data("date").toString();
        var date = new Date(parseInt(rawDateStr.slice(0,4)),parseInt(rawDateStr.slice(4,6))-1, parseInt(rawDateStr.slice(6,8)));

        var dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
        var monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
        var dateStr = dayNames[date.getDay()] + ", " + monthNames[date.getMonth()] + " " + date.getDate();
        $(this).text(dateStr.toString());
    });
 };

var _expandResults = function() {
     $('body').on("click",".stakeResultList h3",function(e){
         var trigger = $(this);
         var content = trigger.next('.resultData');
         if (!$(this).hasClass('active')) {
             content.stop(true, true).slideDown("slow");
             $('.stakeResultList h3').removeClass('active');
             trigger.addClass('active');
             return false;
         } else {
           content.stop(true, true).slideUp("slow");
           trigger.removeClass('active');
         }
         e.preventDefault();
     });

     $('.btnViewDetails').on("click", function(){
     	$(this).toggleClass('active');
     	$(this).parents('.resultRaceTopDesc').next('.resultDetlChart').slideToggle("slow");

     });
  };

var _loggedInDropdown = function() {
    //Set the login link to redirect back correctly
    $('.loginLink').attr("href", "http://access.drf.com/?refferUrl="+document.URL);

    if(Cookies.get('DRF_SSO_AUTH')){
         var ssoAuth = JSON.parse(Cookies.get('DRF_SSO_AUTH'));
         var userName = ssoAuth.userName;
         $('.displayUserName').text(userName);
     }

     if(typeof ssoAuth === "undefined") {
        $('.loginHide').show();
        $('.loginShow').hide();
     }
     else {
        $('.loginHide').hide();
        $('.loginShow').show();
     }
 };

var _logout = function() {
    $('.logout').on('click', function() {
        Cookies.remove("DRF_SSO_AUTH", {path: "/",domain:".drf.com"});
        Cookies.remove('cookieExpire');
        Cookies.remove('currentUser');
        if(Cookies.get('posturl')){
            Cookies.remove('posturl');
        }

    location.reload();
    });
 }

var _videoLinks = function() {
    $("body").on("click", ".raceVideo",function(){
		var vidUrl = $(this).data("vid-url");

		window.open(vidUrl,'Racevideo','width=340,height=300,scrollbars=no,resizable=no,toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0');
        });
}

var _hideUnsupportedVidLinks = function() {
	Modernizr.on('flash', function( hasFlash ) {
		if(!hasFlash) {
			$(".btnReplay").each(function() {
				$(this).hide();
			});
		}
	});
}
