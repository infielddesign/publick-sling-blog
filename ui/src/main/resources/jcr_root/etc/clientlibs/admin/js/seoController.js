/**
 * Angular controller for checking SEO stuff and displaying hints.
 * Works with /libs/publick/components/admin/blogEdit component.
 */
 app.controller('SEOController', function($scope){
  $scope.SEOHintMaxLength = function(maxLength, currentLength) {
    var message,
        cssClass,
        charsRemaining = maxLength - currentLength;
    
    if (charsRemaining < 0) {
      message = "+" + Math.abs(charsRemaining) + " above recommended limit.";
      cssClass = "has-warning";
      showWarning = true;
    } else {
      message = charsRemaining + " characters available.";
    }
    
    $scope.SEOHintMaxLengthMessage = message;
    $scope.SEOHintClass = cssClass;
  }
});