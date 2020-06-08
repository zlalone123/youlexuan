//品牌控制层

app.controller('brandController',function ($scope,$controller,brandService) {

    //继承baseController
    $controller('baseController',{$scope:$scope});
    //条件查询
    $scope.criteria={};
    $scope.search=function(pageNumber,pageSize){
        brandService.search(pageNumber,pageSize,$scope.criteria).success(
            function(response){
                $scope.paginationConf.totalItems=response.total;//总记录数
                $scope.brandList=response.rows;//给列表变量赋值
            }
        );
    }


    //save
    $scope.save=function(){
        if($scope.entity.id!=null){//如果有ID
            brandService.update($scope.entity).success(
                function(response){
                    if(response.status){
                        $scope.reloadList();//重新加载
                    }else{
                        alert(response.message);
                    }
                }
            );
        }else{
            brandService.add($scope.entity).success(
                function(response){
                    if(response.status){
                        $scope.reloadList();//重新加载
                    }else{
                        alert(response.message);
                    }
                }
            );
        }
    }

    //查询实体
    $scope.findOne=function(entity){
        brandService.findOne(entity).success(
            function(response){
                $scope.entity= response;
            }
        );
    }


    //删除
    $scope.delete = function(){

        brandService.dele($scope.selectIds).success(
            function(response){

                if(response.status){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds=[];
                }
            }
        );
    }

})