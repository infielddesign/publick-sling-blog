/**
 * Angular controller for adding and removing keywords/tags while editing blog
 * posts. Works with /libs/publick/components/admin/blogEdit component.
 */
app.controller('KeywordsController', function ($scope, $timeout, $window) {
  var changeElementFocus = function (id) {
    $timeout(function () {
      var element = $window.document.getElementById(id);
      if (element) {
        element.focus();
      }
    });
  };

  var switchToLastKeywordInput = function () {
    var elementId = "keyword-" + ($scope.keywords.length - 1);
    changeElementFocus(elementId);
  };
  
  var isLastKeywordEmpty = function () {
    if ($scope.keywords[$scope.keywords.length - 1] === null ||
        $scope.keywords[$scope.keywords.length - 1] === "") {
      return true;
    }
    
    return false;
  };

  $scope.addKeyword = function (event) {
    event.preventDefault();
    
    if (!isLastKeywordEmpty()) {
      $scope.keywords.push(null);
    } 
    switchToLastKeywordInput();
  };

  $scope.removeKeyword = function (event, index) {
    event.preventDefault();
    $scope.keywords.splice(index, 1);
  };
  
  $scope.onKeywordKeypress = function (event, index) {
    console.log($scope.keywords);
    if (event.keyCode === 13 && !isLastKeywordEmpty()) {
      $scope.addKeyword(event);
    }

    if (event.keyCode === 13) {
      switchToLastKeywordInput();
    }
  };
});