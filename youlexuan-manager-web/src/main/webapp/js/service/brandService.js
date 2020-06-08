//商品服务

app.service('brandService',function ($http) {

    //添加数据
    this.add = function (entity) {
        return $http.post("../brand/save.do",entity);
    }

    //修改
    this.update=function(entity){
        return $http.post('../brand/update.do',entity);
    }
    //删除
    this.dele=function(selectids){
        return $http.post('../brand/del.do',selectids);
    }

    //获取指定id品牌信息
    this.findOne=function(entity){
        return $http.post('../brand/findOne.do',entity);
    }

    //分页条件查询
    this.search  = function (pageNumber,pageSize,criteria) {

       return $http.post("../brand/findPageCriteria.do?pageNumber="+pageNumber+"&pageSize="+pageSize,criteria);
    }

    //下拉列表数据
    this.selectOptionList=function(){
        return $http.get('../brand/selectOptionList.do');
    }

})