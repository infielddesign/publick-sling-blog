/**
 * Angular controller for adding and removing links while editing pages.
 * Works with /libs/publick/components/admin/pageEdit component.
 */
app.controller('LinksController', function($scope){
  $scope.addLink = function(event) {
    event.preventDefault();
    $scope.links.push(null);
  };

  $scope.removeLink = function(event, index) {
    event.preventDefault();
    $scope.links.splice(index,1);
  }
});