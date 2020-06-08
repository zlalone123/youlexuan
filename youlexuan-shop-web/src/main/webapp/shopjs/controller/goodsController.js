//商品控制层
app.controller('goodsController', function ($scope, $controller,$location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //定义页面实体结构
    $scope.entity = {
        goods: {'typeTemplateId': '', 'category2Id': '', 'category3Id': '','isEnableSpec':'1'},
        goodsDesc: {itemImages: [], specificationItems: []}
    };

    //加载商品分类
    $scope.selectItemCat1List = function () {

        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;

            }
        );
    }

    //读取二级分类
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {

        if(oldValue!=null){
            $scope.clear();
        }


        //判断一级分类有选择具体分类值，在去获取二级分类
        if (newValue) {
            //根据选择的分类的id值，查询二级分类
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List = response;

                }
            );
        }
    });

    //读取三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //判断二级分类有选择具体分类值，在去获取三级分类
        if (newValue) {
            //根据选择的值，查询二级分类
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;

                }
            );
        }
    });

    //三级分类选择后  读取模板ID
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //判断三级分类被选中，在去获取更新模板id
        if (newValue) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId = response.typeId; //更新模板ID
                }
            );
        }
    });

    $scope.clear = function () {
        $scope.itemCat2List = [];
        $scope.itemCat3List = [];
        $scope.entity.goods.typeTemplateId = '';
        $scope.entity.goods.category2Id = '';
        $scope.entity.goods.category3Id = '';
    }


    /**
     * 添加与取消规格和规格选项
     * @param $event
     * @param name 规格名称
     * @param value 规格选项
     */
    $scope.updateSpecAttribute = function ($event, name, value) {
        //判断此规格是否已经存在
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);

        if (object != null) {
            //规格已经存在
            //判断复选框的状态
            if ($event.target.checked) {
                //复选框选中，把当前选中的规格选项值加入到当前对应的规格的规格选项数组中
                object.attributeValue.push(value);
            } else {
                //复选框取消
                //移除选项
                object.attributeValue.splice(object.attributeValue.indexOf(value, 1));

                //当所有的规格对应的规格选项都取消了之后，那么这个规格名称也就没有必要保存了
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }

            }
        } else {
            //规格不存在,即首次添加这个规格对应的规格选项
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});

        }
    }

    /**
     * SKU列表的生成
     */
    $scope.createItemList = function () {
        //初始化定义一个SKU记录的模板
        $scope.entity.itemList = [{spec: {}, perice: '0', num: 99, status: '0', isDefault: '0'}];
        var specifications = $scope.entity.goodsDesc.specificationItems;

        //遍历specifications中的值，生成对应的item
        if (specifications.length > 0) {
            //"itemList":[{"spec":{"尺码":"165"},"perice":"0","num":99,"status":"0","isDefault":"0"}]
            for (var i = 0; i < specifications.length; i++) {
                //添加列
                $scope.entity.itemList = addColumn($scope.entity.itemList, specifications[i].attributeName, specifications[i].attributeValue);
            }
        } else {
            $scope.entity.itemList = [];
        }
    }

    /**
     * 当规格选项发生变化时，将itemList回复为默认值,将entity.goodsDesc.specificationItems恢复为默认值
     */
    $scope.$watch('specList', function (newValue,oldValue) {

        if(oldValue!=null && $location.search()['id']==null){
            $scope.entity.itemList = [{spec: {}, perice: '0', num: 99, status: '0', isDefault: '0'}];
            $scope.entity.goodsDesc.specificationItems = [];
        }

    })

    /**
     * 添加列值
     * @param arr 规格和规格选项集合
     * @param attributeName 规格名称
     * @param attributeValue 规格选项值
     */
    addColumn = function (arr, attributeName, attributeValue) {
        //新的列表集合
        var itemlist = [];
        //遍历修改旧的列表
        //"itemList":[{"spec":{"尺码":"165"},"perice":"0","num":99,"status":"0","isDefault":"0"}]
        for (var i = 0; i < arr.length; i++) {
            var oldRow = arr[i];//旧的item
            //添加列
            for (var j = 0; j < attributeValue.length; j++) {
                var item = JSON.parse(JSON.stringify(oldRow));//复制旧的item的数据
                //添加列数据
                item.spec[attributeName] = attributeValue[j];

                //添加生成的行到新的列表中
                itemlist.push(item);
            }
        }
        return itemlist;
    }


    //加载模板对应的品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        if (newValue) {
            typeTemplateService.findOne(newValue).success(function (response) {
                $scope.typeTemplate = response;//返回当前模板id对应的模板
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//将字符串转变为json对象

                //添加的时候在执行
                if($location.search()['id']==null|| $scope.entity.goodsDesc.customAttributeItems.length==0){
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
                }
            })

            //加载规格和规格对应的选项
            typeTemplateService.findSpecList(newValue).success(
                function (response) {
                    $scope.specList = response;
                }
            );
        }
    })


    $scope.save = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        if ($scope.entity.goods.id == null) {

            goodsService.add($scope.entity).success(function (response) {
                if (response.status) {
                    alert(response.message)
                    editor.html('');//清空富文本编辑器
                    window.location.href="../../admin/goods.html";
                } else {
                    alert(response.message)
                }
            })
        }else{
            goodsService.update($scope.entity).success(function (response) {
                if (response.status) {
                   location.href='goods.html';
                }else{
                    alert("修改失败");
                }
            })
        }


    }

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
		//获取上个页面传递过来的参数
        var id  = $location.search()['id'];
        if(id==null){
            return ;
        }
       goodsService.findOne(id).success(function (response) {
           $scope.entity=response;
           editor.html($scope.entity.goodsDesc.introduction);
           if( $scope.entity.goodsDesc.itemImages==null){
               $scope.entity.goodsDesc.itemImages=[];
           }else{
               $scope.entity.goodsDesc.itemImages= JSON.parse($scope.entity.goodsDesc.itemImages);
           }

           if($scope.entity.goodsDesc.customAttributeItems==null){
               $scope.entity.goodsDesc.customAttributeItems = [];
           }else{
               $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems)
           }

           if($scope.entity.goodsDesc.specificationItems==null){
               $scope.entity.goodsDesc.specificationItems=[];
           }
           $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
           for(var i = 0;i<$scope.entity.itemList.length;i++){
               $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
           }
       })

    }

    /**
     * 判断规格和规格选项是否被选中
     * @param specName 规格名称
     * @param optinName 规格选项名称
     */
    $scope.checkAttributeValue =function(specName,optinName){
        var items = $scope.entity.goodsDesc.specificationItems;
        var object =$scope.searchObjectByKey(items,'attributeName',specName);
        if(object==null){
            return false;
        }else{
            if(object.attributeValue.indexOf(optinName)>=0){
                return true;
            }else{
                return false;
            }
        }

    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.status) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    $scope.image_entity = {};
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.status) {//如果上传成功，取出url
                $scope.image_entity.url = response.message;//设置文件地址
            } else {

            }
        }).error(function () {
            alert("上传发生错误");
        });
    };

    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1)

    }

    //审核状态
    $scope.status = ["未审核", "已审核", "未通过", "关闭"];

    //商品的分类级别
    $scope.itemCat1List = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.itemCat1List[response[i].id] = response[i].name;
            }

        })
    }

    $scope.updateIsMarketableStatus = function (status) {
        goodsService.updateIsMarketable($scope.selectIds,status).success(function (response) {
            alert(response.message)

        })
    }

});	