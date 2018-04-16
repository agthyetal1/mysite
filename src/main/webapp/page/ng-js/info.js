main_ng_app.controller('infoCtrl', function ($scope, $http, $q, $rootScope) {
    comm.setMenu($rootScope, 'info.html');

    $scope.convert = function () {
        comm.http({
            url:'/tool/ip.do',
            params:{},
            method:'POST'
        },$http, $q).then(function(data){
            $scope.ip = data.ip
            $scope.ipName = data.ipName
        })
    }
    $scope.convert()
});
