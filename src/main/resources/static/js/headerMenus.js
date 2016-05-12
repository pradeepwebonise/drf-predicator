/**
 * Created with JetBrains PhpStorm.
 * User: Kalashri Aundhkar <kalashri.aundhkar@weboniselab.com>
 * Date: 5/12/14
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * @Description: ajax call request.here we have two different request type for IE and for other browsers
 * @param url
 * @param navigationBarId
 * @private
 */

var _headerMenus = function (url,navigationBarId,staticUrl){
    var staticUrl =staticUrl;
    if(window.XDomainRequest){
        var xdr = new XDomainRequest();
        xdr.open("GET", url);
        xdr.onprogress = function () { };
        xdr.ontimeout = function () { };
        xdr.onload = function() {
            var dataSource = $.parseJSON(xdr.responseText);
            var menuUl = document.createElement("ul");
            menuUl.className = navigationBarId;
            _mainMenuHeader(dataSource,menuUl,navigationBarId);
            _menuChangeEvent();
            _subMenuPos();
        }
        xdr.onerror = function() {
            $.getJSON(staticUrl, function(json){
                var dataSource = json;
                var menuUl = document.createElement("ul");
                menuUl.className = navigationBarId;
                _mainMenuHeader(dataSource,menuUl,navigationBarId);
                _menuChangeEvent();
                _subMenuPos();
            });
        }

        setTimeout(function () {xdr.send();}, 0);
    } else {
        $.ajax({
            url : url,
            type : 'GET',
            cache: false,
            dataType : 'json',
            error : function(data) {
                $.getJSON(staticUrl, function(json){
                    var dataSource = json;
                    var menuUl = document.createElement("ul");
                    menuUl.className = navigationBarId;
                    _mainMenuHeader(dataSource,menuUl,navigationBarId);
                    _menuChangeEvent();
                    _subMenuPos();
                });
            },
            success: function (response){
                var dataSource = response;
                var menuUl = document.createElement("ul");
                menuUl.className = navigationBarId;
                _mainMenuHeader(dataSource,menuUl,navigationBarId);
                _menuChangeEvent();
                _subMenuPos();
            }
        });
    }
}
/**
 * @Description: This method will print all the primary menus first and then print all the child menus.
 * @param dataSource
 * @param menuUl
 * @param navigationBarId
 * @private
 */
var _mainMenuHeader = function(dataSource,menuUl,navigationBarId){
    if(dataSource && dataSource != null) {
        $.each(dataSource, function(index, menu) {
            var subHeaderMenu = menu.menu;

            if(menu && menu.title == 'Home') {
                //Create Home Menu
                var firstLevelHomeLi = document.createElement('li');
                firstLevelHomeLi.className = 'drfNav';

                var anchorHome = document.createElement('a');
                anchorHome.className = "icon-drfMenu";
                anchorHome.href = menu.link;
                anchorHome.innerHTML = menu.title;
                anchorHome.setAttribute('target', '_self');

                firstLevelHomeLi.appendChild(anchorHome);
                menuUl.appendChild(firstLevelHomeLi);

                $("#headerMenu").append(menuUl);

            } else if((subHeaderMenu && subHeaderMenu.length) &&  (subHeaderMenu[0]['menu'] &&  subHeaderMenu[0]['menu'].length)) {

                //Print menus which have submenu hierarchy
                var className = _uniqueClass(menu);
                var firstLevelHomeLi = document.createElement('li');
                firstLevelHomeLi.className = className;

                var anchorHome = document.createElement('a');
                anchorHome.href = menu.link;
                anchorHome.innerHTML = menu.title;
                anchorHome.setAttribute('target', '_self');
                firstLevelHomeLi.appendChild(anchorHome);

                menuUl.appendChild(firstLevelHomeLi);

                $("#headerMenu").append(menuUl);


            } else {

                //print menue which have submenus but not hierarchy

                var className = _uniqueClass(menu);

                var firstLevelHomeLi = document.createElement('li');
                firstLevelHomeLi.className = className;

                var anchorHome = document.createElement('a');
                anchorHome.href = menu.link;
                anchorHome.innerHTML = menu.title;
                firstLevelHomeLi.appendChild(anchorHome);
                anchorHome.setAttribute('target', '_self');
                menuUl.appendChild(firstLevelHomeLi);
                $("#headerMenu").append(menuUl);

            }

        });
        _createChildSubMenus(dataSource,navigationBarId);
    }
}
/**
 * @Description: Create array of subMenus and its child menu.
 * @param dataSource
 * @param navigationBarId
 * @private
 */
