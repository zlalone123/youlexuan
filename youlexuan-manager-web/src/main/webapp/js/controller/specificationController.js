 //规格控制层 
app.controller('specificationController' ,function($scope,$controller,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承

	//规格选项行删除
	$scope.deleTableRow=function(index){
		$scope.entity.specificationOptionList.splice(index,1);//删除
	}

	//新增规格选项行，一个规格多个规格选项
	$scope.addTableRow=function(){
		$scope.entity.specificationOptionList.push({});
	}

	//读取列表数据绑定到表单中
	$scope.findAll=function(){
		specificationService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(pageNumber,pageSize){
		specificationService.findPage(pageNumber,pageSize).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		specificationService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.specification.id!=null){//如果有ID
			serviceObject=specificationService.update( $scope.entity ); //修改  
		}else{
			serviceObject=specificationService.add( $scope.entity );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.status){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){

		alert($scope.selectIds )
		//获取选中的复选框			
		specificationService.dele($scope.selectIds ).success(
			function(response){
				if(response.status){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(pageNumber,pageSize){
		specificationService.search(pageNumber,pageSize,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	