app.controller("paySuccess",function ($scope,$locale) {
    //获取金额
    $scope.getMoney=function(){
        return  $location.search()['money'];
    }
})