var _createChildSubMenus = function(dataSource,navigationBarId){

    $.each(dataSource, function(index, menu) {
        var menuArray = [] , counter = 0 ,subHeaderMenu = menu.menu;

        //print only 1st home menu
        var className = _uniqueClass(menu);

        if(menu && menu.title == 'Home'){
            return;
        }
        else if((subHeaderMenu && subHeaderMenu.length)  &&  (subHeaderMenu[0]['menu'] &&  subHeaderMenu[0]['menu'].length)) {
            var subMenuDiv = document.createElement('div');
            subMenuDiv.className = "subMenu " + className.toLowerCase() + "SubMenu";
            //function to create array of  submenus with defined order
            _createHeaderMenuArray(menu.menu,menuArray,counter);

            for(var childMenu = 0; childMenu < menuArray.length; childMenu++) {

                var subMenuDivColumn = document.createElement('div');
                subMenuDivColumn.className = "colMenu column" + childMenu;

                $.each(menuArray[childMenu], function(childCount, childList) {


                    var childColumn = document.createElement('ul');

                    var childColumnLi = document.createElement('li');
                    var heading = document.createElement('h2');
                    if(childList.link!="" && childList.link!="<front>"){
                        var childAchor = document.createElement('a');
                        childAchor.href =  childList.link;
                        childAchor.innerHTML = childList.title;
                        childAchor.setAttribute('target', '_self');
                        heading.appendChild(childAchor);
                    }else{
                        heading.innerHTML = childList.title;
                    }
                    childColumnLi.appendChild(heading);
                    childColumn.appendChild(childColumnLi);

                    $.each(childList.menu, function(key,value) {

                        _createHTML(childColumn,value.link, value.title);
                    });
                    subMenuDivColumn.appendChild(childColumn);

                });
                subMenuDiv.appendChild(subMenuDivColumn);

            }
            $('ul.'+navigationBarId).find('li.'+className).append(subMenuDiv);
            _subMenuArrow(navigationBarId,className);

        }else{
            if(subHeaderMenu && subHeaderMenu.length) {
                var className = _uniqueClass(menu);
                var subMenuDiv = document.createElement('div');
                subMenuDiv.className = "subMenu " + className.toLowerCase() + "SubMenu";

                var singleMenuColumnDiv = document.createElement('div');
                singleMenuColumnDiv.className = "colMenu";

                var singleColumn = document.createElement('ul');

                $.each(menu['menu'], function(index, child) {

                    _createHTML(singleColumn,child.link, child.title);

                });

                singleMenuColumnDiv.appendChild(singleColumn);
                subMenuDiv.appendChild(singleMenuColumnDiv);
                $('ul.'+navigationBarId).find('li.'+className).append(subMenuDiv);
                _subMenuArrow(navigationBarId,className);
            }
        }

    });

}

/*
 $(document).ajaxComplete(function(){
 _menuChangeEvent();
 _subMenuPos();
 });
 */


/**
 * @Description: Create array of menus which have hierarchy of subMenus inside it .
 * We should have only 6 columns inside every menue .
 * at most 3 sub menus are allowed in one column.
 * If there are 6 or less than 6 child submenus are in one submenu then only we can add 3 submenus in one column.
 *
 * @param SubMenu
 * @param menuArray
 * @param counter
 * @private
 */
var _createHeaderMenuArray = function(SubMenu,menuArray,counter){

    var menuLevelFirst = 1 ,menuLevelSecond = 2,menuLevelThird = 3, menuRangeSix = 6 , menuRangeNine = 9;
    var index = 0 , countInner = 0;
    var childMenu =SubMenu;

    for(var menuIterator = 0 ;menuIterator<childMenu.length;menuIterator++) {
        var childMenuLength = childMenu[menuIterator]['menu'].length;

        if(counter == 0) {

            menuArray[index]=[];
            menuArray[index][countInner] = childMenu[menuIterator];
            countInner++;
            counter++;

        } else if((menuArray[index].length == menuLevelFirst )){

            if(menuArray[index][0]['menu'].length <= menuRangeSix && childMenuLength <= menuRangeSix) {
                menuArray[index][countInner] = childMenu[menuIterator];
                countInner ++;
                counter++;
            }else if(menuArray[index][0]['menu'].length <= menuRangeNine && childMenuLength <= menuRangeNine){
                menuArray[index][countInner] = childMenu[menuIterator];
                counter++;
                countInner = 0;
            }else{
                index++;
                counter++;
                countInner = 0;
                menuArray[index] = [];
                menuArray[index][countInner] = childMenu[menuIterator];
                countInner++;
            }
        }else if(menuArray[index].length == menuLevelSecond) {

            var menuIndexCount=[];
            $.each(menuArray[index],function(keyMenu,value){

                menuIndexCount.push(value.menu.length);
            });

            if((menuIndexCount[0]<= menuRangeSix && menuIndexCount[1]<= menuRangeSix)  && childMenuLength <= menuRangeSix ){

                menuArray[index][countInner] = childMenu[menuIterator];
                countInner++;
                counter++;

            }else{

                index++;
                counter++;
                countInner = 0;
                menuArray[index] = [];
                menuArray[index][countInner] = childMenu[menuIterator];
                countInner++;
            }

        }else if(menuArray[index].length == menuLevelThird){

            index++;
            counter++;
            countInner = 0;
            menuArray[index] = [];
            menuArray[index][countInner] = childMenu[menuIterator];
            countInner++;
        }

    }
}

