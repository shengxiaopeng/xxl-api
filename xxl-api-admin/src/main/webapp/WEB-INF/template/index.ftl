<!DOCTYPE html>
<html>
<head>
    <title>接口汇总信息</title>
<#import "/common/common.macro.ftl" as netCommon>
<@netCommon.commonStyle />
    <!-- daterangepicker -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.css">
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
    <!-- header -->
<@netCommon.commonHeader />
    <!-- left -->
<@netCommon.commonLeft "index" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>Ocean-API<small>接口概览</small></h1>
        </section>

        <!-- Main content -->
        <section class="content">

            <!-- 接口信息 -->
            <div class="row">

            <#-- 任务信息 -->
                <div class="col-md-4 col-sm-6 col-xs-12">
                    <div class="info-box bg-aqua">
                        <span class="info-box-icon"><i class="fa fa-flag-o"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">项目数量</span>
                            <span class="info-box-number">${projectCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">本系统接入的项目数量</span>
                        </div>
                    </div>
                </div>

            <#-- 接口数量 -->
                <div class="col-md-4 col-sm-6 col-xs-12" >
                    <div class="info-box bg-yellow">
                        <span class="info-box-icon"><i class="fa fa-calendar"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">接口数量</span>
                            <span class="info-box-number">${interfaceCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%" ></div>
                            </div>
                            <span class="progress-description">
                                系统接入的接口数量

                            </span>
                        </div>
                    </div>
                </div>

            <#-- 接入人数 -->
                <div class="col-md-4 col-sm-6 col-xs-12">
                    <div class="info-box bg-green">
                        <span class="info-box-icon"><i class="fa ion-ios-settings-strong"></i></span>

                        <div class="info-box-content">
                            <span class="info-box-text">接入人数</span>
                            <span class="info-box-number">${operatorCount}</span>

                            <div class="progress">
                                <div class="progress-bar" style="width: 100%"></div>
                            </div>
                            <span class="progress-description">接口的直接负责人人数</span>
                        </div>
                    </div>
                </div>

            </div>

        <#-- 调度报表：时间区间筛选，左侧折线图 + 右侧饼图 -->
            <div class="row">
                <div class="col-md-12">
                    <div class="box">
                        <div class="box-header with-border">
                            <h3 class="box-title">接口报表</h3>
                     <!-- tools box -->
                           <#-- <div class="pull-right box-tools">

                            </div>-->
                            <!-- /. tools -->
                        </div>
                        <div class="box-body">
                            <div class="row">
                                <div class="col-md-1">
                                    <span class="control-label">用户:</span>
                                </div>
                                <div class="col-md-3">

                                    <select class="form-control" id="operator">
                                        <#--<option value="">请选择用户</option>-->
                                        <#--<option value="盛小朋">盛小朋</option>-->
                                     <#if operatorList?exists && operatorList?size gt 0>
                                        <#list operatorList as operator>
                                            <option value="${operator}"  >${operator}</option>
                                        </#list>
                                    </#if>
                                    </select>
                                </div>
                                <div class="col-md-4"></div>
                                <div class="col-md-1">
                                    <span class="control-label">项目:</span>
                                </div>
                                <div class="col-md-3">
                                    <select class="form-control" id="project">
                                    <#--<option value="">请选择项目</option>-->
                                    <#--<option value="1">test-app</option>-->
                                    <#if projectList?exists && projectList?size gt 0>
                                        <#list projectList as project>
                                            <option value="${project.id}"  >${project.name}</option>
                                        </#list>
                                    </#if>
                                    </select>
                                </div>
                            </div>
                            <div class="row">
                            <#-- 左侧柱状图 -->
                                <div class="col-md-8">
                                    <div id="barChart" style="height: 350px;"></div>
                                </div>
                            <#-- 右侧饼图 -->
                                <div class="col-md-4">
                                    <div id="pieChart" style="height: 350px;"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

    <!-- footer -->
<@netCommon.commonFooter />
</div>
<@netCommon.commonScript />
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<#-- echarts -->
<script src="${request.contextPath}/static/plugins/echarts/echarts.common.min.js"></script>

<script src="${request.contextPath}/static/js/index.js"></script>
</body>
</html>
