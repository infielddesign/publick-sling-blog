!function a(b,c,d){function e(g,h){if(!c[g]){if(!b[g]){var i="function"==typeof require&&require;if(!h&&i)return i(g,!0);if(f)return f(g,!0);throw new Error("Cannot find module '"+g+"'")}var j=c[g]={exports:{}};b[g][0].call(j.exports,function(a){var c=b[g][1][a];return e(c?c:a)},j,j.exports,a,b,c,d)}return c[g].exports}for(var f="function"==typeof require&&require,g=0;g<d.length;g++)e(d[g]);return e}({1:[function(a,b){var c={bannerImageWrapper:$(".hero-bg"),bannerImageOffset:.25,blogImageWrapper:$(".news-list-view .news-teaser-img-wrap, .news-featured-view .news-teaser-img-wrap"),blogImageOffset:.5,imageGrid:{"class":"image-grid",$items:$(".image-grid-item"),linkLocation:".csc-header a",overlayLocation:".csc-textpic-text",textOffset:.5},parallaxElements:[$("#wrapper [parallax-wrapper]")],mobileNavWidth:"60%",mobileNavAnimTime:200,$touchElements:[$(".case-study-teaser"),$(".work-grid .csc-textpic-imagerow"),$(".nav-primary .menu-item-toggle"),$(".image-grid-item")]};b.exports=c},{}],2:[function(a,b){var c={$toggleButton:$("[menu-toggle-button]"),inMobileScreenSize:null,headerHeight:function(){return $("header").height()},pageHeight:function(){return window.innerHeight-this.headerHeight()},offsetFromTop:function(a){if(a){var b=-a[0].getBoundingClientRect().top;return b}var c=-$("#wrapper")[0].getBoundingClientRect().top;return c},checkScreenSize:function(){return this.inMobileScreenSize=this.$toggleButton.is(":visible"),this},initialize:function(){this.checkScreenSize(),$(window).resize(function(){c.checkScreenSize()})}};b.exports=c},{}],3:[function(a,b){var c={initialize:function(){$(".fancybox").fancybox()}};b.exports=c},{}],4:[function(a,b){var c=a("./config.js"),d={setWrapper:function(a){function b(a){return this.$wrapper.each(function(){var b=$(this),c=b.find("img"),d=b.height()-c.height();c.css({top:d*a+"px"})}),this}function c(){return this.$wrapper.each(function(){var a=$(this).find("img");a.addClass(a.width()>a.height()?"landscape":"portrait")}),this}var d={$wrapper:a,valign:b,setOrientationClass:c};return d},initialize:function(){d.setWrapper(c.bannerImageWrapper).valign(c.bannerImageOffset),d.setWrapper(c.blogImageWrapper).valign(c.blogImageOffset),$(window).resize(function(){d.setWrapper(c.bannerImageWrapper).valign(c.bannerImageOffset),d.setWrapper(c.blogImageWrapper).valign(c.blogImageOffset)})}};b.exports=d},{"./config.js":1}],5:[function(a,b){var c=a("./config.js"),d=a("./imageAlign.js"),e={$items:c.imageGrid.$items,$itemImages:function(){return this.$items.find("img")},createGrid:function(){return this.$items.wrapAll('<div class="'+c.imageGrid.class+'"></div>'),this},setItemLinks:function(){return this.$items.each(function(){var a=$(this),b=a.find(c.imageGrid.linkLocation);b.length>0&&(a.wrap('<a class="'+c.imageGrid.class+'-link" href="'+b.attr("href")+'" ></a>'),b.removeAttr("href"))}),this},valignText:function(){return this.$items.find(c.imageGrid.overlayLocation).each(function(){var a=$(this),b=0,d=a.height(),e=a.children();e.each(function(){b+=$(this).height()}),$(e[0]).css("margin-top",(d-b)*c.imageGrid.textOffset)}),this},initialize:function(){e.createGrid().setItemLinks().valignText(),d.setWrapper(this.$items).setOrientationClass()}};b.exports=e},{"./config.js":1,"./imageAlign.js":4}],6:[function(a){var b=a("./domInfo.js"),c=a("./miscFixes.js"),d=a("./navigationBar.js"),e=a("./parallax.js"),f=a("./touchLogic.js"),g=a("./imageAlign.js"),h=a("./fancybox.js"),i=a("./imageGrid.js");window.onload=function(){b.initialize(),i.initialize(),c.initialize(),d.initialize(),e.initialize(),f.initialize(),g.initialize(),h.initialize()}},{"./domInfo.js":2,"./fancybox.js":3,"./imageAlign.js":4,"./imageGrid.js":5,"./miscFixes.js":7,"./navigationBar.js":8,"./parallax.js":9,"./touchLogic.js":10}],7:[function(a,b){var c={workGridLinks:function(){if($(".work-grid"))for(var a=$(".work-grid figure"),b=0;b<a.length;b++){var c=$(a[b]);if(c.find("figcaption")[0]){var d=c.find("a").attr("href");c.find("figcaption").wrap('<a href="'+d+'"></a>')}}return this},initialize:function(){this.workGridLinks()}};b.exports=c},{}],8:[function(a,b){var c=a("./config.js"),d=a("./domInfo.js"),e={$toggleButton:$("[menu-toggle-button]"),$main:$("[role='main']"),$header:$("[role='header']"),$footer:$("[role='footer']"),$navMenu:$("[nav-menu]"),$nav:$("[nav]"),$navExit:$("[nav-menu-exit]"),$navPrimary:$(".nav-primary"),isExpanded:!1,thinNavIfScrollPastThis:5*d.headerHeight(),$shiftOnMenuToggle:function(){return this.$main.add(this.$header).add(this.$footer)},showMenu:function(){return e.isExpanded=!0,this.$nav.addClass("active"),this.$shiftOnMenuToggle().animate({left:c.mobileNavWidth},c.mobileNavAnimTime),this.$navMenu.add(this.$navExit).css({height:$("body").height()}),this},hideMenu:function(){return this.isExpanded=!1,this.$nav.removeClass("active"),this.$shiftOnMenuToggle().animate({left:0},c.mobileNavAnimTime),setTimeout(function(){e.$navMenu.add(e.$navExit).css({height:""})},c.mobileNavAnimTime),this},stripStyling:function(){var a=this.$shiftOnMenuToggle().add(this.$navMenu).add(this.$navExit);return this.isExpanded=!1,this.$nav.removeClass("active"),a.css({height:"",left:"",top:""}),this},enableMobileMenu:function(){return this.$toggleButton.on("mousedown",function(){e.isExpanded?e.hideMenu():e.showMenu()}),this.$navExit.on("mousedown",function(){e.hideMenu()}),this},autoClose:function(){return this.isExpanded&&(d.inMobileScreenSize?d.offsetFromTop()>this.$navPrimary.height()&&this.hideMenu():this.stripStyling()),this},thinNav:function(){return d.offsetFromTop()>this.thinNavIfScrollPastThis?this.$nav.addClass("thin"):this.$nav.removeClass("thin"),this},offsetContent:function(){return d.inMobileScreenSize?this.$main.css("margin-top",""):this.$main.css("margin-top",d.headerHeight()+"px"),this},onLoadCalls:function(){this.offsetContent()},enableResizeCalls:function(){return $(window).resize(function(){e.autoClose(),e.offsetContent()}),this},enableScrollCalls:function(){return $(document.body).scroll(function(){e.thinNav(),e.autoClose()}),this},initialize:function(){return this.onLoadCalls(),this.enableResizeCalls(),this.enableScrollCalls(),this.enableMobileMenu(),this}};b.exports=e},{"./config.js":1,"./domInfo.js":2}],9:[function(a,b){var c=a("./config.js"),d=a("./domInfo.js"),e={apply:function(a,b){if(d.inMobileScreenSize)b.css({"margin-top":""});else{var c=d.offsetFromTop(a)/2+"px";b.css({"margin-top":c})}},applyToAll:function(){for(var a=c.parallaxElements.length;a--;){var b=c.parallaxElements[a],d=b.find("img");e.apply(b,d)}},initialize:function(){e.applyToAll(),$(document.body).scroll(e.applyToAll).resize(e.applyToAll)}};b.exports=e},{"./config.js":1,"./domInfo.js":2}],10:[function(a,b){var c=a("./config.js"),d=a("./domInfo.js"),e={mouseoverClass:"mouseover",mouseover:function(a){return $("."+this.mouseoverClass).removeClass(this.mouseoverClass),$(a).addClass(this.mouseoverClass),this},mouseout:function(a){return $(a).removeClass(this.mouseoverClass),this},toggleHover:function(a){return $(a).hasClass(this.mouseoverClass)?this.mouseout(a):this.mouseover(a),this},delegateEventsByScreenSize:function(){if(d.inMobileScreenSize)for(var a=0;a<c.$touchElements.length;a++)c.$touchElements[a].off("mouseout").off("mouseover"),c.$touchElements[a].on("click",function(){e.toggleHover(this)});else for(var a=0;a<c.$touchElements.length;a++)c.$touchElements[a].on("mouseover",function(){e.mouseover(this)}).on("mouseout",function(){e.mouseout(this)}),c.$touchElements[a].off("click")},initialize:function(){this.delegateEventsByScreenSize(),$(window).resize(function(){e.delegateEventsByScreenSize()})}};b.exports=e},{"./config.js":1,"./domInfo.js":2}]},{},[6]);