//Create li and anchor structure
var _createHTML = function(childColumn,linkVal,linkTitle){
    var childLi = document.createElement('li');
    var childAchor = document.createElement('a');
    childAchor.href = linkVal;
    childAchor.innerHTML = linkTitle;
    childAchor.setAttribute('target', '_self');
    childLi.appendChild(childAchor);
    childColumn.appendChild(childLi);
}


/*change event on device*/
var _menuChangeEvent = function() {
    var ua = navigator.userAgent;
    if((ua.match(/Android/i) || ua.match(/webOS/i) || ua.match(/iPhone/i) || ua.match(/iPad/i) || ua.match(/iPod/i) || ua.match(/BlackBerry/i) || ua.match(/Windows Phone/i))) {
        $('.menuNav').addClass("touch");
    } else {
        $(".menuNav").addClass('desktop');
    }

    $(".menuNav.desktop > li").hover(function() {
        $(this).children('.subMenu').addClass('subMenuActive');
    }, function() {
        $('.menuNav .subMenu').removeClass('subMenuActive');
    });

    $(".menuNav ul li a").click(function() {
        $('.menuNav .subMenu').removeClass('subMenuActive');
    });

    $('.menuNav.touch > li > a').click(function() {
        var colMenuWidth = 0;
        var containerWidth = $(".container").width();
        var containerOffSet = $(".container").offset().left;
        var menuWidth = $(this).parent().width();
        var menuOffSet = $(this).offset().left;
        var remainingWidth = containerWidth - menuOffSet + containerOffSet;

        $(this).next().find(".colMenu").each(function(){
            colMenuWidth = colMenuWidth+$(this).outerWidth(true);
        });
        $(this).next().width(colMenuWidth);
        $(this).next().find(".colMenu").height($(this).next().height()-16);
        if( remainingWidth < colMenuWidth){
            $(this).next().css("right", - ( remainingWidth - menuWidth));
        } else {
            $(this).next().css("right","inherit");
        }

        if(!$(this).hasClass('icon-drfMenu')) {
            if($(this).next('.subMenu').hasClass('subMenuActive')) {
                $('.menuNav .subMenu').removeClass('subMenuActive');
                $('.searchBox').animate({
                    height: '0'
                }, function(){
                    $('.searchWrap').removeClass('searchOpen');
                });
            } else {
                $('.menuNav .subMenu').removeClass('subMenuActive');
                $(this).next('.subMenu').addClass('subMenuActive');
                $('.searchBox').animate({
                    height: '0'
                }, function(){
                    $('.searchWrap').removeClass('searchOpen');
                });
            }
            return false;
        }
    });
    /*onbody click close DRF.com sub menu*/
    $('.container').click(function() {
        $('.menuNav .subMenu').removeClass('subMenuActive');
    });
};

var _subMenuPos = function(){
    $(".menuNav.desktop > li > a").hover(function(){
        var colMenuWidth = 0;
        var containerWidth = $(".container").width();
        var containerOffSet = $(".container").offset().left;
        var menuWidth = $(this).parent().width();
        var menuOffSet = $(this).offset().left;
        var remainingWidth = containerWidth - menuOffSet + containerOffSet;

        $(this).next().find(".colMenu").each(function(){
            colMenuWidth = colMenuWidth+$(this).outerWidth(true);
        });
        $(this).next().width(colMenuWidth);
        $(this).next().find(".colMenu").height($(this).next().height()-16);
        if( remainingWidth < colMenuWidth){
            $(this).next().css("right", - ( remainingWidth - menuWidth));
        } else {
            $(this).next().css("right","inherit");
        }
    });
};
var _subMenuArrow = function(navigationBarId,className){
    if($('ul.'+navigationBarId).find('li.'+className).children('div').length > 0) {
        $('ul.'+navigationBarId).find('li.'+className).children('div').prev('a').addClass('subMenuArrow');
    }
}

var _uniqueClass = function(menu){
    var menuWeight =Math.abs(menu.weight),uniqueClassValue;
    var className = $.trim(menu.title).split(' ');
    return uniqueClassValue = $.trim(className[0]+menuWeight);
}