 //商品类目控制层 
app.controller('itemCatController' ,function($scope,$controller ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.grade = 1;//当前列表的级别，默认1

	//当点击查询下级按钮或者点击面包屑时更新下当前的级别
	$scope.setGrade = function(value){
		$scope.grade = value
	}

	//当查询下一级的时候，就把当前显示行的entity放到面包屑的位置
	$scope.selectList  = function(entity){
		if ($scope.grade == 1){
			$scope.entity1 = null;
			$scope.entity2 = null;
		}

		if($scope.grade == 2){
			$scope.entity1 = entity;
			$scope.entity2 = null;
		}

		if ($scope.grade == 3){
			$scope.entity2 = entity;
		}

		$scope.findByParentId(entity.id);
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{

			//获取当前分类的上级
			$scope.entity.parentId = $scope.parentId
			serviceObject=itemCatService.add( $scope.entity );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.status){
					//重新查询 
		        	$scope.findByParentId($scope.entity.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){
		//获取选中的复选框			
		itemCatService.dele($scope.selectIds ).success(
			function(response){
				if(response.status){
					$scope.findByParentId($scope.parentId);//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//获取顶级类别的数据
	$scope.findByParentId = function (parentId) {

		$scope.parentId = parentId;//记录上级
		itemCatService.findByParentId(parentId).success(function (response) {
			if (response) {
				$scope.list = response;
			}
		})
	}


	$scope.typeTemplateList={data:[]};//模板列表
	//读取模板列表
	$scope.findtypeTemplateList=function(){
		typeTemplateService.selectOptionList().success(
			function(response){
				$scope.typeTemplateList={data:response};
			}
		);
	}
    
});	