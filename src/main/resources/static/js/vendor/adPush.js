var googletag = googletag || {};
googletag.cmd = googletag.cmd || [];
(function() {
	var gads = document.createElement('script');
	gads.async = true;
	gads.type = 'text/javascript';
	var useSSL = 'https:' == document.location.protocol;
	gads.src = (useSSL ? 'https:' : 'http:')
			+ '//www.googletagservices.com/tag/js/gpt.js';
	var node = document.getElementsByTagName('script')[0];
	node.parentNode.insertBefore(gads, node);
})();

var url = window.location.href;
googletag.cmd.push(function() {
	googletag.defineSlot(
			'/25704096/DRF_300x1050_300x600_300x500_300x250_320x50_ATF_1',
			[ [ 300, 500 ], [ 300, 1050 ], [ 300, 600 ], [ 300, 250 ],
					[ 320, 50 ] ], 'div-gpt-ad-1434395586969-2').addService(
			googletag.pubads());
	googletag.defineSlot('/25704096/DRF_300x600_300x250_ATF_2',
			[ [ 300, 250 ], [ 300, 600 ] ], 'div-gpt-ad-1435083304219-0')
			.addService(googletag.pubads());
	googletag.defineSlot('/25704096/DRF_728x90_ATF_1', [ 728, 90 ],
			'div-gpt-ad-1434395586969-7').addService(googletag.pubads());
	googletag.defineSlot('/25704096/DRF_115x45_ATF_1', [115, 45],
			'div-gpt-ad-1455634640662-0').addService(googletag.pubads());
	googletag.pubads().setTargeting("URL", url);
	googletag.pubads().setTargeting("CATEGORY", "results");
	googletag.pubads().setTargeting("APP", "spro");
	googletag.pubads().enableSingleRequest();
	googletag.pubads().collapseEmptyDivs();
	googletag.enableServices();
});