app.controller("cartController",function($scope,$http,cartService,addressService){


    //查询购物车列表
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                $scope.totalValue=cartService.sum($scope.cartList);//求合计数
            }
        );
    }

    //添加或移除商品到购物车
    $scope.addGoodsToCartList=function(itemId,num){
        cartService.addGoodsToCartList(itemId,num).success(
            function(response){
                if(response.status){
                    $scope.findCartList();//刷新列表
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //添加商品到购物车
    $scope.addToCart=function(){
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
            + $scope.sku.id +'&num='+$scope.num).success(
            function(response){
                if(response.status){
                    location.href='http://localhost:9107/cart.html';//跳转到购物车页面
                }else{
                    alert(response.message);
                }
            }
        );
    }


    $scope.findLoginName=function () {
        cartService.findLoginName().success(function (response) {
            if (response.status){
                $scope.loginName=response.message;
            }

        })
    }

    //查询用户的地址信息
    $scope.getAddress =function(){
        addressService.findOne().success(function (response) {
            if (response) {
                $scope.addressList = response;
                for (var i = 0;i<$scope.addressList.length;i++){
                    if ($scope.addressList[i].isDefault=='1'){
                        $scope.isaddressSelected=$scope.addressList[i];
                        break;
                    }
                } 
            }

        })
    }

    $scope.addressSelected=function (address) {
      $scope.isaddressSelected = address;
    }

    //判断当前的地址是否被选中
    $scope.isSelectedAddress = function (address) {
        if($scope.isaddressSelected.contact == address.contact){
            return true;
        }
        return false;
    }

    $scope.order={paymentType:'1'};
    //选择支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType= type;
    }

    //保存订单
    $scope.submitOrder=function(){
        if($scope.totalValue.totalMoney<=0){
            alert("请先选择商品")
            return
        }
        $scope.order.receiverAreaName=$scope.isaddressSelected.address;//地址
        $scope.order.receiverMobile=$scope.isaddressSelected.mobile;//手机
        $scope.order.receiver=$scope.isaddressSelected.contact;//联系人
        cartService.submitOrder( $scope.order ).success(
            function(response){
                if(response.status){
                    //页面跳转
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else{//如果货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else{
                    alert(response.message);	//也可以跳转到提示页面
                }
            }
        );
    }

    $scope.showProvince =[];

    $scope.showCity = [];

    $scope.showTown = [];

    //获取地区信息
    $scope.initArea = function () {
       $http.get('../address/initArea.do').success(function (resp) {

           for(var i = 0;i<resp.provinces.length;i++){
               var pro = resp.provinces[i];
              $scope.showProvince[pro.provinceid] = pro.province;

           }

           for(var i = 0;i<resp.cities.length;i++){
               var city = resp.cities[i];
               $scope.showCity[city.cityid] = city.city;
           }

           for (var i= 0;i<resp.areas.length;i++){
               var town = resp.areas[i];
               $scope.showTown[town.areaid] = town.area;
           }
       })
    }

})