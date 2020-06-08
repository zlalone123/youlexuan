//��Ʒ��ϸҳ�����Ʋ㣩
app.controller('itemController',function($scope){
	//�����������������������߸���
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}

	//记录用户选择的规格，被选中的选项就会在该对象中
	$scope.specificationItems={};
	//点击某个规格的时候，添加至specificationItems
	$scope.selectSpecification=function(name,value){
		$scope.specificationItems[name]=value;
		searchSku();//读取sku
	}
	//判断某规格选项是否被用户选中，如果该规格在specificationItems，说明被选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}
	}

	//加载默认SKU，页面上要显示sku的title和price
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}

	//匹配两个对象
	matchObject=function(map1,map2){
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		return true;
	}

	//查询SKU
	searchSku=function(){
		for(var i=0;i<skuList.length;i++ ){
			if( matchObject(skuList[i].spec ,$scope.specificationItems ) ){
				$scope.sku=skuList[i];
				return ;
			}
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
	}

	//添加商品到购物车
	$scope.addToCart=function(){
		alert('skuid:'+$scope.sku.id);
	}
});