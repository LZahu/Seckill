// 存放主要交互逻辑的js代码，模块化
var seckill = {
    // 封装秒杀相关ajax的url
    URL: {
        // 项目绝对路径
        basePath: function () {
            //获取当前网址，如： http://localhost:8080/ems/Pages/Basic/Person.jsp
            var curWwwPath = window.document.location.href;
            //获取主机地址之后的目录，如： /ems/Pages/Basic/Person.jsp
            var pathName = window.document.location.pathname;
            var pos = curWwwPath.indexOf(pathName);
            //获取主机地址，如： http://localhost:8080
            var localhostPath = curWwwPath.substring(0, pos);
            //获取带"/"的项目名，如：/ems
            var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
            //获取项目的basePath   http://localhost:8080/ems/
            var basePath = localhostPath + projectName + "/";
            return basePath;
        },
        now: function () {
            return seckill.URL.basePath() + 'seckill/time/now';
        },
        exposer: function (seckillId) {
            return seckill.URL.basePath() + 'seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return seckill.URL.basePath() + 'seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },

    // 获取秒杀地址，控制实现逻辑，执行秒杀
    handleSeckill: function (seckillId, node) {
        node.hide().html('<butten class="btn btn-primary btn-lg" id="killBtn">开始秒杀</butten>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            // 在回调函数中，执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    // 开启秒杀
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log('killUrl : ' + killUrl);
                    // 绑定一次点击事件，防止多次点击秒杀
                    $('#killBtn').one('click', function () {
                        // 执行秒杀请求的操作
                        // 1.禁用按钮
                        $(this).addClass('disabled');
                        // 2.发送请求，执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var stateInfo = killResult['stateInfo'];
                                // 3.显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                } else {
                    // 未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log('result : ' + result);
            }
        });
    },

    // 验证手机号
    validatePhone: function (phone) {
        return phone && phone.length == 11 && !isNaN(phone);
    },

    // 时间判断
    countdown: function (seckillId, nowTime, startTime, endTime) {
        // 拿到前端展示倒计时信息的节点
        var seckillBox = $('#seckill-box');

        if (nowTime > endTime) {
            // 秒杀结束
            seckillBox.html("秒杀已结束!");
        } else if (nowTime < startTime) {
            var killTime = new Date(startTime + 1000);
            console.log('killTime : ' + killTime);
            // 秒杀未开始，开始计时
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                // 倒计时结束的操作（回调事件）
                // 获取秒杀地址，控制实现逻辑，执行秒杀
                seckill.handleSeckill(seckillId, seckillBox);
            });
        } else {
            // 秒杀开始
            seckill.handleSeckill(seckillId, seckillBox);
        }
    },

    // 详情页秒杀逻辑
    detail: {
        // 详情页初始化
        init: function (params) {
            // 手机验证、登录、计时的交互
            // 在cookie中查找手机号
            var killPhone = $.cookie('killPhone');

            // 验证手机号
            if (!seckill.validatePhone(killPhone)) {
                // 绑定phone，控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true, // 显示弹出层
                    backdrop: 'static', // 禁止位置关闭
                    keyboard: false // 关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        // 电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        // 刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger"> 手机号错误</label>').show(300);
                    }
                });
            }

            // 已经登录
            // 计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    console.log('nowTime : ' + nowTime);
                    // 时间判断，计时交互
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result : ' + result);
                }
            });

        }
    }
}