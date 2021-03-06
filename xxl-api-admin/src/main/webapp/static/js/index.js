/**
 * Created by xuxueli on 17/4/24.
 */


$(function () {

    // 过滤时间
    var _startDate = moment().subtract(1, 'months');    // 默认，最近一月
    var _endDate = moment();
    $('#filterTime').daterangepicker({
        autoApply:false,
        singleDatePicker:false,
        showDropdowns:false,        // 是否显示年月选择条件
        timePicker: true, 			// 是否显示小时和分钟选择条件
        timePickerIncrement: 10, 	// 时间的增量，单位为分钟
        timePicker24Hour : true,
        opens : 'left', //日期选择框的弹出位置
        ranges: {
            //'最近1小时': [moment().subtract(1, 'hours'), moment()],
            '今日': [moment().startOf('day'), moment().endOf('day')],
            '昨日': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
            '本月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'months').startOf('month'), moment().subtract(1, 'months').endOf('month')],
            '最近1周': [moment().subtract(1, 'weeks'), moment()],
            '最近1月': [_startDate, _endDate]
        },
        locale : {
            format: 'YYYY-MM-DD HH:mm:ss',
            separator : ' - ',
            customRangeLabel : '自定义',
            applyLabel : '确定',
            cancelLabel : '取消',
            fromLabel : '起始时间',
            toLabel : '结束时间',
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
            firstDay : 1
        },
        startDate:_startDate,
        endDate: _endDate
    }, function (start, end, label) {
        freshChartDate(start, end);
    });

    $('#project').change(function () {
        freshUserStats($('#project').val());
    });

    $('#operator').change(function () {
        freshProjectStats($('#operator').val());
    });

    freshUserStats($('#project').val());
    freshProjectStats($('#operator').val());

    /**
     * 刷新报表
     *
     * @param startDate
     * @param endDate
     */
    function freshUserStats(projectId) {
        $.ajax({
            type : 'POST',
            url : base_url + '/document/userStats',
            data : {
                'projectId':projectId
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                   // lineChartInit(data)



                    pieChartInit(data);
                } else {
                    layer.open({
                        title: '系统提示',
                        content: (data.msg || '报表数据加载异常'),
                        icon: '2'
                    });
                }
            }
        });
    }

    function freshProjectStats(operator) {
        $.ajax({
            type : 'POST',
            url : base_url + '/document/projectStats',
            data : {
                'operator':operator
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    // lineChartInit(data)
                    var xdata=[];
                    var ydata=[];
                    for(var i=0;i<data.content.length;i++){
                        xdata.push(data.content[i].name);
                        ydata.push(data.content[i].value||0);
                    }

                    barChartInit(xdata,ydata);
                } else {
                    layer.open({
                        title: '系统提示',
                        content: (data.msg || '报表数据加载异常'),
                        icon: '2'
                    });
                }
            }
        });
    }


    /**
     * 折线图
     */
    function lineChartInit(data) {
        var option = {
            title: {
                text: '日期分布图'
            },
            tooltip : {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross',
                    label: {
                        backgroundColor: '#6a7985'
                    }
                }
            },
            legend: {
                data:['成功调度次数','失败调度次数']
            },
            toolbox: {
                feature: {
                    /*saveAsImage: {}*/
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    boundaryGap : false,
                    data : data.content.triggerDayList
                }
            ],
            yAxis : [
                {
                    type : 'value'
                }
            ],
            series : [
                {
                    name:'成功调度次数',
                    type:'line',
                    stack: '总量',
                    areaStyle: {normal: {}},
                    data: data.content.triggerDayCountSucList
                },
                {
                    name:'失败调度次数',
                    type:'line',
                    stack: '总量',
                    label: {
                        normal: {
                            show: true,
                            position: 'top'
                        }
                    },
                    areaStyle: {normal: {}},
                    data: data.content.triggerDayCountFailList
                }
            ],
            color:['#00A65A', '#F39C12']
        };

        var lineChart = echarts.init(document.getElementById('lineChart'));
        lineChart.setOption(option);
    }

    /**
     * 饼图
     */
    function barChartInit(xdata,ydata) {
       var option = {
            color: ['#3398DB'],
           title : {
               text: '该用户负责各项目接口概况',
               x:'center'
           },
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    data : xdata,
                    axisTick: {
                        alignWithLabel: true
                    }
                }
            ],
            yAxis : [
                {
                    type : 'value'
                }
            ],
            series : [
                {
                    name:'接口数量',
                    type:'bar',
                    barWidth: '60%',
                    data:ydata
                }
            ]
        };
        var barChart = echarts.init(document.getElementById('barChart'));
        barChart.setOption(option);
    }

    /**
     * 饼图
     */
    function pieChartInit(data) {
        var option = {
            title : {
                text: '该项目各负责人接口概况',
                /*subtext: 'subtext',*/
                x:'center'
            },
            tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: ['成功调度次数','失败调度次数']
            },
            series : [
                {
                    name: '分布比例',
                    type: 'pie',
                    radius : '55%',
                    center: ['50%', '60%'],
                    data:data.content,
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ],
            color:['#00A65A', '#F39C12']
        };
        var pieChart = echarts.init(document.getElementById('pieChart'));
        pieChart.setOption(option);
    }

});