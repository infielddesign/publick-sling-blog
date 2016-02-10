/**
 * Angular controller for adding and removing scripts while editing pages.
 * Works with /libs/publick/components/admin/pageEdit component.
 */
app.controller('ScriptsController', function($scope){
  $scope.addScript = function(event) {
    event.preventDefault();
    $scope.scripts.push(null);
  };

  $scope.removeScript = function(event, index) {
    event.preventDefault();
    $scope.scripts.splice(index,1);
  }
});