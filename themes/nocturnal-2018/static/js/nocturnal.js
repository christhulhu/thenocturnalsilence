$(document).ready(function(){
	if ($(".vanillabox").length) {
		$(".vanillabox").vanillabox();
	}
	if (!$("body").hasClass("home")) {
		$('html, body').animate({
			scrollTop: $("#content").offset().top
		}, 1000);
	}
	if ($(document).height()>= $('.main').height()) {
		$('.main').height($(document).height());
	}
})
