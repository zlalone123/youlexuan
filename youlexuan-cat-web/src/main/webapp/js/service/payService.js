app.service('payService',function ($http) {
    this.createNative=function(){
        return $http.get('../pay/creatNative.do');
    }

    //查询交易的状态
    this.queryPayStatus = function (out_trade_no,transaction_id) {
        return $http.get('../pay/queryPayStatus.do?out_trade_no='+out_trade_no);
    }
})