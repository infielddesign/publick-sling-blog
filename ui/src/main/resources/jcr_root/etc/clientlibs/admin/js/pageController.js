/**
 * Angular controller to save settings through AJAX posts and display the proper
 * success or failure message. Works with all settings components including
 * system settings, email config, and reCAPTcha config.
 */
app.controller('PageListController', function($scope, $http, formDataObject, ngDialog, $window, $timeout) {

var rootname = "clbk"
var rootid = "#" + rootname;
var treeroot = $(rootid);
var CONTENT_PATH = "/content";
var ROOT_PATH = "/page";
$scope.content_path = CONTENT_PATH;
$scope.model = {
    status: "",
    msg:"",
    alert_css:""
}




/******************/
/* AJAX Functions */
/******************/

/**
 *  This code gets the a tree starting a page of depth as a JSON object.
**/
function get(page, depth) {
  return $http({
    method: 'GET',
    url: "/" + page + "." + depth + ".json"
  });
}


/**
 *  This code allows you to POST to the Sling Post Servlet.
**/
function slingPostServlet(url, params, headers, timeout) {
    timeout = typeof timeout !== 'undefined' ? timeout : 60000;
    return $http({
        method: 'POST',
        url: url,
        headers: headers,
        data: params,
        transformRequest: formDataObject,
        timeout: timeout
  });
}


/**
 *  This code sends a POST request to the Sling Post Servlet
 *  to change a node's name. This function is used in the DND
 *  feature.
**/
function renameNode(prefix_path, path_string, parent, node)
{
  var ref = treeroot.jstree(true),
  sel = ref.get_selected();

  if(!sel.length) { return false; }

  sel = sel[0];
  ref.edit(sel, null, function(node, status){
      var newText = node["text"];
      var params = {":operation": "move", ":dest": CONTENT_PATH + "/" + prefix_path + "/" + newText};
      var url = CONTENT_PATH + "/" + path_string;

      if(parent != newText){
        slingPostServlet(url, params, null);
      }
  });
}


/**
 *  This code sends a POST request to the Sling Post Servlet
 *  to change a node's path. This function is used in the DND
 *  feature.
**/
function moveNode(old_path, new_path)
{
    var params = {":operation": "move", ":applyTo": old_path, ":dest": new_path};
    var url = CONTENT_PATH + ROOT_PATH;

    if(old_path != new_path){
      slingPostServlet(url, params, null);
    }
}


/**
 *  This code sends a POST request to the Sling Post Servlet
 *  to change a node's path. This function is used in the DND
 *  feature.
**/
function copyNode(old_path)
{
    $scope.old_path = old_path;
}


/**
 *  This code sends a POST request to the Sling Post Servlet
 *  to change a node's path. This function is used in the DND
 *  feature.
**/
function pasteNode(new_path, old_path, prefix_path, node)
{
    if(old_path){
        var params = {":operation": "copy", ":applyTo": $scope.old_path, ":dest": new_path};
        var url = CONTENT_PATH + ROOT_PATH;
        var fetched_node = treeroot.jstree(true).get_node(node).parent;

        if(old_path != new_path){
          slingPostServlet(url, params, null).then(
          function(paste1){
              get(prefix_path, "2").then(
              function(paste2){
                  var tree = translate(paste2['data']);
                  treeroot.jstree(true).refresh_node(fetched_node);
              });
          },function(err){
            $scope.model.status="Error!";
            $scope.model.msg=err["data"]["title"] + ": " + err["data"]["status.message"];
            $scope.model.alert_css="alert-danger";

            $('#alert_placeholder').html('<div class = "alert '+ $scope.model.alert_css +' alert-dismissable fade in"><button type = "button" class = "close" data-dismiss = "alert" aria-hidden = "true">&times;</button><strong>' + $scope.model.status + '</strong> ' + $scope.model.msg + '</div>');
          });
        }
    }
}


/**
 *  This code sends a POST request to the Sling Post Servlet
 *  to change a node's path. This function is used in the DND
 *  feature.
**/
function deleteNode(path, prefix_path, node)
{
    var params = {":operation": "delete", ":applyTo": CONTENT_PATH + "/" + path};
    var url = CONTENT_PATH + ROOT_PATH;
    var fetched_node = treeroot.jstree(true).get_node(node).parent;

    slingPostServlet(url, params, null).then(
         function(res1){

             get(prefix_path, "2").then(
             function(res2){
                 var tree = translate(res2['data']);
                 treeroot.jstree(true).refresh_node(fetched_node);
             });
         }
    );
}


/**
 *  This code sends a POST request to the dispatcher
 *  to invalidate the page or folder in the handle path.
 *
 *  To solve the CORS issues you need to set the
 *  following values in your dispatcher.
 *
 *  Header add Access-Control-Allow-Origin "*"
 *  Header add Access-Control-Allow-Headers "origin, x-requested-with, content-type, CQ-Path, CQ-Action, CQ-Handle"
 *  Header add Access-Control-Allow-Methods "PUT, GET, POST, DELETE, OPTIONS"
 *
**/
function clearCache(url, params, handle)
{
    headers = {
    'CQ-Action': 'DELETE',
    'CQ-Handle': handle,
    'CQ-Path': handle,
    'Content-Type': 'application/octet-stream'
    }
    slingPostServlet(url, params, headers, 5000).then(
             function(cache){

                if(cache["statusText"]=="OK")
                {
                    $scope.model.status="Success!";
                    $scope.model.msg="You have successfully cleared "+ handle +".";
                    $scope.model.alert_css="alert-success";

                }
                else{
                    $scope.model.status="Warning!";
                    $scope.model.msg="Something went wrong please check that you have correctly configured your dispatcher.";
                    $scope.model.alert_css="alert-warning";
                }

                $('#alert_placeholder').html('<div class = "alert '+ $scope.model.alert_css +' alert-dismissable fade in"><button type = "button" class = "close" data-dismiss = "alert" aria-hidden = "true">&times;</button><strong>' + $scope.model.status + '</strong> ' + $scope.model.msg + '</div>');
             },
             function(err){
                if(err["statusText"]==""){
                      $scope.model.status="Warning!";
                      $scope.model.msg="Something went wrong please check that you have correctly configured your dispatcher.";
                      $scope.model.alert_css="alert-warning";
                  }

                  $('#alert_placeholder').html('<div class = "alert '+ $scope.model.alert_css +' alert-dismissable fade in"><button type = "button" class = "close" data-dismiss = "alert" aria-hidden = "true">&times;</button><strong>' + $scope.model.status + '</strong> ' + $scope.model.msg + '</div>');
             }
        );
}




/*********************/
/* Context Functions */
/*********************/

/**
 * Open page that corresponds to node selected.
 **/
function openNodeContext(url, prefix_path, parent) {
    $window.open(url + '/' + prefix_path + '/' + parent + '.html', '_blank');
}


/**
 * Open modal to create a child node with form provided in modal.
 **/
function newNodeContext(prefix_path, parent) {
    ngDialog.open({
        template : "/admin/page/edit.html?post=" + CONTENT_PATH + "/" + prefix_path + "&post2=" + parent + "&post3=new",
        className : 'ngdialog-theme-default custom-width',
        controller : 'newPageController',
        closeByEscape : true,
        scope : $scope,
        preCloseCallback: function(value) { return preCloseCallback() }
    });
}


/**
 * Open modal and load forms with nodes properties
 **/
function editNodeContext(prefix_path, parent) {
  ngDialog.open({
      template : "/admin/page/edit.html?post=" + CONTENT_PATH + "/" + prefix_path + "&post2=" + parent + "&post3=edit",
//      template : "templateId",
      className : 'ngdialog-theme-default custom-width',
      controller : 'newPageController',
      closeByEscape : true,
      scope : $scope,
      preCloseCallback: function(value) { return preCloseCallback() }
  });
}


/**
 * Open edit modal in Subpages and Page Preview tabs
 **/
$scope.editNodeContext = function(prefix_path, parent){
    editNodeContext(prefix_path, parent)
}


/**
 * This function allow you to confirm to close the modal.
 **/
function preCloseCallback() {
  var nestedConfirmDialog = ngDialog.openConfirm({
      template:'\
          <div class="modal-header">\
            <h4 class="modal-title">Attention</h4>\
          </div>\
          <div class="modal-body"><p>Close modal without saving your changes?</p></div>\
          <div class="modal-footer">\
              <button type="button" class="btn btn-primary" ng-click="confirm(1)">Yes</button>\
              <button type="button" class="btn btn-default" ng-click="closeThisDialog(0)">Cancel</button>\
          </div>',
      plain: true,
      closeByEscape : true,
  });

  // NOTE: return the promise from openConfirm
  return nestedConfirmDialog;
}


/**
 *  Called when context is invoked (right clicked on node).
**/
function customMenu(node) {

   var parent = node["text"];
   var path_string = treeroot.jstree("get_path", node,"/",false);
   var prefix_path = path_string.replace("/" + parent, "");

   //Show a different label for renaming files and folders
   if ($(node).hasClass("jstree-closed") || $(node).hasClass("jstree-open")) { //If node is a folder
      var renameLabel = "Rename Folder";
   }
   else {
      var renameLabel = "Rename File";
   }
   var items = {
      "Open" : {
          "icon": "glyphicon glyphicon-new-window",
          "label" : "Open",
          "action" : function (obj) {
            openNodeContext("", prefix_path, parent);
          },
          "_disabled": function (obj){
            var nodeType = node["original"]["properties"]["jcr:primaryType"];

            //If the node selected is a folder then disable option of opening it.
            if(nodeType == "sling:Folder"){
                return true;
            }
            else{
                return false;
            }
          }
      },
      "New" : {
          "icon": "fa fa-plus",
          "label" : "New",
          "action" : function (obj) {
            newNodeContext(prefix_path, parent);
          }
      },
      "Edit" : {
            "icon": "fa fa-pencil",
            "label" : "Edit",
            "action" : function (obj) {
                editNodeContext(prefix_path, parent);

                var url = $scope.model.dispatcherHost + $scope.model.dispatcherInvalidateCacheUri;
                var nodeType = node["original"]["properties"]["jcr:primaryType"];

                if(nodeType == "publick:page")
                {
                    var extension = ".html";
                }
                else{
                    var extension = "/";
                }

                clearCache(url, null, "/" + path_string + extension);
            },
            "_disabled": function (obj){
                var parentId = node["parent"];

                //If the node selected is the root then disable option of opening it.
                if(parentId == "#"){
                    return true;
                }
                else{
                    return false;
                }
            }
      },
      "Rename" : {
        "icon": "glyphicon glyphicon-text-color",
        "label" : "Rename",
        "action" : function (obj) {
            renameNode(prefix_path, path, parent, node);
        },
        "_disabled": function (obj){
          var parentId = node["parent"];

          //If the node selected is a folder then disable option of opening it.
          if(parentId == "#"){
              return true;
          }
          else{
              return false;
          }
        }
      },
    "Copy" : {
        "icon": "fa fa-files-o",
        "label": "Copy",
        "action": function (obj) {
            $scope.old_path = CONTENT_PATH + "/" + path_string;
            console.log(node);
            treeroot.jstree("copy", node);
//            copyNode(old_path);

        },
        "_disabled": function (obj){
            var parentId = node["parent"];

            //If the node selected is a root node then disable option of opening it.
            if(parentId == "#"){
                return true;
            }
            else{
                return false;
            }
        }
    },
    "Paste" : {
        "icon": "fa fa-clipboard",
        "label" : "Paste",
        "action" : function (obj) {
            var target = CONTENT_PATH + "/" +path_string + "/";
            var old_path = $scope.old_path;

            var currentId = node["id"];
            var parentId = node["parent"];
            var copied = treeroot.jstree("get_buffer");
            treeroot.jstree("select_node","#"+parentId);
            treeroot.jstree("paste");
            treeroot.jstree("clear_buffer");
            pasteNode(target, old_path, prefix_path, node);
        },
        "_disabled": function (obj){
              return !treeroot.jstree("can_paste");
        }
    },
    "Delete" : {
        "icon": "fa fa-trash",
        "label" : "Delete",
        "action" : function (obj) {
            deleteNode(path_string, prefix_path, node);
        },
        "_disabled": function (obj){
            var parentId = node["parent"];

            //If the node selected is a folder then disable option of opening it.
            if(parentId == "#"){
                return true;
            }
            else{
                return false;
            }
        }
    },
    "Cache" : {
        "icon": "fa fa-recycle",
        "separator_before" : true,
        "label" : "Cache",
        "action" : true,
        "submenu" :{
            "Open": {
                "icon": "glyphicon glyphicon-new-window",
                "label" : "Open",
                "action": function (obj) {
                    var url = $scope.model.dispatcherHost;
                    openNodeContext(url, prefix_path, parent);
                },
                "_disabled": function (obj){
                    var nodeType = node["original"]["properties"]["jcr:primaryType"];

                    //If the node selected is a folder then disable option of opening it.
                    if(nodeType == "sling:Folder"){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            },
            "Clear": {
                "icon": "fa fa-trash",
                "label" : "Clear",
                "action": function (obj) {
                    var url = $scope.model.dispatcherHost + $scope.model.dispatcherInvalidateCacheUri;
                    var nodeType = node["original"]["properties"]["jcr:primaryType"];

                    if(nodeType == "publick:page")
                    {
                        var extension = ".html";
                    }
                    else{
                        var extension = "/";
                    }

                    clearCache(url, null, "/" + path_string + extension);
                }
            }
        },
        "_disabled": function (obj){
            var parentId = node["parent"];

            //If the node selected is a folder then disable option of opening it.
            if(parentId == "#"){
                return true;
            }
            else{
                return false;
            }
        }
      }
   };

   //If node is a folder do not show the "delete" menu item
   if ($(node).hasClass("jstree-closed") || $(node).hasClass("jstree-open")) {
      delete items.remove;
   }

   return items;
}




/********************/
/* Listen to events */
/********************/

/**
 *  The following code listens for the select_node event.
 *  If the event is triggered then it will pretty highlight
 *  the JSON by providing it an ID.
**/
treeroot
.on('select_node.jstree', function (e, data) {
    var properties = data["node"]["original"]["properties"];
    var filteredProperties;
    var selectedNode = data["node"]["text"];

    var node = data["node"];
    var parent = node["text"];
    var prefix_path = "/" + treeroot.jstree("get_path", node,"/",false).replace("/" + parent, "") + "/";
    var prefix_path_child = "/" + treeroot.jstree("get_path", node,"/",false) + "/";

    $scope.prefix_path = prefix_path;
    $scope.prefix_path_child = prefix_path_child;

    filteredProperties = filterLevelTwoByPrimaryType(selectedNode, properties, "publick:page");
    updatePageList(filteredProperties);
    
    filteredProperties = filterLevelOneByPrimaryType(selectedNode, properties, "publick:page");
    updatePageContent(filteredProperties);
    
    $scope.$apply();
});


/**
 *  The following code listens for the dblclick event.
 *  If the event is triggered then it will let you rename
 *  a new name for the node then it will call the sling
 *  post servlet and update the resource tree.
**/
treeroot
.on('dblclick', '.jstree-anchor', function (event, data) {

    var instance = $.jstree.reference(this),
    node = instance.get_node(this);
    var path_string = treeroot.jstree("get_path", node,"/",false);
    var parent = node["text"];
    var prefix_path = path_string.replace("/" + parent, "");

    renameNode(prefix_path, path_string, parent, node);

});

    
/**
 *  The following code listens for the click event.
 *  If the event is triggered then it will toggle child
 *  nodes. But the toggle does not occur on double-click.
**/
var startedToggle = false;
var timer;
treeroot
.on('click', '.jstree-anchor', function (event, data) {
    var that = this;
    
    if (!startedToggle) {
        timer = $timeout(function() {
            toggle_node(that);
            startedToggle = false;
        }, 500);
        
        startedToggle = true;
    } else {
        $timeout.cancel(timer);
        
        startedToggle = false;
    }
});


function toggle_node(element) {
    if ($.jstree !== undefined) {
        var instance = $.jstree.reference(element);
        instance.toggle_node(element);   
    }
}


/**
 *  The following code listens for the move_node event.
 *  If the event is triggered then it means you are
 *  dragging and dropping a node to a new location so it
 *  will update the frontend and then call the sling post
 *  servlet and update the resource tree.
**/
treeroot
.on("move_node.jstree", function (e, data) {
    node = data["node"];
    var parent = node["text"];
    var prefix_path = treeroot.jstree("get_path", node,"/",false).replace("/" + parent, "");
    var old_path = CONTENT_PATH + "/" + treeroot.jstree("get_path", data["old_parent"],"/",false) + "/" + parent;
    var new_path = CONTENT_PATH + "/" + prefix_path;

    moveNode(old_path, new_path + "/");
});




/******************/
/* MISC Functions */
/******************/
/**
 *  This code translates a Sling return JSON object into a jstree
 *  accepted object. It filters out the properties and only allows
 *  for node to be represented in the JSON main tree.
**/
function translate(objects){
var tree = [];

for(var obj in objects){
var icon = "glyphicon glyphicon-file";

    var isObj = false;

    if(typeof(objects[obj]) == "object" && Object.prototype.toString.call(objects[obj]) != "[object Array]")
    {
        for(obj1 in objects[obj])
        {
            if(obj1 == "jcr:primaryType" && objects[obj][obj1]=="publick:page")
            {
                icon = "glyphicon glyphicon-file";
            }
            else if(obj1 == "jcr:primaryType" && objects[obj][obj1]=="sling:Folder")
            {
                icon = "glyphicon glyphicon-folder-open";
            }

            if((typeof(objects[obj][obj1]) == "object")){
                isArr = (Object.prototype.toString.call(objects[obj][obj1]) === "[object Array]")
                if(!isArr)
                {
                    isObj = true;
                }
            }
        }
        tree.push({"text" : obj, "icon" : icon, "properties" : objects[obj], "children" : isObj});
    }
}

return tree;
}


/**
 *  Return an object that only contains nodes of a specific type (e.g. "publick:page" or "sling:Folder")
**/
function filterLevelOneByPrimaryType(selectedNode, object, primaryType) {
    var filteredProperties = {};

    if (object["jcr:primaryType"] === primaryType) {
        filteredProperties["selectedNode"] = selectedNode;
        for (var key in object) {
            filteredProperties[key] = object[key];
        }
    }

    return filteredProperties;
}


function filterLevelTwoByPrimaryType(selectedNode, object, primaryType) {
    var filteredProperties = {};

    for (var key in object) {
        if (object.hasOwnProperty(key)) {
//            filteredProperties["selectedNode"] = selectedNode;
            var levelTwo = object[key];
            if (typeof(levelTwo) == "object")
            {
                levelTwo["text"] = key
            }
            for (var keysTwo in levelTwo) {
                if (levelTwo.hasOwnProperty(keysTwo) && keysTwo === "jcr:primaryType" && levelTwo["jcr:primaryType"] === primaryType) {
                    filteredProperties[key] = object[key];
                }
            }
        }
    }
//    filteredProperties["selectedNode"] = object["node"]["text"];

    return filteredProperties;
}


/**
 *  Update $scope.pageList.
 *  $scope.apply() is needed to update the scope afterwards.
**/
function updatePageList(object) {
    if (isEmpty(object)) {
        object = undefined;
    }
    $scope.pageList = object;
}


/**
 *  Update $scope.pageContent.
 *  $scope.apply() is needed to update the scope afterwards.
**/
function updatePageContent(object) {
    if (isEmpty(object)) {
        object = undefined;
    }
    $scope.pageContent = object;
}


/**
 * Helper: Test if object is empty
 */
function isEmpty(obj) {
    for (var prop in obj) {
        if (obj.hasOwnProperty(prop))
            return false;
    }
    return true;
}




/*******************/
/* Initialize page */
/*******************/
/**
 * This function enables you to initialize the banner with a welcoming message and
 * Initialize the jstree tree.
**/
function initialize(){

    var status="Info!";
    var msg="Welcome to the siteadmin page, here you will be able to create pages, folders and organize your site as you see fitted.";
    var alert_css="alert-info";
    $('#alert_placeholder').html('<div class = "alert '+ alert_css +' alert-dismissable fade in"><button type = "button" class = "close" data-dismiss = "alert" aria-hidden = "true">&times;</button><strong>' + status + '</strong> ' + msg + '</div>');

    /**
     *  The following code gets the sling resource tree (as JSON)
     *  starting at the /page node with a depth of 2.
     *  This code sets up the initial tree and initialises the jstree
     *  plugins that are used such as contextmenu, dnd ...
    **/
    get("page/", "2").then(function(res){
    var objects = res['data'];
    var tree = [];
    tree = translate(objects);
    tree = [{"text" : "page", "properties" : {"jcr:primaryType" : "sling:Folder"}, "icon" : "glyphicon glyphicon-folder-open", "state" : {"opened" : true, "disabled" : true, "selected" : false},"children" : tree}];

        treeroot
        .jstree({
            'core' : {
                'data' : function (node, cb) {
                    if(node.id === "#") {
                        cb(tree);
                    }
                    else {
                        var path_string = treeroot.jstree("get_path", node,"/",false);
                        get(path_string, "2").then(
                            function(res){
                                var tree = translate(res['data']);
                                cb(tree);
                        });
                    }
                },
                'check_callback' : true
            },
            "plugins" : [
              "unique",
              "contextmenu",
              "dnd"
            ],
            "contextmenu" : {
                "items" : customMenu
            },
            "dnd":{
                "is_draggable": function(node){
                    return true;
                }
            }
        });
    });
}

initialize();

});