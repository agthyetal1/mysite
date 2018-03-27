// 公共对象
var comm = {
    DATE_FMT: 'YYYY-MM-DD',
    DATETIME_FMT: 'YYYY-MM-DD HH:mm:ss',
    // 默认分页大小
    FY_SIZE: 10,
    // console.log简写为comm.log
    log: function (msg) {
        console.log(msg);
    },
    // 显示遮罩层
    showLoading: function () {
        // 由于ng-view，导致loading关不掉，所以先把loading效果去掉以解决问题
        /*if($("#global_loadingModal").length==0) {
         var html = '<div class="modal fade" id="global_loadingModal"></div>';
         $("body").append(html);
         }
         $("#global_loadingModal").modal({backdrop: 'static', keyboard: false});*/
    },
    // 隐藏遮罩层
    hideLoading: function () {
        /*$("#global_loadingModal").modal('hide');*/
    },
    alert: function (info) {
        $("#global_alertModal").remove();
        if ($("#global_alertModal").length == 0) {
            var html = '' +
                '<div class="modal fade" id="global_alertModal">' +
                '    <div class="modal-dialog">' +
                '        <div class="modal-content">' +
                '            <div class="modal-header">' +
                '                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>' +
                '                <h4 class="modal-title">提示:</h4>' +
                '            </div>' +
                '            <div class="modal-body">' +
                '                <div class="box-body">' +
                '                	<h4>' + info + '</h4>' +
                '                </div>' +
                '            </div>' +
                '            <div class="modal-footer">' +
                '                <button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>' +
                '            </div>' +
                '        </div>' +
                '    </div>' +
                '</div>';
            $("body").append(html);
        }
        $("#global_alertModal").modal();
    },
    confirm: function (msg, fn, fn_param) {
        var html = "";
        var id = "confirm_div_20160101123456";
        var jsid = "confirm_js_20160101123456";
        html += '<div class="modal fade" id="' + id + '">';
        html += '<div class="modal-dialog modal-sm">';
        html += '<div class="modal-content">';
        html += '<div class="modal-header">';
        html += '<button type="button" class="close" data-target="#' + id + '" data-dismiss="modal" aria-hidden="true">x</button>';
        html += '<h4 class="modal-title">' + msg + '</h4>';
        html += '</div>';
        html += '<div class="modal-footer">';
        html += '<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="' + jsid + '()">确定</button>';
        html += '<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>';
        html += '</div>';
        html += '</div>';
        html += '</div>';
        html += '</div>';
        if($("#" + id).length) {
            $("#" + id).remove()
        }
        $("body").append(html);
        $("#" + id).modal();
        window.confirm_js_20160101123456 = function () {
            fn(fn_param);
        }
    },
    gcName: "/mysite",
    // 包装angularjs的$http方法，使之好统一处理
    http: function (option, $http, $q) {
        var deferred = $q.defer();
        comm.showLoading()
        option = option || {}
        option.headers = option.headers || {}
        var token = sessionStorage.getItem("token")
        if(token) {
            // 所有请求加入header头部token身份认证参数
            option.headers.token = token
        }
        if(!option.url_no_prefix) {
            option.url = comm.gcName + option.url;
        }


        if(option.method && option.method.toLowerCase() == 'post') {
            option.headers['Content-Type'] = 'application/x-www-form-urlencoded'
        }
        // if(option.data) {
        //     option.data = $httpParamSerializerJQLike(option.data)
        // }
        $http(option).success(function (data) {
            comm.hideLoading();
            if (data && (data.status == '0' || data.status == 0)) {
                if (data.data && data.data.page && data.data.page.list) {
                    _.each(data.data.page.list, function (item, index) {
                        _.each(_.keys(item), function (key) {
                            if (key == 'created' || key == 'modified') {
                                if (item[key]) {
                                    item[key] = moment(item[key]).format('YYYY-MM-DD HH:mm:ss');
                                }
                            }
                        });
                    });
                }
                deferred.resolve(data.data);
            } else if(data && (data.status == '-2' || data.status == -2)){
                comm.log('token过期了，自动重定向到登录页')
                location.href = comm.gcName + '/page/login.html'
            } else {
                comm.alert("操作失败：" + data.data);
                deferred.reject(data);
            }
        }).error(function (data) {
            comm.hideLoading();
            comm.alert("操作失败：" + JSON.stringify(data));
            deferred.reject(data);
        });
        return deferred.promise;
    },
    // 选中菜单
    setMenu: function ($rootScope, htmlName) {
        _.each(menus, function (menu) {
            _.each(menu.child, function (item) {
                if (item.html == htmlName) {
                    $rootScope.oneMenuActive = menu.name;
                    $rootScope.twoMenuActive = item.html;
                }
            });
        });
    },
    /*kindeditor_items:[
        'source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'code', 'cut', 'copy', 'paste',
        'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
        'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image',
        'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
        'anchor', 'link', 'unlink', '|', 'about'
    ],*/
    kindeditor_items:['source','bold','italic','underline', 'fontsize', 'forecolor', 'hilitecolor', 'fontname','image','code','justifyleft','justifycenter','justifyright','justifyfull','link','clearhtml','removeformat','table'],
    // 翻页用方法
    fenyeObj: function (pageNum, count) {
        var page = {};
        page.pageNum = pageNum;
        page.count = count;
        page.pageSum = Math.floor((count - 1) / comm.FY_SIZE + 1);
        page.pageSumArray = comm.getFanye(pageNum, page.pageSum);
        return page;
    },
    // fanyeObj子方法
    getFanye: function (pageNum, pageSum) {
        var ls = [];
        if (pageSum <= 11) {
            //全显示
            for (var i = 0; i < pageSum; i++) {
                ls[i] = {key: i + 1, val: i + 1};
            }
        } else {
            if (pageNum <= 6) {
                //左全显示，右跳
                for (var i = 0; i < 8; i++) {
                    ls[i] = {key: i + 1, val: i + 1};
                }
                ls[ls.length] = {key: '', val: '..'};
                ls[ls.length] = {key: pageSum - 1, val: pageSum - 1};
                ls[ls.length] = {key: pageSum, val: pageSum};
            } else {
                if (pageNum + 5 < pageSum) {
                    //左跳右跳
                    ls[0] = {key: 1, val: 1};
                    ls[1] = {key: 2, val: 2};
                    ls[2] = {key: '', val: '..'};
                    ls[3] = {key: pageNum - 2, val: pageNum - 2};
                    ls[4] = {key: pageNum - 1, val: pageNum - 1};
                    ls[5] = {key: pageNum, val: pageNum};
                    ls[6] = {key: pageNum + 1, val: pageNum + 1};
                    ls[7] = {key: pageNum + 2, val: pageNum + 2};
                    ls[8] = {key: '', val: '..'};
                    ls[9] = {key: pageSum - 1, val: pageSum - 1};
                    ls[10] = {key: pageSum, val: pageSum};
                } else {
                    //左跳，右全
                    ls[0] = {key: 1, val: 1};
                    ls[1] = {key: 2, val: 2};
                    ls[2] = {key: '', val: '..'};
                    for (var i = pageSum - 7; i <= pageSum; i++) {
                        ls[ls.length] = {key: i, val: i};
                    }
                }
            }
        }
        return ls;
    }
};

