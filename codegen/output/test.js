function alarmService(){
    var service=new Object();
    service.url=host+"/alarmService";
        /**
        *根据工单编号，查询对应告警信息（同一异常源的告警只保留一条） 
         *@param no comment
         */
        service.getAlarmListByWdId=function(cWdId,onSuccess)
        {
            var temp=this.url+"/getAlarmListByWdId";
	        var cWdIdStr=cWdId;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cWdId:cWdIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询当前用户订阅的告警列表 
         *@param  分页查询对象 
         */
        service.getSubscribedAlarmList=function(page,onSuccess)
        {
            var temp=this.url+"/getSubscribedAlarmList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据告警源获取其告警信息 
         *@param  告警源设备编码 
         *@param no comment
         */
        service.getAlarmByResCode=function(resCode,page,onSuccess)
        {
            var temp=this.url+"/getAlarmByResCode";
	        var resCodeStr=resCode;
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            resCode:resCodeStr,
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取告警处理统计结果 
         */
        service.getAlarmProcessComposition=function(onSuccess)
        {
            var temp=this.url+"/getAlarmProcessComposition";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按告警处理状态，分页查询告警信息 
         *@param  分页查询对象 
         *@param  1，查询未处理，2查询超时未处理，3 按时已处理，4 超时已处理 
         */
        service.getAlarmListByStatus=function(page,alarmProcessStatus,onSuccess)
        {
            var temp=this.url+"/getAlarmListByStatus";
	        var pageStr=JSON.stringify(page);
	        var alarmProcessStatusStr=alarmProcessStatus;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            alarmProcessStatus:alarmProcessStatusStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *导出设备最新告警信息 
         *@param  查询条件 
         */
        service.exportAlarmList=function(page,onSuccess)
        {
            var temp=this.url+"/exportAlarmList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件分页查询设备最新告警信息 
         *@param  查询对象 
         */
        service.getAlarmList=function(page,onSuccess)
        {
            var temp=this.url+"/getAlarmList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *处理告警信息 
         *@param  告警Id 
         *@param  处理方式 
         *@param  处理描述 
         */
        service.processAlarm=function(cAlarmId,processWay,processComment,onSuccess)
        {
            var temp=this.url+"/processAlarm";
	        var cAlarmIdStr=cAlarmId;
	        var processWayStr=processWay;
	        var processCommentStr=processComment;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cAlarmId:cAlarmIdStr,
                            processWay:processWayStr,
                            processComment:processCommentStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取所有告警类型 
         */
        service.getAlarmTypeList=function(onSuccess)
        {
            var temp=this.url+"/type/list";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取day日内告警数量统计,默认7天内的数据 
         *@param  天之内的，包括今天 
         */
        service.getAlarmStatistic=function(day,onSuccess)
        {
            var temp=this.url+"/getAlarmStatistic";
	        var dayStr=day;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            day:dayStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function alarmRuleService(){
    var service=new Object();
    service.url=host+"/alarmRuleService";
        /**
        *获取告警等级列表 
         */
        service.getAlarmLevelList=function(onSuccess)
        {
            var temp=this.url+"/getAlarmLevelList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加告警规则，如果是为指定类型资源配置告警规则 请给 cResType赋值，为指定资源配置告警规则 请给cResCode赋值 
         *@param  告警规则对象 
         */
        service.addAlarmRule=function(alarmRule,onSuccess)
        {
            var temp=this.url+"/addAlarmRule";
	        var alarmRuleStr=JSON.stringify(alarmRule);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            alarmRule:alarmRuleStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取指定资源类型的告警规则配置项 
         *@param  资源类型编码 
         *@param  告警配置项类型（1 表示状态，2表示性能）,可选查询参数 
         */
        service.getAlarmRuleItemListByResType=function(resTypeCode,oidType,onSuccess)
        {
            var temp=this.url+"/getAlarmRuleItemListByResType";
	        var resTypeCodeStr=resTypeCode;
	        var oidTypeStr=oidType;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            resTypeCode:resTypeCodeStr,
                            oidType:oidTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据告警规则Id，修改告警规则 
         *@param  告警规则对象，其Id字段不能为null 
         */
        service.updateAlarmRuleById=function(alarmRule,onSuccess)
        {
            var temp=this.url+"/updateAlarmRuleById";
	        var alarmRuleStr=JSON.stringify(alarmRule);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            alarmRule:alarmRuleStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据告警规则Id删除告警规则 
         *@param  告警规则Id集合 
         */
        service.deleteAlarmRuleById=function(ids,onSuccess)
        {
            var temp=this.url+"/deleteAlarmRuleById";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据告警规则Id禁用告警规则 
         *@param  告警规则Id集合 
         */
        service.disableAlarmRuleById=function(ids,onSuccess)
        {
            var temp=this.url+"/disableAlarmRuleById";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取指定资源类型的告警规则 
         *@param  资源类型编码 
         *@param  告警指标类型（1 表示状态，2表示性能）,可选查询参数 
         */
        service.getAlarmRuleListByResType=function(resTypeCode,condType,onSuccess)
        {
            var temp=this.url+"/getAlarmRuleListByResType";
	        var resTypeCodeStr=resTypeCode;
	        var condTypeStr=condType;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            resTypeCode:resTypeCodeStr,
                            condType:condTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据告警规则Id启用告警规则 
         *@param  告警规则Id集合 
         */
        service.enableAlarmRuleById=function(ids,onSuccess)
        {
            var temp=this.url+"/enableAlarmRuleById";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取指定资源的告警规则 
         *@param  资源编码 
         *@param  告警指标类型（1 表示状态，2表示性能）,可选查询参数 
         */
        service.getAlarmRuleListByResCode=function(resCode,condType,onSuccess)
        {
            var temp=this.url+"/getAlarmRuleListByResCode";
	        var resCodeStr=resCode;
	        var condTypeStr=condType;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            resCode:resCodeStr,
                            condType:condTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function AssetMgntService(){
    var service=new Object();
    service.url=host+"/AssetMgntService";
        /**
        *更新资产信息  
         *@param no comment
         */
        service.syncAsset=function(page,onSuccess)
        {
            var temp=this.url+"/syncAsset";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *资产大类信息  
         *@param no comment
         */
        service.listVenderType=function(page,onSuccess)
        {
            var temp=this.url+"/listVenderType";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *更新资产信息  
         *@param  资产类信息  
         */
        service.updateAsset=function(asset,onSuccess)
        {
            var temp=this.url+"/updateAsset";
	        var assetStr=JSON.stringify(asset);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            asset:assetStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *列举资产信息  
         *@param no comment
         */
        service.listAsset=function(page,onSuccess)
        {
            var temp=this.url+"/listAsset";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *资产删除  
         *@param no comment
         */
        service.deleteAsset=function(asset,onSuccess)
        {
            var temp=this.url+"/deleteAsset";
	        var assetStr=JSON.stringify(asset);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            asset:assetStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *资产类型信息  
         *@param no comment
         */
        service.listAssetType=function(page,onSuccess)
        {
            var temp=this.url+"/listAssetType";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.countByMinus=function(page,onSuccess)
        {
            var temp=this.url+"/countByMinus";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.listAssetGet=function(page,onSuccess)
        {
            var temp=this.url+"/listBigType";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *增加资产类型  
         *@param no comment
         */
        service.addAsset=function(asset,onSuccess)
        {
            var temp=this.url+"/addAsset";
	        var assetStr=JSON.stringify(asset);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            asset:assetStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.assetInfoExport=function(page,onSuccess)
        {
            var temp=this.url+"/assetInfoExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *资产大类信息  
         *@param no comment
         */
        service.listBigType=function(page,onSuccess)
        {
            var temp=this.url+"/listBigType";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function AssetRepairService(){
    var service=new Object();
    service.url=host+"/AssetRepairService";
        /**
        *查询维修单  
         *@param no comment
         */
        service.listRepairApply=function(page,onSuccess)
        {
            var temp=this.url+"/listRepairApply";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取维修单号  
         */
        service.getRepairId=function(onSuccess)
        {
            var temp=this.url+"/getRepairId";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *增加维修单  
         *@param no comment
         */
        service.getConfrimedWdId=function(page,onSuccess)
        {
            var temp=this.url+"/getConfrimedWdId";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *增加维修单  
         *@param  增加维修单  
         */
        service.addRepairApply=function(assetRepairApply,onSuccess)
        {
            var temp=this.url+"/addRepairApply";
	        var assetRepairApplyStr=JSON.stringify(assetRepairApply);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            assetRepairApply:assetRepairApplyStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除维修单  
         *@param  资产维修单  
         */
        service.deleteRepairApply=function(assetRepairApply,onSuccess)
        {
            var temp=this.url+"/deleteRepairApply";
	        var assetRepairApplyStr=JSON.stringify(assetRepairApply);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            assetRepairApply:assetRepairApplyStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.assetRepairExport=function(page,onSuccess)
        {
            var temp=this.url+"/assetRepairExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *更新维修单  
         *@param no comment
         */
        service.updateRepairApply=function(assetRepairApply,onSuccess)
        {
            var temp=this.url+"/updateRepairApply";
	        var assetRepairApplyStr=JSON.stringify(assetRepairApply);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            assetRepairApply:assetRepairApplyStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.getAssetInfoByWdId=function(page,onSuccess)
        {
            var temp=this.url+"/getAssetInfoByWdId";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function AssetScrapService(){
    var service=new Object();
    service.url=host+"/AssetScrapService";
        /**
        *更新维修单  
         *@param no comment
         */
        service.updateScrapApply=function(assetScrapApply,onSuccess)
        {
            var temp=this.url+"/updateScrapApply";
	        var assetScrapApplyStr=JSON.stringify(assetScrapApply);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            assetScrapApply:assetScrapApplyStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取维修单号  
         */
        service.getRepairId=function(onSuccess)
        {
            var temp=this.url+"/getScrapId";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *增加报废单  
         *@param  增加报废单  
         */
        service.addScrapApply=function(assetScrapApply,onSuccess)
        {
            var temp=this.url+"/addScrapApply";
	        var assetScrapApplyStr=JSON.stringify(assetScrapApply);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            assetScrapApply:assetScrapApplyStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除维修单  
         *@param no comment
         */
        service.deleteScrapApply=function(assetScrapApply,onSuccess)
        {
            var temp=this.url+"/deleteScrapApply";
	        var assetScrapApplyStr=JSON.stringify(assetScrapApply);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            assetScrapApply:assetScrapApplyStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.listScrapApply=function(page,onSuccess)
        {
            var temp=this.url+"/listScrapApply";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.getAssetInfoByWdId=function(page,onSuccess)
        {
            var temp=this.url+"/getAssetInfoByWdId";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.assetScrapeExport=function(page,onSuccess)
        {
            var temp=this.url+"/assetScrapeExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function AssetStatService(){
    var service=new Object();
    service.url=host+"/AssetStatService";
        /**
        *按全部统计  
         *@param no comment
         */
        service.allAssetStat=function(page,onSuccess)
        {
            var temp=this.url+"/allAssetStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.statusAssetStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/statusAssetStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.vendorAssetStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/vendorAssetStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.orgAssetStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/orgAssetStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按维修报废统计  
         *@param no comment
         */
        service.allRepairScrapStat=function(page,onSuccess)
        {
            var temp=this.url+"/allRepairScrapStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.allAssetStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/allAssetStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.allRepairScrapStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/allRepairScrapStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按供货商维修报废统计  
         *@param no comment
         */
        service.vendorRepairScrapStat=function(page,onSuccess)
        {
            var temp=this.url+"/vendorRepairScrapStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按状态维修报废统计  
         *@param no comment
         */
        service.statusRepairScrapStat=function(page,onSuccess)
        {
            var temp=this.url+"/StatusRepairScrapStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.statusRepairScrapStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/statusRepairScrapStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.orgRepairScrapStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/orgRepairScrapStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按组织维修报废统计  
         *@param no comment
         */
        service.orgRepairScrapStat=function(page,onSuccess)
        {
            var temp=this.url+"/orgRepairScrapStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.venderRepairScrapStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/venderRepairScrapStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按供货商资产统计  
         *@param no comment
         */
        service.vendorAssetStat=function(page,onSuccess)
        {
            var temp=this.url+"/vendorAssetStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按状态资产统计  
         *@param no comment
         */
        service.statusAssetStat=function(page,onSuccess)
        {
            var temp=this.url+"/statusAssetStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按组织资产统计  
         *@param no comment
         */
        service.orgAssetStat=function(page,onSuccess)
        {
            var temp=this.url+"/orgAssetStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function camera(){
    var service=new Object();
    service.url=host+"/camera";
        /**
        *查询摄像机类型列表 
         */
        service.getCameraTypeList=function(onSuccess)
        {
            var temp=this.url+"/type/list";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改摄像机信息 
         *@param  摄像机编码 
         *@param  摄像机对象json字符串 
         */
        service.updateCameraInfoByCOde=function(cResCode,cameraInfo,onSuccess)
        {
            var temp=this.url+"/update";
	        var cResCodeStr=cResCode;
	        var cameraInfoStr=JSON.stringify(cameraInfo);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cResCode:cResCodeStr,
                            cameraInfo:cameraInfoStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除摄像机信息 
         *@param  摄像机编码 
         */
        service.deleteCameraInfoByCOde=function(cResCode,onSuccess)
        {
            var temp=this.url+"/delete";
	        var cResCodeStr=cResCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cResCode:cResCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加摄像机 
         *@param  摄像机名称 
         *@param  组织代码 
         *@param  摄像机类型 
         *@param  电表户号 
         *@param  电费基数,其实就是电的单价 
         *@param  摄像机IP地址 
         *@param  设备登录名称 
         *@param  设备登录密码 
         */
        service.addCamera=function(cResName,cOrgCode,cTypeCode,cEleCode,nElePrice,cIpAddress,cLoginName,cLoginPassword,onSuccess)
        {
            var temp=this.url+"/add";
	        var cResNameStr=cResName;
	        var cOrgCodeStr=cOrgCode;
	        var cTypeCodeStr=cTypeCode;
	        var cEleCodeStr=cEleCode;
	        var nElePriceStr=nElePrice;
	        var cIpAddressStr=cIpAddress;
	        var cLoginNameStr=cLoginName;
	        var cLoginPasswordStr=cLoginPassword;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cResName:cResNameStr,
                            cOrgCode:cOrgCodeStr,
                            cTypeCode:cTypeCodeStr,
                            cEleCode:cEleCodeStr,
                            nElePrice:nElePriceStr,
                            cIpAddress:cIpAddressStr,
                            cLoginName:cLoginNameStr,
                            cLoginPassword:cLoginPasswordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询摄像机列表 
         *@param  分页查询对象 
         *@param  摄像机名称,可选查询条件之一 
         *@param  摄像机类型集合,可选查询条件之一 
         *@param  摄像机IP,可选查询条件之一 
         */
        service.getCameraList=function(page,cResName,cTypeCodes,cIpAddress,onSuccess)
        {
            var temp=this.url+"/list";
	        var pageStr=JSON.stringify(page);
	        var cResNameStr=cResName;
	        var cTypeCodesStr=JSON.stringify(cTypeCodes);
	        var cIpAddressStr=cIpAddress;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            cResName:cResNameStr,
                            cTypeCodes:cTypeCodesStr,
                            cIpAddress:cIpAddressStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function ContractorService(){
    var service=new Object();
    service.url=host+"/ContractorService";
        /**
         * no comments
         *@param no comment
         */
        service.assetInfoExport=function(page,onSuccess)
        {
            var temp=this.url+"/contractorExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *列举设备服务供应商  
         *@param no comment
         */
        service.listVendor=function(page,onSuccess)
        {
            var temp=this.url+"/listVendor";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.updateVendor=function(vendor,onSuccess)
        {
            var temp=this.url+"/updateVendor";
	        var vendorStr=vendor;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            vendor:vendorStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.listVendorGet=function(page,onSuccess)
        {
            var temp=this.url+"/listVendor";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.addVendor=function(vendor,onSuccess)
        {
            var temp=this.url+"/addVendor";
	        var vendorStr=vendor;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            vendor:vendorStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.deleteVendor=function(vendor,onSuccess)
        {
            var temp=this.url+"/deleteVendor";
	        var vendorStr=vendor;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            vendor:vendorStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function DeviceStateController(){
    var service=new Object();
    service.url=host+"/device/state";
        /**
        *查询设备状态  
         *@param page 分页查询参数 
         *@param resTypeCodes 设备具体类型，可选查询条件之一 
         *@param resName 设备名称，可选查询条件之一 
         *@param ipAddress 设备Ip地址，可选查询条件之一 
         *@param status 设备在线状态，2 表示表示查询离线，1表示查询在线，不传表示查询所有， 可选查询条件之一 
         */
        service.getDeviceStateList=function(page,resTypeCodes,resName,ipAddress,status,onSuccess)
        {
            var temp=this.url+"/list";
	        var pageStr=JSON.stringify(page);
	        var resTypeCodesStr=JSON.stringify(resTypeCodes);
	        var resNameStr=resName;
	        var ipAddressStr=ipAddress;
	        var statusStr=status;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            resTypeCodes:resTypeCodesStr,
                            resName:resNameStr,
                            ipAddress:ipAddressStr,
                            status:statusStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按设备状态查询设备信息列表  
         *@param status 设备状态，1 在线，2离线， 
         *@param page 分页查询参数 
         *@param resTypeCodes 设备类型集合,可选查询条件 
         */
        service.getDeviceStateListByStatus=function(status,page,resTypeCodes,onSuccess)
        {
            var temp=this.url+"/{status}/list";
	        var statusStr=status;
	        var pageStr=JSON.stringify(page);
	        var resTypeCodesStr=JSON.stringify(resTypeCodes);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            status:statusStr,
                            page:pageStr,
                            resTypeCodes:resTypeCodesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *导出设备状态列表  
         *@param page 分页查询参数 
         *@param resTypeCodes 设备具体类型，可选查询条件之一 
         *@param resName 设备名称，可选查询条件之一 
         *@param ipAddress 设备Ip地址，可选查询条件之一 
         *@param status 设备在线状态，2 表示表示查询离线，1表示查询在线，不传表示查询所有， 可选查询条件之一 
         */
        service.exportDeviceStateList=function(page,resTypeCodes,resName,ipAddress,status,onSuccess)
        {
            var temp=this.url+"/export";
	        var pageStr=JSON.stringify(page);
	        var resTypeCodesStr=JSON.stringify(resTypeCodes);
	        var resNameStr=resName;
	        var ipAddressStr=ipAddress;
	        var statusStr=status;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            resTypeCodes:resTypeCodesStr,
                            resName:resNameStr,
                            ipAddress:ipAddressStr,
                            status:statusStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *导出设备类型统计设备在线时长列表  
         *@param page 查询参数 
         *@param resTypeCodes 设备类型，可选查询参数 
         *@param startTime 统计开始时间 
         *@param endTime 统计结束时间 
         */
        service.exportOnlineStatisticByType=function(page,resTypeCodes,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/statistic/type/online/export";
	        var pageStr=JSON.stringify(page);
	        var resTypeCodesStr=JSON.stringify(resTypeCodes);
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            resTypeCodes:resTypeCodesStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *导出设备在线时长统计数据列表  
         *@param page 查询分页对象 
         *@param resName 设备名称，可选查询条件之一 
         *@param ipAddress IP地址，可选查询条件之一 
         *@param startTime 起始时间 
         *@param endTime 结束时间 
         */
        service.exportOnlineStatisticByDevice=function(page,resName,ipAddress,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/statistic/device/online/export";
	        var pageStr=JSON.stringify(page);
	        var resNameStr=resName;
	        var ipAddressStr=ipAddress;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            resName:resNameStr,
                            ipAddress:ipAddressStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按区域统计设备在线时长  
         *@param page 查询分页对象 
         *@param startTime 起始时间 
         *@param endTime 结束时间 
         */
        service.getOnlineStatisticByOrg=function(page,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/statistic/org/online";
	        var pageStr=JSON.stringify(page);
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询设备在线时长统计数据列表  
         *@param page 查询分页对象 
         *@param resName 设备名称，可选查询条件之一 
         *@param ipAddress IP地址，可选查询条件之一 
         *@param startTime 起始时间 
         *@param endTime 结束时间 
         */
        service.getOnlineStatisticByDevice=function(page,resName,ipAddress,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/statistic/device/online";
	        var pageStr=JSON.stringify(page);
	        var resNameStr=resName;
	        var ipAddressStr=ipAddress;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            resName:resNameStr,
                            ipAddress:ipAddressStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *导出基于区域统计设备在线时长列表  
         *@param page 查询分页对象 
         *@param startTime 起始时间 
         *@param endTime 结束时间 
         */
        service.exportOnlineStatisticByOrg=function(page,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/statistic/org/online/export";
	        var pageStr=JSON.stringify(page);
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询设备在线历史状态  
         *@param resCode 设备编码 
         *@param startTime 起始时间 
         *@param endTime 结束时间 
         */
        service.getDeviceOnlineHistory=function(resCode,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/history";
	        var resCodeStr=resCode;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            resCode:resCodeStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据设备类型统计设备在线时长  
         *@param page 查询参数 
         *@param resTypeCodes 设备类型，可选查询参数 
         *@param startTime 统计开始时间 
         *@param endTime 统计结束时间 
         */
        service.getOnlineStatisticByType=function(page,resTypeCodes,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/statistic/type/online";
	        var pageStr=JSON.stringify(page);
	        var resTypeCodesStr=JSON.stringify(resTypeCodes);
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            resTypeCodes:resTypeCodesStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取设备在线统计数据  
         *@param resTypeCodes 设备类型集合 
         */
        service.getStatistic=function(resTypeCodes,onSuccess)
        {
            var temp=this.url+"/statistic";
	        var resTypeCodesStr=JSON.stringify(resTypeCodes);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            resTypeCodes:resTypeCodesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function DeviceStatePlanController(){
    var service=new Object();
    service.url=host+"/nms/plan";
        /**
        *添加状态巡检计划  
         *@param planName 计划名称 
         *@param inspectorCode 探针代码 
         *@param checkInterval 巡检间隔 
         *@param targetType 目标类型 1表示指定资源，2表示指定类型 
         *@param targetCodes 目标代码，资源代码或者类型代码 
         */
        service.addPlan=function(planName,inspectorCode,checkInterval,targetType,targetCodes,onSuccess)
        {
            var temp=this.url+"/add";
	        var planNameStr=planName;
	        var inspectorCodeStr=inspectorCode;
	        var checkIntervalStr=checkInterval;
	        var targetTypeStr=targetType;
	        var targetCodesStr=JSON.stringify(targetCodes);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            planName:planNameStr,
                            inspectorCode:inspectorCodeStr,
                            checkInterval:checkIntervalStr,
                            targetType:targetTypeStr,
                            targetCodes:targetCodesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *禁用巡检任务  
         *@param planId 任务Id 
         */
        service.disablePlanById=function(planId,onSuccess)
        {
            var temp=this.url+"/disable";
	        var planIdStr=planId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            planId:planIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *加载检测资源  
         *@param resTypes 资源类型集合 
         *@param page 查询对象 
         */
        service.getResourceList=function(resTypes,page,onSuccess)
        {
            var temp=this.url+"/target/res";
	        var resTypesStr=JSON.stringify(resTypes);
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            resTypes:resTypesStr,
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *启用巡检任务  
         *@param planId 任务Id 
         */
        service.enablePlanById=function(planId,onSuccess)
        {
            var temp=this.url+"/enable";
	        var planIdStr=planId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            planId:planIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改状态巡检计划  
         *@param planId 计划Id 
         *@param planName 计划名称 
         *@param inspectorCode 探针代码 
         *@param checkInterval 巡检间隔 
         *@param targetType 目标类型 1表示指定资源，2表示指定类型 
         *@param targetCodes 目标代码，资源代码或者类型代码 
         */
        service.updatePlanById=function(planId,planName,inspectorCode,checkInterval,targetType,targetCodes,onSuccess)
        {
            var temp=this.url+"/update";
	        var planIdStr=planId;
	        var planNameStr=planName;
	        var inspectorCodeStr=inspectorCode;
	        var checkIntervalStr=checkInterval;
	        var targetTypeStr=targetType;
	        var targetCodesStr=JSON.stringify(targetCodes);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            planId:planIdStr,
                            planName:planNameStr,
                            inspectorCode:inspectorCodeStr,
                            checkInterval:checkIntervalStr,
                            targetType:targetTypeStr,
                            targetCodes:targetCodesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除巡检任务  
         *@param planId 任务Id 
         */
        service.deletePlanById=function(planId,onSuccess)
        {
            var temp=this.url+"/delete";
	        var planIdStr=planId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            planId:planIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询巡检任务列表  
         *@param page 查询分页参数 
         *@param planName 任务名称，可选查询条件之一 
         *@param planTime 任务时间配置，可选查询条件之一 
         *@param inspectorCode 探针代码，可选查询条件之一 
         *@param targetType 目标类型 1表示指定资源，2表示指定类型，可选查询条件之一 
         */
        service.getPlanList=function(page,planName,planTime,inspectorCode,targetType,onSuccess)
        {
            var temp=this.url+"/list";
	        var pageStr=JSON.stringify(page);
	        var planNameStr=planName;
	        var planTimeStr=planTime;
	        var inspectorCodeStr=inspectorCode;
	        var targetTypeStr=targetType;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            planName:planNameStr,
                            planTime:planTimeStr,
                            inspectorCode:inspectorCodeStr,
                            targetType:targetTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *加载指定巡检任务的检测资源类型  
         *@param planId 巡检任务Id 
         *@param categoryCodes 资源类型种类集合 1表示编码器，2表示摄像机，3表示服务 
         */
        service.getResourceTypeListByPlanId=function(planId,categoryCodes,onSuccess)
        {
            var temp=this.url+"/{planId}/target/type";
	        var planIdStr=planId;
	        var categoryCodesStr=JSON.stringify(categoryCodes);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            planId:planIdStr,
                            categoryCodes:categoryCodesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *加载检测资源类型  
         *@param categoryCodes 资源类型种类集合 1表示编码器，2表示摄像机，3表示服务 
         */
        service.getResourceTypeList=function(categoryCodes,onSuccess)
        {
            var temp=this.url+"/target/type";
	        var categoryCodesStr=JSON.stringify(categoryCodes);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            categoryCodes:categoryCodesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *加载指定巡检任务的检测资源  
         *@param planId 巡检任务Id 
         *@param resTypes 资源类型集合 
         *@param page 查询对象 
         */
        service.getResourceListByPlanId=function(planId,resTypes,page,onSuccess)
        {
            var temp=this.url+"/{planId}/target/res";
	        var planIdStr=planId;
	        var resTypesStr=JSON.stringify(resTypes);
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            planId:planIdStr,
                            resTypes:resTypesStr,
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function dictService(){
    var service=new Object();
    service.url=host+"/dictService";
        /**
        *添加字典  
         *@param detail 待添加的字典对象 
         */
        service.addDictDetail=function(detail,onSuccess)
        {
            var temp=this.url+"/addDictDetail";
	        var detailStr=JSON.stringify(detail);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            detail:detailStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件查询字典信息  
         *@param page 分页查询对象 
         */
        service.getDictList=function(page,onSuccess)
        {
            var temp=this.url+"/getDictList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据字典代码修改字典信息  
         *@param no comment
         */
        service.updateDictDetail=function(dictDetail,onSuccess)
        {
            var temp=this.url+"/updateDictDetail";
	        var dictDetailStr=JSON.stringify(dictDetail);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            dictDetail:dictDetailStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *从excel中导入字典信息  
         *@param file excel文件对象 
         */
        service.importExcel=function(file,onSuccess)
        {
            var temp=this.url+"/importExcel";
	        var fileStr=JSON.stringify(file);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            file:fileStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取字典信息excel导入模板数据项集合  
         */
        service.getExcelFieldList=function(onSuccess)
        {
            var temp=this.url+"/getExcelFieldList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取资源类型的在特定工作场景下支持的操作  
         *@param resTypeCode 资源类型，可选查询条件 
         *@param module 模块名称 
         */
        service.getResourceOperation=function(resTypeCode,module,onSuccess)
        {
            var temp=this.url+"/res/operation";
	        var resTypeCodeStr=resTypeCode;
	        var moduleStr=module;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            resTypeCode:resTypeCodeStr,
                            module:moduleStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *下载字典导入模板  
         *@param ids 选择的可选项Id 
         */
        service.downloadExcelTemplate=function(ids,onSuccess)
        {
            var temp=this.url+"/downloadExcelTemplate";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据字典代码，删除字典信息  
         *@param codes 
         */
        service.deleteDetailtByCodes=function(codes,onSuccess)
        {
            var temp=this.url+"/deleteDetailByCodes";
	        var codesStr=JSON.stringify(codes);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            codes:codesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取资源类型  
         */
        service.getResourceTypeList=function(onSuccess)
        {
            var temp=this.url+"/getResourceTypeList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取设备厂商类型  
         */
        service.getManufacturerTypeList=function(onSuccess)
        {
            var temp=this.url+"/getManufacturerTypeList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function docService(){
    var service=new Object();
    service.url=host+"/docService";
        /**
         * no comments
         *@param no comment
         */
        service.downloadDoc=function(id,onSuccess)
        {
            var temp=this.url+"/downloadDoc";
	        var idStr=id;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            id:idStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.getAllDocList=function(page,onSuccess)
        {
            var temp=this.url+"/getAllDocList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         */
        service.deleteDoc=function(onSuccess)
        {
            var temp=this.url+"/deleteDoc";
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         */
        service.updateDoc=function(onSuccess)
        {
            var temp=this.url+"/updateDoc";
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         */
        service.createDoc=function(onSuccess)
        {
            var temp=this.url+"/createDoc";
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function eleService(){
    var service=new Object();
    service.url=host+"/eleService";
        /**
         * no comments
         *@param no comment
         */
        service.createEleBill=function(elecBill,onSuccess)
        {
            var temp=this.url+"/addBillInfo";
	        var elecBillStr=JSON.stringify(elecBill);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            elecBill:elecBillStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.deleteEleBill=function(elecBill,onSuccess)
        {
            var temp=this.url+"/deleteEleBill";
	        var elecBillStr=JSON.stringify(elecBill);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            elecBill:elecBillStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.updateEleBill=function(elecBill,onSuccess)
        {
            var temp=this.url+"/updateEleBill";
	        var elecBillStr=JSON.stringify(elecBill);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            elecBill:elecBillStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
         * no comments
         *@param no comment
         */
        service.getElectrityBillList=function(page,onSuccess)
        {
            var temp=this.url+"/getElectrityBillList";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function fileService(){
    var service=new Object();
    service.url=host+"/fileService";
        /**
        *vqd诊断图片上传接口  
         *@param cResultId 诊断结果Id 
         *@param file 文件对象 
         *@param index 序号，可以为null 
         */
        service.uploadVQDPicture=function(cResultId,file,index,onSuccess)
        {
            var temp=this.url+"/uploadVQDPicture";
	        var cResultIdStr=cResultId;
	        var fileStr=JSON.stringify(file);
	        var indexStr=index;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cResultId:cResultIdStr,
                            file:fileStr,
                            index:indexStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取VQD诊断结果图片保存路径，仅适用于单机版  
         */
        service.getVQDPicturePath=function(onSuccess)
        {
            var temp=this.url+"getVQDPicturePath";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *vqd诊断图片下载接口  
         *@param cResultId 诊断结果Id 
         *@param index 序号，可以为null 
         */
        service.downloadVQDPicture=function(cResultId,index,onSuccess)
        {
            var temp=this.url+"/downloadVQDPicture";
	        var cResultIdStr=cResultId;
	        var indexStr=index;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cResultId:cResultIdStr,
                            index:indexStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *工单反馈图片上传接口  
         *@param cWdId 工单编号 
         *@param file 文件 
         *@param index 
         */
        service.uploadWorkOrderFeedbackPicture=function(cWdId,file,index,onSuccess)
        {
            var temp=this.url+"/uploadWorkOrderFeedbackPicture";
	        var cWdIdStr=cWdId;
	        var fileStr=JSON.stringify(file);
	        var indexStr=index;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cWdId:cWdIdStr,
                            file:fileStr,
                            index:indexStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *下载工单反馈图片  
         *@param cWdId 工单编号 
         *@param fileName 文件名 
         */
        service.downloadWorkOrderFeedbackPicture=function(cWdId,fileName,onSuccess)
        {
            var temp=this.url+"/downloadWorkOrderFeedbackPicture";
	        var cWdIdStr=cWdId;
	        var fileNameStr=fileName;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cWdId:cWdIdStr,
                            fileName:fileNameStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取工单反馈图片列表  
         *@param cWdId 工单编号 
         */
        service.getWorkOrderFeedbackPicture=function(cWdId,onSuccess)
        {
            var temp=this.url+"/getWorkOrderFeedbackPicture";
	        var cWdIdStr=cWdId;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cWdId:cWdIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function linkageService(){
    var service=new Object();
    service.url=host+"/linkageService";
        /**
        *根据组织加载用户工单联动配置信息  
         *@param cOrgCode 组织代码 
         */
        service.getUserWorkorderLinkageConfigInfo=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/getUserWorkorderLinkageConfigInfo";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *创建告警联动,注意 timeSchedules 与 planDetails 两个集合大小一致，并一一对应  
         *@param linkagePlan 联动基本信息对象 
         *@param timeSchedules 联动时间对象集合 
         *@param planDetails 联动参数配置对象集合 
         */
        service.addAlarmLinkage=function(linkagePlan,timeSchedules,planDetails,onSuccess)
        {
            var temp=this.url+"/addAlarmLinkage";
	        var linkagePlanStr=JSON.stringify(linkagePlan);
	        var timeSchedulesStr=JSON.stringify(timeSchedules);
	        var planDetailsStr=JSON.stringify(planDetails);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            linkagePlan:linkagePlanStr,
                            timeSchedules:timeSchedulesStr,
                            planDetails:planDetailsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据联动编码修改联动配置,注意 timeSchedules 与 planDetails 两个集合大小一致，并一一对应  
         *@param linkagePlan 联动基本信息对象，编码字段不能为空 
         *@param timeSchedules 联动时间对象集合 编码字段不能为空 
         *@param planDetails 联动参数配置对象集合 编码字段不能为空 
         */
        service.updateLinkageByCode=function(linkagePlan,timeSchedules,planDetails,onSuccess)
        {
            var temp=this.url+"/updateLinkageByCode";
	        var linkagePlanStr=JSON.stringify(linkagePlan);
	        var timeSchedulesStr=JSON.stringify(timeSchedules);
	        var planDetailsStr=JSON.stringify(planDetails);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            linkagePlan:linkagePlanStr,
                            timeSchedules:timeSchedulesStr,
                            planDetails:planDetailsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件，分页查询告警联动列表  
         *@param page 查询对象 
         *@param loginName 用户名称 
         */
        service.getAlarmLinkageList=function(page,loginName,onSuccess)
        {
            var temp=this.url+"/getAlarmLinkageList";
	        var pageStr=JSON.stringify(page);
	        var loginNameStr=loginName;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            loginName:loginNameStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件，分页查询工单联动列表  
         *@param page 查询对象 
         *@param loginName 用户名称 
         */
        service.getWorkOrderLinkageList=function(page,loginName,onSuccess)
        {
            var temp=this.url+"/getWorkOrderLinkageList";
	        var pageStr=JSON.stringify(page);
	        var loginNameStr=loginName;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            loginName:loginNameStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据联动编号删除联动配置  
         *@param codes 联动编号集合 
         */
        service.deleteLinkageByCode=function(codes,onSuccess)
        {
            var temp=this.url+"/deleteLinkageByCode";
	        var codesStr=JSON.stringify(codes);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            codes:codesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据联动配置代码获取联动配置详情  
         *@param cLinkageCode 联动配置编码 
         */
        service.getLinkageDetailInfoByCode=function(cLinkageCode,onSuccess)
        {
            var temp=this.url+"/getLinkageDetailInfo";
	        var cLinkageCodeStr=cLinkageCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cLinkageCode:cLinkageCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据组织加载用户告警联动配置信息  
         *@param cOrgCode 组织代码 
         */
        service.getUserAlarmLinkageConfigInfo=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/getUserAlarmLinkageConfigInfo";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *创建工单联动,注意 timeSchedules 与 planDetails 两个集合大小一致，并一一对应  
         *@param linkagePlan 联动基本信息对象 
         *@param timeSchedules 联动时间对象集合 
         *@param planDetails 联动参数配置对象集合 
         */
        service.addWorkOrderLinkage=function(linkagePlan,timeSchedules,planDetails,onSuccess)
        {
            var temp=this.url+"/addWorkOrderLinkage";
	        var linkagePlanStr=JSON.stringify(linkagePlan);
	        var timeSchedulesStr=JSON.stringify(timeSchedules);
	        var planDetailsStr=JSON.stringify(planDetails);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            linkagePlan:linkagePlanStr,
                            timeSchedules:timeSchedulesStr,
                            planDetails:planDetailsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function logService(){
    var service=new Object();
    service.url=host+"/logService";
        /**
        *查询模块列表 
         */
        service.getModuleList=function(onSuccess)
        {
            var temp=this.url+"/getModuleList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询操作列表 
         *@param no comment
         */
        service.getActList=function(cModule,onSuccess)
        {
            var temp=this.url+"/getActList";
	        var cModuleStr=cModule;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cModule:cModuleStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询操作日志 
         *@param  查询条件对象 
         */
        service.getLogInfoList=function(page,onSuccess)
        {
            var temp=this.url+"/getLogInfoList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *导出操作日志 
         *@param  查询条件对象 
         */
        service.exportLogInfoList=function(page,onSuccess)
        {
            var temp=this.url+"/exportLogInfoList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function menuService(){
    var service=new Object();
    service.url=host+"/menuService";
        /**
        *获取子菜单 
         *@param  父菜单Id 
         */
        service.getChildMenu=function(cParentId,onSuccess)
        {
            var temp=this.url+"/getChildMenu";
	        var cParentIdStr=cParentId;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cParentId:cParentIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取用户基本菜单 
         */
        service.getRootMenu=function(onSuccess)
        {
            var temp=this.url+"/getRootMenu";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *加载指定权限的具体描述,即页面权限描述 
         *@param  权限ID 
         */
        service.getPagePermission=function(cPermissionId,onSuccess)
        {
            var temp=this.url+"/getPagePermission";
	        var cPermissionIdStr=cPermissionId;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cPermissionId:cPermissionIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function device(){
    var service=new Object();
    service.url=host+"/device";
        /**
        *添加网络设备  
         *@param resInfo 网络设备基本信息 
         *@param deviceTypeCode 网络设备类型代码 例如666 
         */
        service.addNetworkDevice=function(resInfo,deviceTypeCode,onSuccess)
        {
            var temp=this.url+"/add";
	        var resInfoStr=JSON.stringify(resInfo);
	        var deviceTypeCodeStr=deviceTypeCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            resInfo:resInfoStr,
                            deviceTypeCode:deviceTypeCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取所有的网络设备类型  
         */
        service.getNetworkDeviceTypeList=function(onSuccess)
        {
            var temp=this.url+"/type/list";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除网络设备  
         *@param codes 网络设备代码集合 
         */
        service.deleteNetworkDeviceByCode=function(codes,onSuccess)
        {
            var temp=this.url+"/delete";
	        var codesStr=JSON.stringify(codes);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            codes:codesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询网络设备列表  
         *@param page 分页查询参数 
         *@param resTypeCodes 网络设备类型集合，可选查询条件之一 
         *@param resName 设备名称，可选查询条件之一 
         *@param ipAddress 设备IP，可选查询条件之一 
         */
        service.getNetworkDeviceList=function(page,resTypeCodes,resName,ipAddress,onSuccess)
        {
            var temp=this.url+"/list";
	        var pageStr=JSON.stringify(page);
	        var resTypeCodesStr=JSON.stringify(resTypeCodes);
	        var resNameStr=resName;
	        var ipAddressStr=ipAddress;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            resTypeCodes:resTypeCodesStr,
                            resName:resNameStr,
                            ipAddress:ipAddressStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询指定组织下的网络设备列表  
         *@param cOrgCode 组织编码 
         */
        service.getNetworkDeviceByOrgCode=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/org/list";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据IP地址段批量添加设备,如要录入[192.168.0.1]-[192.168.0.157]之间的设备,ipPrefix为[192.168.0.],ipStart为[1],ipEnd为[157] 
         *@param  设备基础信息 
         *@param  设备类型代码 
         *@param  IP前缀,例如IP地址[192.168.0.157],该Ip地址对应前缀为[192.168.0.] 
         *@param  ip地址起始位置,包含ipStart 
         *@param  ip地址结束为止,包含ipEnd 
         */
        service.addNetworkDeviceByIpSection=function(resInfo,deviceTypeCode,ipPrefix,ipStart,ipEnd,onSuccess)
        {
            var temp=this.url+"/add/ip";
	        var resInfoStr=JSON.stringify(resInfo);
	        var deviceTypeCodeStr=deviceTypeCode;
	        var ipPrefixStr=ipPrefix;
	        var ipStartStr=ipStart;
	        var ipEndStr=ipEnd;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            resInfo:resInfoStr,
                            deviceTypeCode:deviceTypeCodeStr,
                            ipPrefix:ipPrefixStr,
                            ipStart:ipStartStr,
                            ipEnd:ipEndStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改网络设备  
         *@param deviceCode 网络设备代码 
         *@param resInfo 网络设备信息 
         */
        service.updateNetworkDeviceByCode=function(deviceCode,resInfo,onSuccess)
        {
            var temp=this.url+"/update";
	        var deviceCodeStr=deviceCode;
	        var resInfoStr=JSON.stringify(resInfo);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            deviceCode:deviceCodeStr,
                            resInfo:resInfoStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function orgService(){
    var service=new Object();
    service.url=host+"/orgService";
        /**
        *根据组织代码,查询该组织的直接子组织节点 
         *@param  组织代码,访问根节点请传入-1 
         */
        service.getChildList=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/getChildList";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *判断组织名称orgName是否已存在 
         *@param  组织名称 
         */
        service.existOrgName=function(orgName,onSuccess)
        {
            var temp=this.url+"/existOrgName";
	        var orgNameStr=orgName;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            orgName:orgNameStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据组织代码删除组织,包括其所有子节点,其以及其所有子节点所含有的资源(服务,用户,设备等一切资源) 
         *@param  组织代码 
         */
        service.deleteOrgByCode=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/deleteOrgByCode";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *将指定组织cOrgCode下的组织信息，导出到Excel 
         *@param  组织代码 
         */
        service.exportOrgList=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/exportOrgList";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据组织编码更新组织信息 
         *@param  组织对象,注意编码字段自动不能为空 
         */
        service.updateOrg=function(org,onSuccess)
        {
            var temp=this.url+"/updateOrg";
	        var orgStr=JSON.stringify(org);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            org:orgStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *分页 多条件 排序查询组织信息 
         *@param  查询分装对象 
         */
        service.getOrgList=function(page,onSuccess)
        {
            var temp=this.url+"/getOrgList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *新增组织 
         *@param  组织对象 
         *@param  地区代码 
         */
        service.addOrg=function(org,areaCode,onSuccess)
        {
            var temp=this.url+"/addOrg";
	        var orgStr=JSON.stringify(org);
	        var areaCodeStr=areaCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            org:orgStr,
                            areaCode:areaCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取当前行政地区的子行政地区 
         *@param  当前行政地区编码，根行政地区的父编码为000000， 
         */
        service.getChildArea=function(cParentCode,onSuccess)
        {
            var temp=this.url+"/getChildArea";
	        var cParentCodeStr=cParentCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cParentCode:cParentCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *一次查询所有组织信息 
         */
        service.listAllOrg=function(onSuccess)
        {
            var temp=this.url+"/listAllOrg";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询一个组织信息 
         *@param  查询对象code 
         */
        service.getOrgByCode=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/getOrgByCode";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *从Excel中导入组织信息 
         *@param  Excel文件对象 
         *@param  地区代码 
         */
        service.importExcel=function(file,cAreaCode,onSuccess)
        {
            var temp=this.url+"/importExcel";
	        var fileStr=JSON.stringify(file);
	        var cAreaCodeStr=cAreaCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            file:fileStr,
                            cAreaCode:cAreaCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取组织Excel模板数据项 
         */
        service.getExcelFieldList=function(onSuccess)
        {
            var temp=this.url+"/getExcelFieldList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *下载组织Excel数据导入模块 
         *@param  选中的数据导入项id集合 
         */
        service.downloadExcelTemplate=function(ids,onSuccess)
        {
            var temp=this.url+"/downloadExcelTemplate";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function platformService(){
    var service=new Object();
    service.url=host+"/platformService";
        /**
        *获取移动端的软件版本信息 
         */
        service.getVersionInfo=function(onSuccess)
        {
            var temp=this.url+"/getVersionInfo";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function roleService(){
    var service=new Object();
    service.url=host+"/roleService";
        /**
        *根据角色id批量禁用角色 
         *@param  角色id集合 
         */
        service.disableRoleByIds=function(cRoleIds,onSuccess)
        {
            var temp=this.url+"/disableRoleByIds";
	        var cRoleIdsStr=JSON.stringify(cRoleIds);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleIds:cRoleIdsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *分页,多条件,排序查询 角色信息 
         *@param no comment
         */
        service.getRoleList=function(page,onSuccess)
        {
            var temp=this.url+"/getRoleList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *剥夺角色的资源 
         *@param  角色id 
         *@param  资源代码集合 
         *@param  资源类型 
         */
        service.depriveResource=function(cRoleId,cResCodes,iResourceType,onSuccess)
        {
            var temp=this.url+"/depriveResource";
	        var cRoleIdStr=cRoleId;
	        var cResCodesStr=JSON.stringify(cResCodes);
	        var iResourceTypeStr=iResourceType;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleId:cRoleIdStr,
                            cResCodes:cResCodesStr,
                            iResourceType:iResourceTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件 导出角色信息 
         *@param  分页查询对象 
         */
        service.exportRoleList=function(page,onSuccess)
        {
            var temp=this.url+"/exportRoleList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据角色id批量启用角色 
         *@param  角色id集合 
         */
        service.enableRoleByIds=function(cRoleIds,onSuccess)
        {
            var temp=this.url+"/enableRoleByIds";
	        var cRoleIdsStr=JSON.stringify(cRoleIds);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleIds:cRoleIdsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据角色，组织，资源类型加载资源 
         *@param  角色id 
         *@param  资源类型 
         *@param no comment
         */
        service.getResourceList=function(cRoleId,iResourceType,page,onSuccess)
        {
            var temp=this.url+"/getResourceList";
	        var cRoleIdStr=cRoleId;
	        var iResourceTypeStr=iResourceType;
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cRoleId:cRoleIdStr,
                            iResourceType:iResourceTypeStr,
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *从Excel中导入角色信息 
         *@param  excel文件对象 
         */
        service.importExcel=function(file,onSuccess)
        {
            var temp=this.url+"/importExcel";
	        var fileStr=JSON.stringify(file);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            file:fileStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *判断是否存在角色名称 
         *@param  角色名称 
         */
        service.existRoleName=function(roleName,onSuccess)
        {
            var temp=this.url+"/existRoleName";
	        var roleNameStr=roleName;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            roleName:roleNameStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据角色id,修改角色信息 
         *@param  角色对象,注意其cRoleId不能为null 
         */
        service.updateRoleById=function(role,onSuccess)
        {
            var temp=this.url+"/updateRoleById";
	        var roleStr=JSON.stringify(role);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            role:roleStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *授予角色权限 
         *@param  角色id 
         *@param no comment
         */
        service.grantPermission=function(cRoleId,permissions,onSuccess)
        {
            var temp=this.url+"/grantPermission";
	        var cRoleIdStr=cRoleId;
	        var permissionsStr=JSON.stringify(permissions);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleId:cRoleIdStr,
                            permissions:permissionsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *给角色分配资源 
         *@param  角色id 
         *@param  资源代码集合json数据格式 
         *@param  资源类型 
         */
        service.grantResource=function(cRoleId,cResCodes,iResourceType,onSuccess)
        {
            var temp=this.url+"/grantResource";
	        var cRoleIdStr=cRoleId;
	        var cResCodesStr=JSON.stringify(cResCodes);
	        var iResourceTypeStr=iResourceType;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleId:cRoleIdStr,
                            cResCodes:cResCodesStr,
                            iResourceType:iResourceTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *重新给角色分配资源 
         *@param  角色id 
         *@param  资源编码集合 
         *@param  资源类型 
         */
        service.resetResource=function(cRoleId,cResCodes,iResourceType,onSuccess)
        {
            var temp=this.url+"/resetResource";
	        var cRoleIdStr=cRoleId;
	        var cResCodesStr=JSON.stringify(cResCodes);
	        var iResourceTypeStr=iResourceType;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleId:cRoleIdStr,
                            cResCodes:cResCodesStr,
                            iResourceType:iResourceTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据角色id删除角色 
         *@param  角色id集合 
         */
        service.deleteRoleByIds=function(ids,onSuccess)
        {
            var temp=this.url+"/deleteRoleByIds";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加角色 
         *@param  角色对象 
         */
        service.addRole=function(role,onSuccess)
        {
            var temp=this.url+"/addRole";
	        var roleStr=JSON.stringify(role);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            role:roleStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *重新授予角色权限 
         *@param  角色id 
         *@param no comment
         */
        service.resetPermission=function(cRoleId,permissions,onSuccess)
        {
            var temp=this.url+"/resetPermission";
	        var cRoleIdStr=cRoleId;
	        var permissionsStr=JSON.stringify(permissions);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleId:cRoleIdStr,
                            permissions:permissionsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取角色权限信息 
         *@param  角色Id 
         *@param  权限父类代码 
         */
        service.getPermissionByParentCode=function(cRoleId,cParentCode,onSuccess)
        {
            var temp=this.url+"/getPermissionByParentCode";
	        var cRoleIdStr=cRoleId;
	        var cParentCodeStr=cParentCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cRoleId:cRoleIdStr,
                            cParentCode:cParentCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *剥夺角色权限 
         *@param  角色id 
         *@param no comment
         */
        service.deprivePermission=function(cRoleId,permissions,onSuccess)
        {
            var temp=this.url+"/deprivePermission";
	        var cRoleIdStr=cRoleId;
	        var permissionsStr=JSON.stringify(permissions);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleId:cRoleIdStr,
                            permissions:permissionsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取角色基本权限信息 
         *@param  角色id 
         */
        service.getRootPermissionByRoleId=function(cRoleId,onSuccess)
        {
            var temp=this.url+"/getRootPermission";
	        var cRoleIdStr=cRoleId;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cRoleId:cRoleIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取角色导入模板Excel数据项 
         */
        service.getExcelFieldList=function(onSuccess)
        {
            var temp=this.url+"/getExcelFieldList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *下载角色导入excel模板 
         *@param  选中的excel数据导入项id集合 
         */
        service.downloadExcelTemplate=function(ids,onSuccess)
        {
            var temp=this.url+"/downloadExcelTemplate";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function serviceBaseInfoService(){
    var service=new Object();
    service.url=host+"/serviceBaseInfoService";
        /**
        *多条件 分页 排序查询服务模块 
         *@param  分页对象 
         */
        service.getServiceList=function(page,onSuccess)
        {
            var temp=this.url+"/getServiceList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取所有cms列表 
         */
        service.getCMSList=function(onSuccess)
        {
            var temp=this.url+"/getCMSList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加服务模块 
         *@param  服务基本信息 
         *@param  服务类型编码 
         */
        service.addService=function(resInfo,serviceTypeCode,onSuccess)
        {
            var temp=this.url+"/addService";
	        var resInfoStr=JSON.stringify(resInfo);
	        var serviceTypeCodeStr=serviceTypeCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            resInfo:resInfoStr,
                            serviceTypeCode:serviceTypeCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据服务代码更新服务模块的信息，注意编码字段不能为空 
         *@param  服务基本信息 
         */
        service.updateServiceByCode=function(resInfo,onSuccess)
        {
            var temp=this.url+"/updateServiceByCode";
	        var resInfoStr=JSON.stringify(resInfo);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            resInfo:resInfoStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取所有的服务类型 
         */
        service.getServiceTypeList=function(onSuccess)
        {
            var temp=this.url+"/getServiceTypeList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *分页查询离线服务模块 
         *@param  分页对象 
         */
        service.getOfflineServiceList=function(page,onSuccess)
        {
            var temp=this.url+"/getOfflineServiceList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件 分页 排序查询导出服务模块 
         *@param  分页对象 
         */
        service.exportServiceList=function(page,onSuccess)
        {
            var temp=this.url+"/exportServiceList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据服务code删除服务模块 
         *@param  服务code集合 
         */
        service.deleteServiceByCode=function(codes,onSuccess)
        {
            var temp=this.url+"/deleteServiceByCode";
	        var codesStr=JSON.stringify(codes);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            codes:codesStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *分页查询在线服务模块 
         *@param  分页对象 
         */
        service.getOnlineServiceList=function(page,onSuccess)
        {
            var temp=this.url+"/getOnlineServiceList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取服务在线率 
         */
        service.getServiceStatusComposition=function(onSuccess)
        {
            var temp=this.url+"/getServiceStatusComposition";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询指定组织下的服务列表 
         *@param  组织编码 
         */
        service.getServiceListByOrgCode=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/getServiceListByOrgCode";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *从Excel中导入服务信息到指定组织下 
         *@param  Excel文件对象 
         *@param  组织代码 
         */
        service.importExcel=function(file,cOrgCode,onSuccess)
        {
            var temp=this.url+"/importExcel";
	        var fileStr=JSON.stringify(file);
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            file:fileStr,
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取服务信息Excel导入模板数据项 
         */
        service.getExcelFieldList=function(onSuccess)
        {
            var temp=this.url+"/getExcelFieldList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *下载服务信息Excel导入模板 
         *@param  选中的Excel数据导入项 
         */
        service.downloadExcelTemplate=function(ids,onSuccess)
        {
            var temp=this.url+"/downloadExcelTemplate";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function systemService(){
    var service=new Object();
    service.url=host+"/systemService";
        /**
        *设置系统变量  
         *@param cServerCode 服务编码，如果为空，默认为本服务模块 
         *@param name 变量名称 
         *@param value 变量内容 
         */
        service.setEnv=function(cServerCode,name,value,onSuccess)
        {
            var temp=this.url+"/variable/set";
	        var cServerCodeStr=cServerCode;
	        var nameStr=name;
	        var valueStr=value;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cServerCode:cServerCodeStr,
                            name:nameStr,
                            value:valueStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *读取服务模块配置信息  
         *@param cServerCode 服务模块编码，参数可选，默认本服务模块 
         */
        service.readSystemInfo=function(cServerCode,onSuccess)
        {
            var temp=this.url+"/readSystemInfo";
	        var cServerCodeStr=cServerCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cServerCode:cServerCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *读取系统变量  
         *@param cServerCode 服务编码，如果为空，默认为本服务模块 
         *@param name 变量名称 
         */
        service.readEnv=function(cServerCode,name,onSuccess)
        {
            var temp=this.url+"/variable/read";
	        var cServerCodeStr=cServerCode;
	        var nameStr=name;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cServerCode:cServerCodeStr,
                            name:nameStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *
         *@param cServerCode 服务编码，参数可选，默认为本服务模块 
         *@param file 系统logo文件，参数可选 
         *@param pictureStorageThreshold 图片存储上限单位GB，参数可选 
         *@param cSystemName 系统名，参数可选 
         *@param operateLogReserveDay 操作日志保存天数，参数可选 
         *@param systemLogReserveDay 系统日志保存天数，参数可选 
         */
        service.setSystemInfo=function(cServerCode,file,pictureStorageThreshold,cSystemName,operateLogReserveDay,systemLogReserveDay,onSuccess)
        {
            var temp=this.url+"/setSystemInfo";
	        var cServerCodeStr=cServerCode;
	        var fileStr=JSON.stringify(file);
	        var pictureStorageThresholdStr=pictureStorageThreshold;
	        var cSystemNameStr=cSystemName;
	        var operateLogReserveDayStr=operateLogReserveDay;
	        var systemLogReserveDayStr=systemLogReserveDay;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cServerCode:cServerCodeStr,
                            file:fileStr,
                            pictureStorageThreshold:pictureStorageThresholdStr,
                            cSystemName:cSystemNameStr,
                            operateLogReserveDay:operateLogReserveDayStr,
                            systemLogReserveDay:systemLogReserveDayStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除服务模块配置信息  
         *@param cServerCode 服务模块编码，参数可选，默认本服务模块 
         */
        service.deleteSystemInfo=function(cServerCode,onSuccess)
        {
            var temp=this.url+"/deleteSystemInfo";
	        var cServerCodeStr=cServerCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cServerCode:cServerCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function userService(){
    var service=new Object();
    service.url=host+"/userService";
        /**
        *获取当前登录用户信息 
         */
        service.getUserInfo=function(onSuccess)
        {
            var temp=this.url+"/getUserInfo";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据用户Id批量禁用用户账号 
         *@param  用户Id集合 
         */
        service.disableUserByIds=function(ids,onSuccess)
        {
            var temp=this.url+"/disableUsers";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *登出 
         */
        service.logout=function(onSuccess)
        {
            var temp=this.url+"/logout";
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据用户Id批量启用用户账号 
         *@param  用户 id集合 
         */
        service.enableUserByIds=function(ids,onSuccess)
        {
            var temp=this.url+"/enableUserByIds";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据用户id更新用户信息 
         *@param  用户信息 
         */
        service.updateUser=function(user,onSuccess)
        {
            var temp=this.url+"/updateUser";
	        var userStr=JSON.stringify(user);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            user:userStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件 分页 排序 查询用户信息 
         *@param  查询对象 
         */
        service.getUserList=function(page,onSuccess)
        {
            var temp=this.url+"/getUserList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *判断是否已存在用户名 
         *@param  用户名 
         */
        service.existLoginName=function(loginName,onSuccess)
        {
            var temp=this.url+"/existLoginName";
	        var loginNameStr=loginName;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            loginName:loginNameStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据用户的id批量删除用户 
         *@param  用户id集合 
         */
        service.deleteUsers=function(ids,onSuccess)
        {
            var temp=this.url+"/deleteUsers";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件 分页 排序 查询导出用户信息到excel 
         *@param  查询对象 
         */
        service.exportUserList=function(page,onSuccess)
        {
            var temp=this.url+"/exportUserList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加用户接口 
         *@param  用户对象 
         *@param  用户账号过期时间 long 类型整数 
         */
        service.addUser=function(user,expire,onSuccess)
        {
            var temp=this.url+"/addUser";
	        var userStr=JSON.stringify(user);
	        var expireStr=expire;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            user:userStr,
                            expire:expireStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改当前用户密码 
         *@param  旧密码 
         *@param  新密码 
         */
        service.updatePassword=function(oldPassword,newPassword,onSuccess)
        {
            var temp=this.url+"/updatePassword";
	        var oldPasswordStr=oldPassword;
	        var newPasswordStr=newPassword;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            oldPassword:oldPasswordStr,
                            newPassword:newPasswordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据用户Id批量修改用户所属角色 
         *@param  用户id集合 
         *@param  角色id 
         */
        service.updateUserRoleById=function(ids,cRoleId,onSuccess)
        {
            var temp=this.url+"/UpdateUserRoleById";
	        var idsStr=JSON.stringify(ids);
	        var cRoleIdStr=cRoleId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            ids:idsStr,
                            cRoleId:cRoleIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询所有的维修员账号信息,无组织限制 
         */
        service.getMaintenanceManList=function(onSuccess)
        {
            var temp=this.url+"/getMaintenanceManList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取当前登录用户详细信息 
         */
        service.getUserDetailInfo=function(onSuccess)
        {
            var temp=this.url+"/getUserDetailInfo";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询维修员信息，用于组织树加载 
         *@param  组织编号 
         */
        service.getMaintenanceManByOrgId=function(cOrgCode,onSuccess)
        {
            var temp=this.url+"/getMaintenanceManByOrgId";
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据用户id更新用户登录限制 
         *@param  登录限制信息 注意其用户id字段不能为null 
         */
        service.updateLoginRestrictionByUserId=function(loginRestriction,onSuccess)
        {
            var temp=this.url+"/updateLoginRestriction";
	        var loginRestrictionStr=JSON.stringify(loginRestriction);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            loginRestriction:loginRestrictionStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据用户代码获取用户信息 
         *@param  用户代码 
         */
        service.getUserInfoByUserCode=function(cUserCode,onSuccess)
        {
            var temp=this.url+"/getUserInfoByCode";
	        var cUserCodeStr=cUserCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cUserCode:cUserCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *用户登录接口 
         *@param  登录用户名 
         *@param  登录密码 
         */
        service.login=function(loginName,password,onSuccess)
        {
            var temp=this.url+"/login";
	        var loginNameStr=loginName;
	        var passwordStr=password;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            loginName:loginNameStr,
                            password:passwordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *从Excel文件中导入用户数据 
         *@param  excel文件 
         *@param  组织编码 
         */
        service.importExcel=function(file,cOrgCode,onSuccess)
        {
            var temp=this.url+"/importExcel";
	        var fileStr=JSON.stringify(file);
	        var cOrgCodeStr=cOrgCode;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            file:fileStr,
                            cOrgCode:cOrgCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取用户excel导入模板数据项集合 
         */
        service.getExcelFieldList=function(onSuccess)
        {
            var temp=this.url+"/getExcelFieldList";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *下载用户信息导入模板 
         *@param  选中的可选Excel导入数据项id 
         */
        service.downloadExcelTemplate=function(ids,onSuccess)
        {
            var temp=this.url+"/downloadExcelTemplate";
	        var idsStr=JSON.stringify(ids);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            ids:idsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
function workOrderService(){
    var service=new Object();
    service.url=host+"/workOrderService";
        /**
        *上报工单 
         *@param no comment
         *@param no comment
         */
        service.reportWorkOrder=function(workOrder,alarmIds,onSuccess)
        {
            var temp=this.url+"/reportWorkOrder";
	        var workOrderStr=JSON.stringify(workOrder);
	        var alarmIdsStr=JSON.stringify(alarmIds);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            workOrder:workOrderStr,
                            alarmIds:alarmIdsStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按处理状态分页查询工单 
         *@param  分页查询对象 
         */
        service.getWorkOrderList=function(page,onSuccess)
        {
            var temp=this.url+"/getWorkOrderList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按处理状态分页查询工单 
         *@param  分页查询对象 
         *@param  工单处理状态 1表示按时完成的已下发工单，2表示未完成，超时完成的已下发工单 
         */
        service.getWorkOrderList=function(page,processStatus,onSuccess)
        {
            var temp=this.url+"/getWorkOrderListByProcessStatus";
	        var pageStr=JSON.stringify(page);
	        var processStatusStr=processStatus;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            processStatus:processStatusStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取工单信息 
         *@param  工单编码 
         */
        service.getWorkOrderInfo=function(cWdId,onSuccess)
        {
            var temp=this.url+"/getWorkOrderInfo";
	        var cWdIdStr=cWdId;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cWdId:cWdIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *申请延期工单 
         *@param  工单号 
         *@param  延期原因 
         *@param  延期完成时间 
         */
        service.delayWorkOrder=function(cWOCode,delayReason,delayTime,onSuccess)
        {
            var temp=this.url+"/applyDelayWorkOrder";
	        var cWOCodeStr=cWOCode;
	        var delayReasonStr=delayReason;
	        var delayTimeStr=delayTime;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cWOCode:cWOCodeStr,
                            delayReason:delayReasonStr,
                            delayTime:delayTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按地区工单统计 
         *@param no comment
         */
        service.getWorkOrderStat=function(page,onSuccess)
        {
            var temp=this.url+"/getWorkOrderStat";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据工单号查询工单日志 
         *@param  工单号 
         */
        service.getWorkOrderLog=function(cWdId,onSuccess)
        {
            var temp=this.url+"/getWorkOrderLog";
	        var cWdIdStr=cWdId;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cWdId:cWdIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *反馈工单审核 
         *@param no comment
         *@param  是否驳回 驳回传入1，否则传入0 
         */
        service.confirmFeedbackWorkOrder=function(workOrder,refuse,onSuccess)
        {
            var temp=this.url+"/confirmFeedbackWorkOrder";
	        var workOrderStr=JSON.stringify(workOrder);
	        var refuseStr=refuse;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            workOrder:workOrderStr,
                            refuse:refuseStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *反馈工单 
         *@param no comment
         */
        service.feedbackWorkOrder=function(workOrder,onSuccess)
        {
            var temp=this.url+"/feedbackWorkOrder";
	        var workOrderStr=JSON.stringify(workOrder);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            workOrder:workOrderStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *延期工单审核 
         *@param no comment
         *@param  是否驳回 驳回传入1，否则传入0 
         */
        service.confirmDelayedWorkOrder=function(workOrder,refuse,onSuccess)
        {
            var temp=this.url+"/confirmDelayedWorkOrder";
	        var workOrderStr=JSON.stringify(workOrder);
	        var refuseStr=refuse;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            workOrder:workOrderStr,
                            refuse:refuseStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *上报工单审核 
         *@param no comment
         *@param  是否驳回 驳回传入1，否则传入0 
         *@param  是否挂起 挂起传入1，否则传入0 
         */
        service.confirmReportedWorkOrder=function(workOrder,refuse,suspend,onSuccess)
        {
            var temp=this.url+"/confirmReportedWorkOrder";
	        var workOrderStr=JSON.stringify(workOrder);
	        var refuseStr=refuse;
	        var suspendStr=suspend;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            workOrder:workOrderStr,
                            refuse:refuseStr,
                            suspend:suspendStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询工单按时完成率统计 
         */
        service.getCompletedOnTimeComposition=function(onSuccess)
        {
            var temp=this.url+"/getCompletedOnTimeComposition";
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *多条件查询过期工单 
         *@param  查询对象 
         */
        service.getExpiredWorkOrderList=function(page,onSuccess)
        {
            var temp=this.url+"/getExpiredWorkOrderList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按员工工单统计 
         *@param no comment
         */
        service.getWorkOrderStatUser=function(page,onSuccess)
        {
            var temp=this.url+"/getWorkOrderStatUser";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按工单处理效率统计 
         *@param no comment
         */
        service.getWorkOrderStatEffi=function(page,onSuccess)
        {
            var temp=this.url+"/getWorkOrderStatEffi";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按工单处理效率统计 
         *@param no comment
         */
        service.getWorkOrderStatEffiExport=function(page,onSuccess)
        {
            var temp=this.url+"/getWorkOrderStatEffiExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *导出过期工单列表 
         *@param  查询对象 
         */
        service.exportExpiredWorkOrderList=function(page,onSuccess)
        {
            var temp=this.url+"/exportExpiredWorkOrderList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按地区工单统计导出 
         *@param no comment
         */
        service.getWorkOrderStatExport=function(page,onSuccess)
        {
            var temp=this.url+"/getWorkOrderStatExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *按员工工单统计 
         *@param no comment
         */
        service.getWorkOrderStatUserExport=function(page,onSuccess)
        {
            var temp=this.url+"/getWorkOrderStatUserExport";
	        var pageStr=page;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询当前用户订阅的工单 
         *@param  查询条件对象 
         */
        service.getSubscribedWorkOrderList=function(page,onSuccess)
        {
            var temp=this.url+"/getSubscribedWorkOrderList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *导出工单列表 
         *@param  查询条件 
         */
        service.exportWorkOrderList=function(page,onSuccess)
        {
            var temp=this.url+"/exportWorkOrderList";
	        var pageStr=JSON.stringify(page);
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
    return service;
}
