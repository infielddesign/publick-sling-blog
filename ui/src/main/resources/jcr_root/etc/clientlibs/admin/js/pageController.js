/**
 * Angular controller to save settings through AJAX posts and display the proper
 * success or failure message. Works with all settings components including
 * system settings, email config, and reCAPTcha config.
 */
app.controller('PageListController', function($scope, $http, formDataObject, ngDialog, $window) {

    $scope.pageList;
    
var treeroot = $("#clbk");
var CONTENT_PATH = "/content";
var ROOT_PATH = "/page";

$scope.textcontent = "nicola";

/**
 *  This code takes a JSON object, an id and inserts a pretty printed
 *  and syntax highlighted JSON in your DOM.
**/
function jsonPrettyHighlightToId(jsonobj, id_to_send_to) {

  var json = JSON.stringify(jsonobj, undefined, 2);

  json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  json = json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
      var cls = 'color: darkorange;';
      if (/^"/.test(match)) {
          if (/:$/.test(match)) {
              cls = 'color: red;';
          } else {
              cls = 'color: green;';
          }
      } else if (/true|false/.test(match)) {
          cls = 'color: blue;';
      } else if (/null/.test(match)) {
          cls = 'color: magenta;';
      }
      return '<span style="' + cls + '">' + match + '</span>';
  });

  document.getElementById(id_to_send_to).innerHTML = json;

}



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
function slingPostServlet(url, params) {
  return $http({
    method: 'POST',
    url: url,
    data: params,
    transformRequest: formDataObject
  });
}



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
        slingPostServlet(url, params);
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
      slingPostServlet(url, params);
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
    console.log(path);

    slingPostServlet(url, params).then(
         function(res1){
                console.log(res1);

             get(prefix_path, "2").then(
             function(res2){
                 var tree = translate(res2['data']);
                 treeroot.jstree(true).refresh_node(fetched_node);
             });
         }
    );
}



/**
 *  Context Functions
**/
function openNodeContext(obj, prefix_path, parent) {
    console.log("AEM changed");
    $window.open('/' + prefix_path + '/' + parent + '.html', '_blank');
}

function newNodeContext(obj, prefix_path, parent) {
console.log(prefix_path);
console.log(parent);
console.log("parent");
    ngDialog.open({
        template : "/admin/page/edit.html?post=" + CONTENT_PATH + "/" + prefix_path + "&post2=" + parent + "&post3=new",
        className : 'ngdialog-theme-default custom-width',
        controller : 'newPageController',
        closeByEscape : true,
        scope : $scope,
        preCloseCallback: function(value) { return preCloseCallback() }
    });
}

function editNodeContext(obj, prefix_path, parent) {
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
    
function renameNodeContext(obj, prefix_path, path, parent, node) {
    renameNode(prefix_path, path, parent, node);
}

function deleteNodeContext(obj, prefix_path, path, node) {
    deleteNode(path, prefix_path, node);
}



/**
 *  Context Functions
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
          "label" : "Open",
          "action" : function (obj) {
            openNodeContext(obj, prefix_path, parent);
          }
      },
      "New" : {
          "label" : "New",
          "action" : function (obj) {
            newNodeContext(obj, prefix_path, parent);
          }
      },
      "Edit" : {
        "label" : "Edit",
        "action" : function (obj) {
            editNodeContext(obj, prefix_path, parent);
        }
      },
      "Rename" : {
          "label" : "Rename",
          "action" : function (obj) {
            renameNodeContext(obj, prefix_path, path_string, parent, node);
          }
      },
      "Delete" : {
         "label" : "Delete",
         "action" : function (obj) {
            deleteNodeContext(obj, prefix_path, path_string, node);
          }
      }
   };

   //If node is a folder do not show the "delete" menu item
   if ($(node).hasClass("jstree-closed") || $(node).hasClass("jstree-open")) {
      delete items.remove;
   }

   return items;
}



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
tree = [{"text" : "page", "properties" : "jcr:primaryType : sling:Folder", "icon" : "glyphicon glyphicon-folder-open",  "children" : tree}];

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



/**
 *  The following code listens for the select_node event.
 *  If the event is triggered then it will pretty highlight
 *  the JSON by providing it an ID.
**/
treeroot
.on('select_node.jstree', function (e, data) {
    var properties = data["node"]["original"]["properties"];
    
    jsonPrettyHighlightToId(properties, 'pretty_json');
    
    var filteredProperties = filterLevelTwoByPrimaryType(properties, "publick:page");    
    updatePageList(filteredProperties);
    
//    jsonPrettyHighlightToId(filteredProperties, 'pretty_json');
});


/**
 *  Return an object that only contains nodes of a specific type (e.g. "publick:page" or "sling:Folder")
**/
function filterLevelTwoByPrimaryType(object, primaryType) {
    var filteredProperties = {};
  
    for (var key in object) {
        if (object.hasOwnProperty(key)) {
            var levelTwo = object[key];
            
            for (var keysTwo in levelTwo) {
                if (levelTwo.hasOwnProperty(keysTwo) && keysTwo === "jcr:primaryType" && levelTwo["jcr:primaryType"] === primaryType) {
                    filteredProperties[key] = object[key];
                }
            }
        }
    }
    
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
    $scope.$apply();
}

    
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

    renameNode(node, prefix_path, path_string, parent);

});



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

    console.log(prefix_path);

    moveNode(old_path, new_path + "/");
});


  $("select[name='primarytype']").on("change", function(){
      var typeValue = $(this).find(":selected").val();
      console.log(typeValue);

      if(typeValue==="sling:Folder"){
          $("#configurationName").addClass("hide");
          $("#visible").addClass("hide");
          $("#keywords").addClass("hide");
          $("#links").addClass("hide");
          $("#scripts").addClass("hide");
          $("#description").addClass("hide");
          $("#contentfield").addClass("hide");
      }
      if(typeValue==="publick:page"){
          $("#configurationName").removeClass("hide");
          $("#visible").removeClass("hide");
          $("#keywords").removeClass("hide");
          $("#links").removeClass("hide");
          $("#scripts").removeClass("hide");
          $("#description").removeClass("hide");
          $("#contentfield").removeClass("hide");
      }
  });

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
});