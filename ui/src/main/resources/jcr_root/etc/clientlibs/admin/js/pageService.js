/**
 * Angular service to communicate with the server-side for posting config
 * settings and returning success or failure messages.
 */
app.factory('PageService', function($http, formDataObject) {
  var pageFactory = {},
      PATH_BASE = '/bin/admin',
      PATHS = {
        editPage    : PATH_BASE + '/editpage',
        deletePage : PATH_BASE + '/deletepage',
      };

  /**
   * @private
   */
  function post(path, data) {
    return $http({
      method: 'POST',
      url: path,
      data: data,
      transformRequest: formDataObject
    });
  }

  pageFactory.deletePage = function(type, model) {
    return post(PATHS[type], model);
  };

  return pageFactory;
});