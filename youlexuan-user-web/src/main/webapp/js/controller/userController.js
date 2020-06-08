//用户表控制层
app.controller('userController', function ($scope, $location, userService, loginService) {



    //注册
    $scope.reg = function () {
        if ($scope.entity.password != $scope.password) {
            alert("两次输入的密码不一致，请重新输入");
            return;
        }
        userService.add($scope.entity, $scope.smscode).success(
            function (response) {
                alert(response.message)
            }
        );
    }

    //发送验证码
    $scope.sendCode = function () {
        if ($scope.entity.phone == null) {
            alert("请输入手机号！");
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        );
    }

    $scope.getName = function () {
        loginService.getName().success(function (response) {
            $scope.loginName = response.loginName;

            //获取个人信息
            userService.findOne($scope.loginName).success(function (response) {
                $scope.userInfo = response;
                $scope.userInfo.birthday = getTargetDate($scope.userInfo.birthday)

            })
        })

    }

    function getTargetDate(date){
        return date.substring(0,date.indexOf(" "));
    }

   /* //字符串转日期格式，strDate要转为日期格式的字符串
    function getDate(strDate) {
        var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
            function (a) {
                return parseInt(a, 10) - 1;
            }).match(/\d+/g) + ')');

        var targetDate = date.getFullYear()+"-"+date.getMonth()+"-"+date.getDay();
        return targetDate;
    }*/


    //判断当前的性别
    $scope.selectSex = function (sex, num) {

        if (num == sex) {
            return true;
        } else {
            return false;
        }
    }

    $scope.save = function () {
        userService.update($scope.userInfo).success(function (response) {

                alert(response.message)

        })
    }


    $scope.upload = function () {
        userService.upload().success(function (response) {
            if (response) {

                $scope.userInfo.headPic = response.message;
                alert("上传成功")
            }else{
                alert(response.message)
            }
        })
    }

});