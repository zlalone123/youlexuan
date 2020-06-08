app.service("uploadService",function ($http) {
    this.uploadFile = function () {

        //获取页面表单中的文件
        var forData = new FormData();
        forData.append("file",file.files[0]);

        return $http({
            url:"../upload.do",
            method:"post",
            data:forData,
            headers:{'Content-type':undefined},
            transformRequest:angular.identity
        })
    }
    
})