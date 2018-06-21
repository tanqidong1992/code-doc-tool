function alarmService(){
    var service=new Object();
    service.url=host+"/alarmService";
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
        *查询当前用户订阅的告警列表 
         *@param page  分页查询对象 
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
         *@param resCode  告警源设备编码 
         *@param page no comment
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
        *根据工单编号，查询对应告警信息（同一异常源的告警只保留一条） 
         *@param cWdId no comment
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
        *获取day日内告警数量统计,默认7天内的数据 
         *@param day  天之内的，包括今天 
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
        /**
        *按告警处理状态，分页查询告警信息 
         *@param page  分页查询对象 
         *@param alarmProcessStatus  1，查询未处理，2查询超时未处理，3 按时已处理，4 超时已处理 
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
         *@param page  查询条件 
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
         *@param page  查询对象 
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
         *@param cAlarmId  告警Id 
         *@param processWay  处理方式 
         *@param processComment  处理描述 
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
    return service;
}
function alarmRuleService(){
    var service=new Object();
    service.url=host+"/alarmRuleService";
        /**
        *获取指定资源类型的告警规则配置项 
         *@param resTypeCode  资源类型编码 
         *@param oidType  告警配置项类型（1 表示状态，2表示性能）,可选查询参数 
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
        *获取指定资源类型的告警规则 
         *@param resTypeCode  资源类型编码 
         *@param condType  告警指标类型（1 表示状态，2表示性能）,可选查询参数 
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
        *根据告警规则Id启用告警规则 
         *@param ids  告警规则Id集合 
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
         *@param resCode  资源编码 
         *@param condType  告警指标类型（1 表示状态，2表示性能）,可选查询参数 
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
        /**
        *根据告警规则Id，修改告警规则 
         *@param alarmRule  告警规则对象，其Id字段不能为null 
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
         *@param ids  告警规则Id集合 
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
         *@param ids  告警规则Id集合 
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
        *添加告警规则，如果是为指定类型资源配置告警规则 请给 cResType赋值，为指定资源配置告警规则 请给cResCode赋值 
         *@param alarmRule  告警规则对象 
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
    return service;
}
function asset(){
    var service=new Object();
    service.url=host+"/asset";
        /**
        *修改资产厂商信息 
         *@param cVendorId  资产厂商Id 
         *@param cVendorName  资产厂商名称,可选参数 
         *@param cDesc  资产厂商描述,可选参数 
         */
        service.updateAssetVendor=function(cVendorId,cVendorName,cDesc,onSuccess)
        {
            var temp=this.url+"/vendor/update";
	        var cVendorIdStr=cVendorId;
	        var cVendorNameStr=cVendorName;
	        var cDescStr=cDesc;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cVendorId:cVendorIdStr,
                            cVendorName:cVendorNameStr,
                            cDesc:cDescStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除资产厂商信息 
         *@param cVendorId  资产厂商Id 
         */
        service.deleteAssetVendorById=function(cVendorId,onSuccess)
        {
            var temp=this.url+"/vendor/delete";
	        var cVendorIdStr=cVendorId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cVendorId:cVendorIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *统计资产出入库记录 
         *@param cTypeId  资产类型Id,可选查询参数之一 
         *@param cVendorId  资产厂商Id,可选查询参数之一 
         *@param startTime  开始时间,可选查询参数之一 
         *@param endTime  结束时间,可选查询参数之一 
         */
        service.getAssetFlowRecordStatistic=function(cTypeId,cVendorId,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/flow/record/statistic";
	        var cTypeIdStr=cTypeId;
	        var cVendorIdStr=cVendorId;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cTypeId:cTypeIdStr,
                            cVendorId:cVendorIdStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加资产出入库记录 
         *@param cTypeId  资产类型Id 
         *@param cVendorId  资产厂商Id 
         *@param iFlowType  出入库类型 1表示入库,2表示出库 
         *@param nFlowCount  资产数量 
         *@param cComment  描述信息 
         *@param tCreateTime  出入库时间 
         */
        service.addAssetFlowRecord=function(cTypeId,cVendorId,iFlowType,nFlowCount,cComment,tCreateTime,onSuccess)
        {
            var temp=this.url+"/flow/record/add";
	        var cTypeIdStr=cTypeId;
	        var cVendorIdStr=cVendorId;
	        var iFlowTypeStr=iFlowType;
	        var nFlowCountStr=nFlowCount;
	        var cCommentStr=cComment;
	        var tCreateTimeStr=tCreateTime;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cTypeId:cTypeIdStr,
                            cVendorId:cVendorIdStr,
                            iFlowType:iFlowTypeStr,
                            nFlowCount:nFlowCountStr,
                            cComment:cCommentStr,
                            tCreateTime:tCreateTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *加载资产厂商列表 
         */
        service.getAssetVendorList=function(onSuccess)
        {
            var temp=this.url+"/vendor/list";
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
        *分页查询资产出入库记录 
         *@param page  分页查询参数 
         *@param cTypeId  资产类型Id,可选查询参数之一 
         *@param cVendorId  资产厂商Id,可选查询参数之一 
         *@param startTime  开始时间,可选查询参数之一 
         *@param endTime  结束时间,可选查询参数之一 
         */
        service.getAssetFlowRecordList=function(page,cTypeId,cVendorId,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/flow/record/list";
	        var pageStr=JSON.stringify(page);
	        var cTypeIdStr=cTypeId;
	        var cVendorIdStr=cVendorId;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            cTypeId:cTypeIdStr,
                            cVendorId:cVendorIdStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除资产出入库记录 
         *@param cRecorldId  资产出入库记录Id 
         */
        service.deleteAssetFlowRecordById=function(cRecorldId,onSuccess)
        {
            var temp=this.url+"/flow/record/delete";
	        var cRecorldIdStr=cRecorldId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRecorldId:cRecorldIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改资产出入库信息 
         *@param cRecorldId  资产出入库记录Id 
         *@param flowRecord  待修改的资产对象,json字符串 
         */
        service.updateAssetFlowRecord=function(cRecorldId,flowRecord,onSuccess)
        {
            var temp=this.url+"/flow/record/update";
	        var cRecorldIdStr=cRecorldId;
	        var flowRecordStr=JSON.stringify(flowRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRecorldId:cRecorldIdStr,
                            flowRecord:flowRecordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *根据资产类型,厂商类型统计资产出入库记录 
         *@param cTypeId  资产类型Id,可选查询参数之一 
         *@param cVendorId  资产厂商Id,可选查询参数之一 
         *@param startTime  开始时间,可选查询参数之一 
         *@param endTime  结束时间,可选查询参数之一 
         */
        service.getAssetFlowRecordStatistic1=function(cTypeId,cVendorId,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/flow/record/statistic1";
	        var cTypeIdStr=cTypeId;
	        var cVendorIdStr=cVendorId;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cTypeId:cTypeIdStr,
                            cVendorId:cVendorIdStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除资产类型信息 
         *@param cTypeId  资产类型Id 
         */
        service.deleteAssetTypeById=function(cTypeId,onSuccess)
        {
            var temp=this.url+"/type/delete";
	        var cTypeIdStr=cTypeId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cTypeId:cTypeIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *新增资产类型 
         *@param cTypeName  资产类型名称 
         *@param cDesc  资产类型描述 
         */
        service.addAssetType=function(cTypeName,cDesc,onSuccess)
        {
            var temp=this.url+"/type/add";
	        var cTypeNameStr=cTypeName;
	        var cDescStr=cDesc;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cTypeName:cTypeNameStr,
                            cDesc:cDescStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改资产类型信息 
         *@param cTypeId  资产类型Id 
         *@param cTypeName  新的资产类型名称,可选参数 
         *@param cDesc  资产描述,可选参数 
         */
        service.updateAssetType=function(cTypeId,cTypeName,cDesc,onSuccess)
        {
            var temp=this.url+"/type/update";
	        var cTypeIdStr=cTypeId;
	        var cTypeNameStr=cTypeName;
	        var cDescStr=cDesc;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cTypeId:cTypeIdStr,
                            cTypeName:cTypeNameStr,
                            cDesc:cDescStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加资产厂商信息 
         *@param cVendorName  资产厂商名称 
         *@param cDesc  资产厂商描述,可选 
         */
        service.addAssetVendor=function(cVendorName,cDesc,onSuccess)
        {
            var temp=this.url+"/vendor/add";
	        var cVendorNameStr=cVendorName;
	        var cDescStr=cDesc;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cVendorName:cVendorNameStr,
                            cDesc:cDescStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *加载资产类型列表 
         */
        service.getAssetTypeList=function(onSuccess)
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
    return service;
}
function camera(){
    var service=new Object();
    service.url=host+"/camera";
        /**
        *修改摄像机信息 
         *@param cResCode  摄像机编码 
         *@param cameraInfo  摄像机对象json字符串 
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
         *@param cResCode  摄像机编码 
         */
        service.deleteCameraInfoByCode=function(cResCode,onSuccess)
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
        *查询摄像机列表 
         *@param page  分页查询对象 
         *@param cResName  摄像机名称,可选查询条件之一 
         *@param cTypeCodes  摄像机类型集合,可选查询条件之一 
         *@param cIpAddress  摄像机IP,可选查询条件之一 
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
        /**
        *添加摄像机 
         *@param cResName  摄像机名称 
         *@param cOrgCode  组织代码 
         *@param cTypeCode  摄像机类型 
         *@param cEleCode  电表户号 
         *@param nElePrice  电费基数,其实就是电的单价 
         *@param cIpAddress  摄像机IP地址 
         *@param cLoginName  设备登录名称 
         *@param cLoginPassword  设备登录密码 
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
    return service;
}
function DeviceStateController(){
    var service=new Object();
    service.url=host+"/device/state";
        /**
        *查询设备在线时长统计数据列表  
         *@param page page 查询分页对象 
         *@param resName resName 设备名称，可选查询条件之一 
         *@param ipAddress ipAddress IP地址，可选查询条件之一 
         *@param startTime startTime 起始时间 
         *@param endTime endTime 结束时间 
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
        *按区域统计设备在线时长  
         *@param page page 查询分页对象 
         *@param startTime startTime 起始时间 
         *@param endTime endTime 结束时间 
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
        *按设备状态查询设备信息列表  
         *@param status status 设备状态，1 在线，2离线， 
         *@param page page 分页查询参数 
         *@param resTypeCodes resTypeCodes 设备类型集合,可选查询条件 
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
        *查询设备在线历史状态  
         *@param resCode resCode 设备编码 
         *@param startTime startTime 起始时间 
         *@param endTime endTime 结束时间 
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
        *导出设备状态列表  
         *@param page page 分页查询参数 
         *@param resTypeCodes resTypeCodes 设备具体类型，可选查询条件之一 
         *@param resName resName 设备名称，可选查询条件之一 
         *@param ipAddress ipAddress 设备Ip地址，可选查询条件之一 
         *@param status status 设备在线状态，2 表示表示查询离线，1表示查询在线，不传表示查询所有， 可选查询条件之一 
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
         *@param page page 查询参数 
         *@param resTypeCodes resTypeCodes 设备类型，可选查询参数 
         *@param startTime startTime 统计开始时间 
         *@param endTime endTime 统计结束时间 
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
        *导出基于区域统计设备在线时长列表  
         *@param page page 查询分页对象 
         *@param startTime startTime 起始时间 
         *@param endTime endTime 结束时间 
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
        *根据设备类型统计设备在线时长  
         *@param page page 查询参数 
         *@param resTypeCodes resTypeCodes 设备类型，可选查询参数 
         *@param startTime startTime 统计开始时间 
         *@param endTime endTime 统计结束时间 
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
        *查询设备状态  
         *@param page page 分页查询参数 
         *@param resTypeCodes resTypeCodes 设备具体类型，可选查询条件之一 
         *@param resName resName 设备名称，可选查询条件之一 
         *@param ipAddress ipAddress 设备Ip地址，可选查询条件之一 
         *@param status status 设备在线状态，2 表示表示查询离线，1表示查询在线，不传表示查询所有， 可选查询条件之一 
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
        *导出设备在线时长统计数据列表  
         *@param page page 查询分页对象 
         *@param resName resName 设备名称，可选查询条件之一 
         *@param ipAddress ipAddress IP地址，可选查询条件之一 
         *@param startTime startTime 起始时间 
         *@param endTime endTime 结束时间 
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
        *获取设备在线统计数据  
         *@param resTypeCodes resTypeCodes 设备类型集合 
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
         *@param planName planName 计划名称 
         *@param inspectorCode inspectorCode 探针代码 
         *@param checkInterval checkInterval 巡检间隔 
         *@param targetType targetType 目标类型 1表示指定资源，2表示指定类型 
         *@param targetCodes targetCodes 目标代码，资源代码或者类型代码 
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
        *加载检测资源  
         *@param resTypes resTypes 资源类型集合 
         *@param page page 查询对象 
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
        *删除巡检任务  
         *@param planId planId 任务Id 
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
        *启用巡检任务  
         *@param planId planId 任务Id 
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
        *查询巡检任务列表  
         *@param page page 查询分页参数 
         *@param planName planName 任务名称，可选查询条件之一 
         *@param planTime planTime 任务时间配置，可选查询条件之一 
         *@param inspectorCode inspectorCode 探针代码，可选查询条件之一 
         *@param targetType targetType 目标类型 1表示指定资源，2表示指定类型，可选查询条件之一 
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
        *修改状态巡检计划  
         *@param planId planId 计划Id 
         *@param planName planName 计划名称 
         *@param inspectorCode inspectorCode 探针代码 
         *@param checkInterval checkInterval 巡检间隔 
         *@param targetType targetType 目标类型 1表示指定资源，2表示指定类型 
         *@param targetCodes targetCodes 目标代码，资源代码或者类型代码 
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
        *禁用巡检任务  
         *@param planId planId 任务Id 
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
        *加载指定巡检任务的检测资源  
         *@param planId planId 巡检任务Id 
         *@param resTypes resTypes 资源类型集合 
         *@param page page 查询对象 
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
        /**
        *加载检测资源类型  
         *@param categoryCodes categoryCodes 资源类型种类集合 1表示编码器，2表示摄像机，3表示服务 
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
        *加载指定巡检任务的检测资源类型  
         *@param planId planId 巡检任务Id 
         *@param categoryCodes categoryCodes 资源类型种类集合 1表示编码器，2表示摄像机，3表示服务 
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
    return service;
}
function dictService(){
    var service=new Object();
    service.url=host+"/dictService";
        /**
        *从excel中导入字典信息  
         *@param file file excel文件对象 
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
        *添加字典  
         *@param detail detail 待添加的字典对象 
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
        *根据字典代码修改字典信息  
         *@param dictDetail no comment
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
        *多条件查询字典信息  
         *@param page page 分页查询对象 
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
        *根据字典代码，删除字典信息  
         *@param codes codes 
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
        *下载字典导入模板  
         *@param ids ids 选择的可选项Id 
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
        *根据字典父代码加载字典数据 
         *@param parentCode  父代码 
         */
        service.getDictByParentCode=function(parentCode,onSuccess)
        {
            var temp=this.url+"/getDictByParentCode";
	        var parentCodeStr=parentCode;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            parentCode:parentCodeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *获取资源类型的在特定工作场景下支持的操作  
         *@param resTypeCode resTypeCode 资源类型，可选查询条件 
         *@param module module 模块名称 
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
    return service;
}
function doc(){
    var service=new Object();
    service.url=host+"/doc";
        /**
        *上传文件 
         *@param cDesc  文件描述 
         *@param file  文件对象,文件大小限制在200M以下(包括200M) 
         *@param cType  文件类型,此处用名称 
         */
        service.createDoc=function(cDesc,file,cType,onSuccess)
        {
            var temp=this.url+"/upload";
	        var cDescStr=cDesc;
	        var fileStr=JSON.stringify(file);
	        var cTypeStr=cType;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cDesc:cDescStr,
                            file:fileStr,
                            cType:cTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询文件列表 
         *@param page  分页查询对象 
         *@param documentName  文档名称,可选查询条件之一 
         *@param cType  文档类型,可选查询条件之一 
         */
        service.getDocmentList=function(page,documentName,cType,onSuccess)
        {
            var temp=this.url+"/list";
	        var pageStr=JSON.stringify(page);
	        var documentNameStr=documentName;
	        var cTypeStr=cType;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            documentName:documentNameStr,
                            cType:cTypeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *批量删除文件 
         *@param ids  待删除多个文件的id集合 
         */
        service.deleteDoc=function(ids,onSuccess)
        {
            var temp=this.url+"/delete";
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
        *下载文件 
         *@param id  待下载文件的id 
         */
        service.downloadDoc=function(id,onSuccess)
        {
            var temp=this.url+"/download";
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
        *获取文档类型列表 
         */
        service.getDocmentTypeList=function(onSuccess)
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
    return service;
}
function eleService(){
    var service=new Object();
    service.url=host+"/eleService";
        /**
        *添加电费缴费记录 
         *@param elecBill no comment
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
        *删除电费缴费账单 
         *@param id  待删除账单的Id 
         */
        service.deleteEleBill=function(id,onSuccess)
        {
            var temp=this.url+"/deleteEleBill";
	        var idStr=id;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            id:idStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改电费缴费单 
         *@param id  待修改缴费账单的Id 
         *@param elecBill no comment
         */
        service.updateEleBill=function(id,elecBill,onSuccess)
        {
            var temp=this.url+"/updateEleBill";
	        var idStr=id;
	        var elecBillStr=JSON.stringify(elecBill);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            id:idStr,
                            elecBill:elecBillStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询电费列表 
         *@param page  分页查询对象 
         *@param cInvoiceStatus  发票状态,可选查询条件之一 
         *@param cMeterNumber   户号,可选查询条件之一 
         *@param startTime  开始时间,可选查询条件之一 
         *@param endTime  结束时间,可选查询条件之一 
         */
        service.getElectrityBillList=function(page,cInvoiceStatus,cMeterNumber,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/getElectrityBillList";
	        var pageStr=JSON.stringify(page);
	        var cInvoiceStatusStr=cInvoiceStatus;
	        var cMeterNumberStr=cMeterNumber;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            cInvoiceStatus:cInvoiceStatusStr,
                            cMeterNumber:cMeterNumberStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
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
         *@param cResultId cResultId 诊断结果Id 
         *@param file file 文件对象 
         *@param index index 序号，可以为null 
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
        *vqd诊断图片下载接口  
         *@param cResultId cResultId 诊断结果Id 
         *@param index index 序号，可以为null 
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
         *@param cWdId cWdId 工单编号 
         *@param file file 文件 
         *@param index index 
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
        *获取工单反馈图片列表  
         *@param cWdId cWdId 工单编号 
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
        *下载工单反馈图片  
         *@param cWdId cWdId 工单编号 
         *@param fileName fileName 文件名 
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
    return service;
}
function linkageService(){
    var service=new Object();
    service.url=host+"/linkageService";
        /**
        *创建告警联动,注意 timeSchedules 与 planDetails 两个集合大小一致，并一一对应  
         *@param linkagePlan linkagePlan 联动基本信息对象 
         *@param timeSchedules timeSchedules 联动时间对象集合 
         *@param planDetails planDetails 联动参数配置对象集合 
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
        *根据组织加载用户工单联动配置信息  
         *@param cOrgCode cOrgCode 组织代码 
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
        *多条件，分页查询告警联动列表  
         *@param page page 查询对象 
         *@param loginName loginName 用户名称 
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
        *根据组织加载用户告警联动配置信息  
         *@param cOrgCode cOrgCode 组织代码 
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
         *@param linkagePlan linkagePlan 联动基本信息对象 
         *@param timeSchedules timeSchedules 联动时间对象集合 
         *@param planDetails planDetails 联动参数配置对象集合 
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
        /**
        *多条件，分页查询工单联动列表  
         *@param page page 查询对象 
         *@param loginName loginName 用户名称 
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
        *根据联动配置代码获取联动配置详情  
         *@param cLinkageCode cLinkageCode 联动配置编码 
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
        *根据联动编码修改联动配置,注意 timeSchedules 与 planDetails 两个集合大小一致，并一一对应  
         *@param linkagePlan linkagePlan 联动基本信息对象，编码字段不能为空 
         *@param timeSchedules timeSchedules 联动时间对象集合 编码字段不能为空 
         *@param planDetails planDetails 联动参数配置对象集合 编码字段不能为空 
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
        *根据联动编号删除联动配置  
         *@param codes codes 联动编号集合 
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
    return service;
}
function logService(){
    var service=new Object();
    service.url=host+"/logService";
        /**
        *查询操作日志 
         *@param page  查询条件对象 
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
        *查询操作列表 
         *@param cModule no comment
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
        *导出操作日志 
         *@param page  查询条件对象 
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
        *获取子菜单 
         *@param cParentId  父菜单Id 
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
        *加载指定权限的具体描述,即页面权限描述 
         *@param cPermissionId  权限ID 
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
        /**
        *配置指定权限的具体描述,即页面权限描述 
         *@param cRoleId  角色ID 
         *@param cPermissionId  权限Id 
         *@param cDesc  权限描述字符串     11111 表示 [‘增’、’改’，‘删’、‘上传’、‘下载’] 
         */
        service.setPagePermission=function(cRoleId,cPermissionId,cDesc,onSuccess)
        {
            var temp=this.url+"/setPagePermission";
	        var cRoleIdStr=cRoleId;
	        var cPermissionIdStr=cPermissionId;
	        var cDescStr=cDesc;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cRoleId:cRoleIdStr,
                            cPermissionId:cPermissionIdStr,
                            cDesc:cDescStr,
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
         *@param resInfo resInfo 网络设备基本信息 
         *@param deviceTypeCode deviceTypeCode 网络设备类型代码 例如666 
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
        *根据IP地址段批量添加设备,如要录入[192.168.0.1]-[192.168.0.157]之间的设备,ipPrefix为[192.168.0.],ipStart为[1],ipEnd为[157] 
         *@param resInfo  设备基础信息 
         *@param deviceTypeCode  设备类型代码 
         *@param ipPrefix  IP前缀,例如IP地址[192.168.0.157],该Ip地址对应前缀为[192.168.0.] 
         *@param ipStart  ip地址起始位置,包含ipStart 
         *@param ipEnd  ip地址结束为止,包含ipEnd 
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
         *@param deviceCode deviceCode 网络设备代码 
         *@param resInfo resInfo 网络设备信息 
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
         *@param codes codes 网络设备代码集合 
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
         *@param page page 分页查询参数 
         *@param resTypeCodes resTypeCodes 网络设备类型集合，可选查询条件之一 
         *@param resName resName 设备名称，可选查询条件之一 
         *@param ipAddress ipAddress 设备IP，可选查询条件之一 
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
         *@param cOrgCode cOrgCode 组织编码 
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
    return service;
}
function orgService(){
    var service=new Object();
    service.url=host+"/orgService";
        /**
        *根据组织代码,查询该组织的直接子组织节点 
         *@param cOrgCode  组织代码,访问根节点请传入-1 
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
        *将指定组织cOrgCode下的组织信息，导出到Excel 
         *@param cOrgCode  组织代码 
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
         *@param cOrgCode  查询对象code 
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
        *分页 多条件 排序查询组织信息 
         *@param page  查询分装对象 
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
        *获取当前行政地区的子行政地区 
         *@param cParentCode  当前行政地区编码，根行政地区的父编码为000000， 
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
        *根据组织代码删除组织,包括其所有子节点,其以及其所有子节点所含有的资源(服务,用户,设备等一切资源) 
         *@param cOrgCode  组织代码 
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
        *从Excel中导入组织信息 
         *@param file  Excel文件对象 
         *@param cAreaCode  地区代码 
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
        *判断组织名称orgName是否已存在 
         *@param orgName  组织名称 
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
        *根据组织编码更新组织信息 
         *@param org  组织对象,注意编码字段自动不能为空 
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
        *新增组织 
         *@param org  组织对象 
         *@param areaCode  地区代码 
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
         *@param ids  选中的数据导入项id集合 
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
function performance(){
    var service=new Object();
    service.url=host+"/performance";
        /**
        *添加协商记录信息 
         *@param negotiationRecord  待添加协商记录的信息 
         */
        service.addNegotiationRecord=function(negotiationRecord,onSuccess)
        {
            var temp=this.url+"/negotiation/record/add";
	        var negotiationRecordStr=JSON.stringify(negotiationRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            negotiationRecord:negotiationRecordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询并统计图片上传记录 
         *@param page  分页查询对象 
         *@param cUserId  用户Id(员工Id),可选查询参数之一 
         *@param startTime  开始时间,可选查询参数之一 
         *@param endTime  结束时间,可选查询参数之一 
         */
        service.getUploadPictureRecordList=function(page,cUserId,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/upload/picture/record/list";
	        var pageStr=JSON.stringify(page);
	        var cUserIdStr=cUserId;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            cUserId:cUserIdStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改图片上传记录信息 
         *@param cId  待修改图片上传记录的Id 
         *@param pictureRecord no comment
         */
        service.updateUploadPictureRecordById=function(cId,pictureRecord,onSuccess)
        {
            var temp=this.url+"/upload/picture/record/update";
	        var cIdStr=cId;
	        var pictureRecordStr=JSON.stringify(pictureRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                            pictureRecord:pictureRecordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除图片上传记录信息 
         *@param cId  待删除图片上传记录的Id 
         */
        service.deleteUploadPictureRecordById=function(cId,onSuccess)
        {
            var temp=this.url+"/upload/picture/record/delete";
	        var cIdStr=cId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加违章上传记录 
         *@param violationRecord  待添加违章上传记录的信息 
         */
        service.addUploadViolationRecord=function(violationRecord,onSuccess)
        {
            var temp=this.url+"/upload/violation/record/add";
	        var violationRecordStr=JSON.stringify(violationRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            violationRecord:violationRecordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询,统计违章上传记录列表 
         *@param page  分页查询对象 
         *@param cUserId  用户Id(员工Id),可选查询参数之一 
         *@param startTime  开始时间,可选查询参数之一 
         *@param endTime  结束时间,可选查询参数之一 
         */
        service.getUploadViolationRecordList=function(page,cUserId,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/upload/violation/record/list";
	        var pageStr=JSON.stringify(page);
	        var cUserIdStr=cUserId;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            cUserId:cUserIdStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除违章上传记录 
         *@param cId  待删除违章上传记录的Id 
         */
        service.deleteUploadViolationRecordById=function(cId,onSuccess)
        {
            var temp=this.url+"/upload/violation/record/delete";
	        var cIdStr=cId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询,统计协商记录信息列表 
         *@param page  分页查询参数 
         *@param cUserId  用户Id(员工Id),可选查询参数之一 
         *@param startTime  开始时间,可选查询参数之一 
         *@param endTime  结束时间,可选查询参数之一 
         *@param feedback  事件状态（0表示未反馈,1表示已反馈）,可选查询参数之一 
         */
        service.getNegotiationRecordList=function(page,cUserId,startTime,endTime,feedback,onSuccess)
        {
            var temp=this.url+"/negotiation/record/list";
	        var pageStr=JSON.stringify(page);
	        var cUserIdStr=cUserId;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
	        var feedbackStr=feedback;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            cUserId:cUserIdStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                            feedback:feedbackStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改违章上传记录信息 
         *@param cId  待修改违章上传记录的Id 
         *@param violationRecord  待修改违章上传记录的信息 
         */
        service.updateUploadViolationRecordById=function(cId,violationRecord,onSuccess)
        {
            var temp=this.url+"/upload/violation/record/update";
	        var cIdStr=cId;
	        var violationRecordStr=JSON.stringify(violationRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                            violationRecord:violationRecordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改协商记录信息 
         *@param cId  待修改协商记录信息的Id 
         *@param negotiationRecord  待修改协商记录的信息 
         */
        service.updateNegotiationRecordById=function(cId,negotiationRecord,onSuccess)
        {
            var temp=this.url+"/negotiation/record/update";
	        var cIdStr=cId;
	        var negotiationRecordStr=JSON.stringify(negotiationRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                            negotiationRecord:negotiationRecordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除协商信息 
         *@param cId  待删除协商记录信息的Id 
         */
        service.deleteNegotiationRecordById=function(cId,onSuccess)
        {
            var temp=this.url+"/negotiation/record/delete";
	        var cIdStr=cId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加图片上传记录 
         *@param pictureRecord  待添加图片上传记录的信息 
         */
        service.addUploadPictureRecord=function(pictureRecord,onSuccess)
        {
            var temp=this.url+"/upload/picture/record/add";
	        var pictureRecordStr=JSON.stringify(pictureRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            pictureRecord:pictureRecordStr,
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
function repair(){
    var service=new Object();
    service.url=host+"/repair";
        /**
        *添加维修小队 
         *@param team  维修小队信息对象,全字段 
         */
        service.addRepairTeam=function(team,onSuccess)
        {
            var temp=this.url+"/team/add";
	        var teamStr=JSON.stringify(team);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            team:teamStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加维修记录 
         *@param record  维修记录对象 
         */
        service.addRepairRecord=function(record,onSuccess)
        {
            var temp=this.url+"/record/add";
	        var recordStr=JSON.stringify(record);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            record:recordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改维修小队信息 
         *@param cId  维修小队Id 
         *@param team   待修改的维修小队信息 
         */
        service.updateRepairTeamById=function(cId,team,onSuccess)
        {
            var temp=this.url+"/team/update";
	        var cIdStr=cId;
	        var teamStr=JSON.stringify(team);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                            team:teamStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除维修记录信息 
         *@param cId  维修记录Id 
         */
        service.deleteRepairRecordById=function(cId,onSuccess)
        {
            var temp=this.url+"/record/delete";
	        var cIdStr=cId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *分页查询维修记录列表 
         *@param page  分页查询对象 
         *@param startTime  开始时间,可选查询参数之一 
         *@param endTime  结束时间,可选查询参数之一 
         *@param cTeamId  维修小队,可选查询参数之一 
         *@param cRepairReason  维修原由,可选查询参数之一 
         */
        service.getRepairRecordList=function(page,startTime,endTime,cTeamId,cRepairReason,onSuccess)
        {
            var temp=this.url+"/record/list";
	        var pageStr=JSON.stringify(page);
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
	        var cTeamIdStr=cTeamId;
	        var cRepairReasonStr=cRepairReason;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                            cTeamId:cTeamIdStr,
                            cRepairReason:cRepairReasonStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除维修小队信息 
         *@param cId  维修小队ID 
         */
        service.deleteRepairTeamById=function(cId,onSuccess)
        {
            var temp=this.url+"/team/delete";
	        var cIdStr=cId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询维修小队列表 
         */
        service.getRepairTeamList=function(onSuccess)
        {
            var temp=this.url+"/team/list";
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
        *修改维修记录信息 
         *@param cId  维修记录Id 
         *@param record  待修改的维修记录信息 
         */
        service.updateRepairRecordById=function(cId,record,onSuccess)
        {
            var temp=this.url+"/record/update";
	        var cIdStr=cId;
	        var recordStr=JSON.stringify(record);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                            record:recordStr,
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
        *剥夺角色的资源 
         *@param cRoleId  角色id 
         *@param cResCodes  资源代码集合 
         *@param iResourceType  资源类型 
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
        *根据角色id删除角色 
         *@param ids  角色id集合 
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
        *授予角色权限 
         *@param cRoleId  角色id 
         *@param permissions no comment
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
        *重新授予角色权限 
         *@param cRoleId  角色id 
         *@param permissions no comment
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
        *重新给角色分配资源 
         *@param cRoleId  角色id 
         *@param cResCodes  资源编码集合 
         *@param iResourceType  资源类型 
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
        *分页,多条件,排序查询 角色信息 
         *@param page no comment
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
        *根据角色id批量禁用角色 
         *@param cRoleIds  角色id集合 
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
        *根据角色id,修改角色信息 
         *@param role  角色对象,注意其cRoleId不能为null 
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
        *添加角色 
         *@param role  角色对象 
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
        *给角色分配资源 
         *@param cRoleId  角色id 
         *@param cResCodes  资源代码集合json数据格式 
         *@param iResourceType  资源类型 
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
        *判断是否存在角色名称 
         *@param roleName  角色名称 
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
        *多条件 导出角色信息 
         *@param page  分页查询对象 
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
         *@param cRoleIds  角色id集合 
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
         *@param cRoleId  角色id 
         *@param iResourceType  资源类型 
         *@param page no comment
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
         *@param file  excel文件对象 
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
        *获取角色权限信息 
         *@param cRoleId  角色Id 
         *@param cParentCode  权限父类代码 
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
        *获取角色基本权限信息 
         *@param cRoleId  角色id 
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
        *剥夺角色权限 
         *@param cRoleId  角色id 
         *@param permissions no comment
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
         *@param ids  选中的excel数据导入项id集合 
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
         *@param resInfo  服务基本信息 
         *@param serviceTypeCode  服务类型编码 
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
        *多条件 分页 排序查询服务模块 
         *@param page  分页对象 
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
        *从Excel中导入服务信息到指定组织下 
         *@param file  Excel文件对象 
         *@param cOrgCode  组织代码 
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
        *查询指定组织下的服务列表 
         *@param cOrgCode  组织编码 
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
        *多条件 分页 排序查询导出服务模块 
         *@param page  分页对象 
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
        *根据服务代码更新服务模块的信息，注意编码字段不能为空 
         *@param resInfo  服务基本信息 
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
        *分页查询离线服务模块 
         *@param page  分页对象 
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
        *根据服务code删除服务模块 
         *@param codes  服务code集合 
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
         *@param page  分页对象 
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
         *@param ids  选中的Excel数据导入项 
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
        *删除服务模块配置信息  
         *@param cServerCode cServerCode 服务模块编码，参数可选，默认本服务模块 
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
        /**
        *设置系统变量  
         *@param cServerCode cServerCode 服务编码，如果为空，默认为本服务模块 
         *@param name name 变量名称 
         *@param value value 变量内容 
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
         *@param cServerCode cServerCode 服务模块编码，参数可选，默认本服务模块 
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
         *@param cServerCode cServerCode 服务编码，如果为空，默认为本服务模块 
         *@param name name 变量名称 
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
         *@param cServerCode cServerCode 服务编码，参数可选，默认为本服务模块 
         *@param file file 系统logo文件，参数可选 
         *@param pictureStorageThreshold pictureStorageThreshold 图片存储上限单位GB，参数可选 
         *@param cSystemName cSystemName 系统名，参数可选 
         *@param operateLogReserveDay operateLogReserveDay 操作日志保存天数，参数可选 
         *@param systemLogReserveDay systemLogReserveDay 系统日志保存天数，参数可选 
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
        *根据用户id更新用户信息 
         *@param user  用户信息 
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
        *添加用户接口 
         *@param user  用户对象 
         *@param expire  用户账号过期时间 long 类型整数 
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
        *多条件 分页 排序 查询导出用户信息到excel 
         *@param page  查询对象 
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
        *判断是否已存在用户名 
         *@param loginName  用户名 
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
        *多条件 分页 排序 查询用户信息 
         *@param page  查询对象 
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
        *根据用户的id批量删除用户 
         *@param ids  用户id集合 
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
        *根据用户Id批量启用用户账号 
         *@param ids  用户 id集合 
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
        *根据用户Id批量禁用用户账号 
         *@param ids  用户Id集合 
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
        *修改当前用户密码 
         *@param oldPassword  旧密码 
         *@param newPassword  新密码 
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
        *从Excel文件中导入用户数据 
         *@param file  excel文件 
         *@param cOrgCode  组织编码 
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
        *用户登录接口 
         *@param loginName  登录用户名 
         *@param password  登录密码 
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
        *查询维修员信息，用于组织树加载 
         *@param cOrgCode  组织编号 
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
        *根据用户代码获取用户信息 
         *@param cUserCode  用户代码 
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
        *根据用户id更新用户登录限制 
         *@param loginRestriction  登录限制信息 注意其用户id字段不能为null 
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
        *根据用户Id批量修改用户所属角色 
         *@param ids  用户id集合 
         *@param cRoleId  角色id 
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
         *@param ids  选中的可选Excel导入数据项id 
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
function vehicle(){
    var service=new Object();
    service.url=host+"/vehicle";
        /**
        *添加车辆配置信息 
         *@param vehicleInfo  待添加的车辆配置信息 
         */
        service.addVehicleInfo=function(vehicleInfo,onSuccess)
        {
            var temp=this.url+"/info/add";
	        var vehicleInfoStr=JSON.stringify(vehicleInfo);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            vehicleInfo:vehicleInfoStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *添加车辆行程记录 
         *@param travelRecord  待添加的车辆行程记录信息 
         */
        service.addTravelRecord=function(travelRecord,onSuccess)
        {
            var temp=this.url+"/travel/record/add";
	        var travelRecordStr=JSON.stringify(travelRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            travelRecord:travelRecordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改车辆配置信息 
         *@param cId  待修改车辆配置信息的Id 
         *@param vehicleInfo  待修改车辆配置信息的内容 
         */
        service.updateVehicleInfoById=function(cId,vehicleInfo,onSuccess)
        {
            var temp=this.url+"/info/update";
	        var cIdStr=cId;
	        var vehicleInfoStr=JSON.stringify(vehicleInfo);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                            vehicleInfo:vehicleInfoStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除车辆配置信息 
         *@param cId  待删除车辆配置信息的Id 
         */
        service.deleteVehicleInfoById=function(cId,onSuccess)
        {
            var temp=this.url+"/info/delete";
	        var cIdStr=cId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *查询车辆配置信息列表 
         *@param cVehicleNo  车牌号码,可选查询条件之一 
         *@param cDriver  驾驶员,可选查询条件之一 
         */
        service.getVehicleInfoList=function(cVehicleNo,cDriver,onSuccess)
        {
            var temp=this.url+"/info/list";
	        var cVehicleNoStr=cVehicleNo;
	        var cDriverStr=cDriver;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            cVehicleNo:cVehicleNoStr,
                            cDriver:cDriverStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *修改车辆行程记录 
         *@param cId  待修改车辆行程记录的Id 
         *@param travelRecord  待修改车辆行程记录的信息(新的) 
         */
        service.updateTravelRecordById=function(cId,travelRecord,onSuccess)
        {
            var temp=this.url+"/travel/record/update";
	        var cIdStr=cId;
	        var travelRecordStr=JSON.stringify(travelRecord);
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                            travelRecord:travelRecordStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *删除车辆行程记录 
         *@param cId  待删除车辆行程记录的Id 
         */
        service.deleteTravelRecordById=function(cId,onSuccess)
        {
            var temp=this.url+"/travel/record/delete";
	        var cIdStr=cId;
            $.ajax({
                    url:temp,
                    type:'post',
                    data:{
                            cId:cIdStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *分页查询车辆考勤记录 
         *@param page  分页查询参数 
         *@param cVehicleNo  车牌号码,可选查询参数之一 
         *@param startTime  开始时间,可选查询参数之一 
         *@param endTime  结束时间,可选查询参数之一 
         *@param violation  是否违规,0表示正常,1表示违规,可选查询参数之一 
         */
        service.getPunchRecordList=function(page,cVehicleNo,startTime,endTime,violation,onSuccess)
        {
            var temp=this.url+"/punch/record/list";
	        var pageStr=JSON.stringify(page);
	        var cVehicleNoStr=cVehicleNo;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
	        var violationStr=violation;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            cVehicleNo:cVehicleNoStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
                            violation:violationStr,
                        },
                    cache:false,
                    success:onSuccess,
                    error:common.onError
                    });
        }
        /**
        *分页统计,查询车辆行程记录列表 
         *@param page  分页查询对象 
         *@param cVehicleNo  车牌号码,可选查询条件之一 
         *@param startTime  开始时间,可选查询条件之一 
         *@param endTime  结束时间,可选查询条件之一 
         */
        service.getTravelRecordList=function(page,cVehicleNo,startTime,endTime,onSuccess)
        {
            var temp=this.url+"/travel/record/list";
	        var pageStr=JSON.stringify(page);
	        var cVehicleNoStr=cVehicleNo;
	        var startTimeStr=startTime;
	        var endTimeStr=endTime;
            $.ajax({
                    url:temp,
                    type:'get',
                    data:{
                            page:pageStr,
                            cVehicleNo:cVehicleNoStr,
                            startTime:startTimeStr,
                            endTime:endTimeStr,
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
         *@param workOrder no comment
         *@param alarmIds no comment
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
         *@param page  分页查询对象 
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
         *@param page  分页查询对象 
         *@param processStatus  工单处理状态 1表示按时完成的已下发工单，2表示未完成，超时完成的已下发工单 
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
        *申请延期工单 
         *@param cWOCode  工单号 
         *@param delayReason  延期原因 
         *@param delayTime  延期完成时间 
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
        *获取工单信息 
         *@param cWdId  工单编码 
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
        *根据工单号查询工单日志 
         *@param cWdId  工单号 
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
        *按地区工单统计 
         *@param page no comment
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
        *反馈工单审核 
         *@param workOrder no comment
         *@param refuse  是否驳回 驳回传入1，否则传入0 
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
         *@param workOrder no comment
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
        *查询当前用户订阅的工单 
         *@param page  查询条件对象 
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
        *导出过期工单列表 
         *@param page  查询对象 
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
         *@param page no comment
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
        *上报工单审核 
         *@param workOrder no comment
         *@param refuse  是否驳回 驳回传入1，否则传入0 
         *@param suspend  是否挂起 挂起传入1，否则传入0 
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
        *延期工单审核 
         *@param workOrder no comment
         *@param refuse  是否驳回 驳回传入1，否则传入0 
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
        *多条件查询过期工单 
         *@param page  查询对象 
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
        *导出工单列表 
         *@param page  查询条件 
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
        /**
        *按员工工单统计 
         *@param page no comment
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
        *按员工工单统计 
         *@param page no comment
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
        *按工单处理效率统计 
         *@param page no comment
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
         *@param page no comment
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
    return service;
}
