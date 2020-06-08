app.controller('baseController',function ($scope) {

    //分页组件
    $scope.paginationConf = {
        currentPage:1,//当前页
        totalItems:10,//总记录数
        itemsPerPage:10,//每页显示的记录数
        perPageOptions:[10,20,30,40,50],//每页显示多少记录的下拉列表
        //页面加载时会默认调用
        onChange:function () {
            //分页组件的参数发生变化时，调用其的方法
            $scope.reloadList();

        }
    };

    //重新加载页面数据
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    //选中的id
    $scope.selectIds = [];
    /**
     * 获取复选框中的id到数组中
     * $event 表示页面的事件对象
     */
    $scope.updateSelection = function($event,id){
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else {
            //获取当前复选框对应的id在数组中的位置
            var i = $scope.selectIds.indexOf(id);
            //从数组中移除元素
            $scope.selectIds.splice(i,1);
        }
    }


    //json对象转为想要的字符串
    $scope.jsonString = function (str,key) {
       var strArr =  JSON.parse(str);
       var value = [];
       for(var i = 0;i<strArr.length;i++){
           value.push(strArr[i][key]);
        }

       return value.toString();
    }
})