main_ng_app.controller('toolCtrl', function ($scope, $http, $q, $rootScope) {
    comm.setMenu($rootScope, 'tool.html');

    $scope.convert = function () {
        comm.http({
            url:'/tool/ddl2bean.do',
            params:{ddl:$scope.ddl},
            method:'POST'
        },$http, $q).then(function(data){
            $scope.bean = data.bean
            $scope.mapper = data.mapper
        })
    }
});
