//用户表服务层
app.service('userService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../user/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../user/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(loginName){
		return $http.get('../user/findOne.do?loginName='+loginName);
	}
	//增加 
	this.add=function(entity,smscode){
		return  $http.post('../user/add.do?smscode='+smscode,entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../user/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../user/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../user/search.do?page='+page+"&rows="+rows, searchEntity);
	}

	//发送验证码
	this.sendCode=function(phone){
		return $http.get("../user/sendCode.do?phone="+phone);
	}

	//文件上传
	this.upload = function () {

		var formData = new FormData();
		formData.append("file",up_img_WU_FILE_0.files[0]);

		return $http({
			url:"user/upload.do",
			method:"post",
			data:formData,
			headers:{'Content-type':undefined},
			transformRequest:angular.identity
		})
	}
});