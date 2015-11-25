/**
 * Angular controller for checking SEO stuff and displaying hints.
 * Works with /libs/publick/components/admin/blogEdit component.
 */
 app.controller('SEOController', function($scope){
  $scope.SEOHintMaxLength = function(maxLength, currentLength) {
    var showSEOHintMessage,
        cssClass,
        charsRemaining = maxLength - currentLength;
    
    if (charsRemaining < 0) {
      charsRemaining = "+" + Math.abs(charsRemaining);
      showSEOHintMessage = true;
      cssClass = "has-error";
    } else if (charsRemaining === 0) {
      charsRemaining = "0";
    }
    
    $scope.charsRemaining = charsRemaining;
    $scope.showSEOHintMessage = showSEOHintMessage;
    $scope.SEOHintClass = cssClass;
  }
});