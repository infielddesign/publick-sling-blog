/**
 * Angular controller to save settings through AJAX posts and display the proper
 * success or failure message. Works with all settings components including
 * system settings, email config, and reCAPTcha config.
 */
app.controller('PageListController', function($scope, $http) {

  function get(page, depth) {
      return $http({
        method: 'GET',
        url: "/page/" + page + "." + depth + ".json"
      });
  }

  function translate(objects){
    var tree = [];

    for(var obj in objects){

        var childnodes = [];

        var isObj = false;

        if((typeof(objects[obj]) == "object"))
        {
            for(obj1 in objects[obj])
            {
                if((typeof(objects[obj][obj1]) == "object")){
                    isArr = (objects[obj][obj1].constructor === Array)
                    if(!isArr)
                    {
                        isObj = true;
                        childnodes[obj1] = objects[obj][obj1];
                    }
                }
            }
            tree.push({"text" : obj, "properties" : objects[obj], "children" : isObj});
        }
    }

    return tree;
  }

  // Takes a JSON object, returns a pretty printed and syntax highlighted
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


    function customMenu(node) {
       //Show a different label for renaming files and folders
       if ($(node).hasClass("jstree-closed") || $(node).hasClass("jstree-open")) { //If node is a folder
          var renameLabel = "Rename Folder";
       }
       else {
          var renameLabel = "Rename File";
       }
       var items = {
          "New" : {
              "label" : "New",
              "action" : function (obj) {
                var path_string = $("#clbk").jstree("get_path", node,"/",false);

                window.location.href = "edit.html?path=/" + path_string;
              }
          },
          "rename" : {
             "label" : renameLabel,   //Different label (defined above) will be shown depending on node type
             "action" : function () { console.log(renameLabel); }
          },
          "delete" : {
             "label" : "Delete File",
             "action" : function () { console.log("Delete File"); }
          }
       };

       //If node is a folder do not show the "delete" menu item
       if ($(node).hasClass("jstree-closed") || $(node).hasClass("jstree-open")) {
          delete items.remove;
       }

       return items;
    }

  get("", "2").then(function(res){
    var objects = res['data'];
    var tree = [];
    tree = translate(objects);

    $('#clbk')
    .on('select_node.jstree', function (e, data) {

        jsonPrettyHighlightToId(data["node"]["original"]["properties"], 'pretty_json');
    }).jstree({
        'core' : {
            'data' : function (node, cb) {
                if(node.id === "#") {
                    cb(tree);
                }
                else {
                    var path_string = $("#clbk").jstree("get_path", node,"/",false);
                    get(path_string, "2").then(function(res){
                        var tree = translate(res['data']);
                        cb(tree);
                    });
                }
            }
        },
        "plugins" : [
          "contextmenu"
        ],
        "contextmenu" : {
            "items" : customMenu
        }
    });
  });
});