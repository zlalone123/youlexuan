app.controller('payController' ,function($scope ,payService) {
    //本地生成二维码
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {

                if(response.code=="10000"){
                    $scope.money = response.total_fee;
                    $scope.out_trade_no = response.out_trade_no;//订单号（支付单号）

                    //二维码
                    var qr = new QRious({
                        element: document.getElementById('qrious'),
                        size: 250,
                        level: 'H',
                        value: response.qrcode
                    });

                    if($scope.out_trade_no!=null) {
                        queryPayStatus($scope.out_trade_no);
                    }
                }else{
                    alert("二维码生成失败")
                }

            }
        );
    }

    queryPayStatus=function(out_trade_no){
        payService.queryPayStatus(out_trade_no).success(
            function(response){
                if(response.status){
                    location.href="paysuccess.html#?money="+$scope.money;
                }else{
                    if(response.message=='二维码超时'){
                        document.getElementById('timeout').innerHTML=
                            '二维码已过期，刷新页面重新获取二维码。';

                    }else{
                        location.href="payfail.html";
                    }
                }
            }
        );
    }
})