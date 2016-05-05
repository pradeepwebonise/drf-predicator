$(document).ready(function(){
	$('body').on("click",".stakeResultList h3",function(e){
		e.preventDefault();
		var trigger = $(this);
	    var content = trigger.next('.resultData');
	    if(!$(this).parents("li").hasClass("active")){
		    if (!$(this).hasClass('active')) {
		        $('.active').next('.resultData').stop(true, true).slideUp();
		        $('.stakeResultList h3').removeClass('active');
		        trigger.addClass('active');
		        content.stop(true, true).slideDown();
		        return false;
		    } else {
		        trigger.removeClass('active');
		        content.stop(true, true).slideUp();
		    }	   
	    }
	});
	
	$("body").on("click",".resultRaceTopDesc .btnViewMore",function(e){
		e.preventDefault();
		var trigger = $(this);
		var content = trigger.parents(".resultRaceTopDesc").next(".resultDetlChart");	
		$(content).stop().slideToggle("slow");		
	});

	$("body").on("click",".searchWrap .icon-search",function(e){
		e.preventDefault();
		$(this).next(".searchBox").stop().slideToggle("slow");
		e.stopPropagation();
	});

	$("body").on("click",function(e){
		$(".searchBox").slideUp("slow");
	});
});