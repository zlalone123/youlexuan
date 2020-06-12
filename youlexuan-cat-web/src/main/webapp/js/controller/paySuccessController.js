app.controller("paySuccessController",function ($scope,$location) {
    //获取金额
    $scope.getMoney=function(){
        return  $location.search()['money'];
    }
})