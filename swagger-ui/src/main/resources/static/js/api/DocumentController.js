function DocumentController(){
    var service=new Object();
    service.url=host+"/doc"+"/api/document";
    /**
      *查询接口文档
      *@param file key
      */
     service.uploadDocument=function(file,onSuccess){
	     var fileStr=JSON.stringify(file);
		 var url=this.url+"/upload";
		 var parameters = {
                   file:fileStr,
		 
		 }
         //当形参和实参数量相等时，执行异步的请求
        if(arguments.callee.length === arguments.length){
            axios({
                method: 'Post',
                url: url,
                changeOrigin: true, //是否跨域
                withCredentials: true,
                data:Qs.stringify(parameters)
            })
                .then(onSuccess)
                .catch(common.hngdApiRespError);
        }else{
            // common.hngdApiArgumentsError();
        }
        return{
            url:url,
            parameters:parameters
        }
    };
           
    /**
      *加载所有模块的接口文档
      */
     service.getAllDocument=function(onSuccess){
		 var url=this.url+"/list/all";
		 var parameters = {
		 
		 }
         //当形参和实参数量相等时，执行异步的请求
        if(arguments.callee.length === arguments.length){
            axios({
                method: 'Get',
                url: url,
                changeOrigin: true, //是否跨域
                withCredentials: true,
                params:parameters
            })
                .then(onSuccess)
                .catch(common.hngdApiRespError);
        }else{
            // common.hngdApiArgumentsError();
        }
        return{
            url:url,
            parameters:parameters
        }
    };
           
    /**
      *加载所有模块接口文档的原文件
      */
     service.getAllOriginDocument=function(onSuccess){
		 var url=this.url+"/origin/file/list";
		 var parameters = {
		 
		 }
         //当形参和实参数量相等时，执行异步的请求
        if(arguments.callee.length === arguments.length){
            axios({
                method: 'Get',
                url: url,
                changeOrigin: true, //是否跨域
                withCredentials: true,
                params:parameters
            })
                .then(onSuccess)
                .catch(common.hngdApiRespError);
        }else{
            // common.hngdApiArgumentsError();
        }
        return{
            url:url,
            parameters:parameters
        }
    };
           
    /**
      *删除原文件
      *@param filename no comment
      */
     service.deleteOriginFile=function(filename,onSuccess){
	     var filenameStr=filename;
		 var url=this.url+"/origin/file/delete";
		 var parameters = {
                   filename:filenameStr,
		 
		 }
         //当形参和实参数量相等时，执行异步的请求
        if(arguments.callee.length === arguments.length){
            axios({
                method: 'Post',
                url: url,
                changeOrigin: true, //是否跨域
                withCredentials: true,
                data:Qs.stringify(parameters)
            })
                .then(onSuccess)
                .catch(common.hngdApiRespError);
        }else{
            // common.hngdApiArgumentsError();
        }
        return{
            url:url,
            parameters:parameters
        }
    };
           
    /**
      *加载接口文档
      *@param filename filename
      */
     service.loadDocumnet=function(filename,onSuccess){
	     var filenameStr=filename;
		 var url=this.url+"/info/"+filenameStr+"";
		 var parameters = {
                   filename:filenameStr,
		 
		 }
         //当形参和实参数量相等时，执行异步的请求
        if(arguments.callee.length === arguments.length){
            axios({
                method: 'Get',
                url: url,
                changeOrigin: true, //是否跨域
                withCredentials: true,
                params:parameters
            })
                .then(onSuccess)
                .catch(common.hngdApiRespError);
        }else{
            // common.hngdApiArgumentsError();
        }
        return{
            url:url,
            parameters:parameters
        }
    };
           
    /**
      *加载接口文档
      *@param filename filename
      *@param tag no comment
      */
     service.loadDocumnetByTag=function(filename,tag,onSuccess){
	     var filenameStr=filename;
	     var tagStr=tag;
		 var url=this.url+"/info/"+filenameStr+"/"+tagStr+"";
		 var parameters = {
                   filename:filenameStr,
                   tag:tagStr,
		 
		 }
         //当形参和实参数量相等时，执行异步的请求
        if(arguments.callee.length === arguments.length){
            axios({
                method: 'Get',
                url: url,
                changeOrigin: true, //是否跨域
                withCredentials: true,
                params:parameters
            })
                .then(onSuccess)
                .catch(common.hngdApiRespError);
        }else{
            // common.hngdApiArgumentsError();
        }
        return{
            url:url,
            parameters:parameters
        }
    };
           
    /**
      *查询接口文档
      *@param key key
      */
     service.uploadDocument=function(key,onSuccess){
	     var keyStr=key;
		 var url=this.url+"/query";
		 var parameters = {
                   key:keyStr,
		 
		 }
         //当形参和实参数量相等时，执行异步的请求
        if(arguments.callee.length === arguments.length){
            axios({
                method: 'Get',
                url: url,
                changeOrigin: true, //是否跨域
                withCredentials: true,
                params:parameters
            })
                .then(onSuccess)
                .catch(common.hngdApiRespError);
        }else{
            // common.hngdApiArgumentsError();
        }
        return{
            url:url,
            parameters:parameters
        }
    };
           
    return service;
}
