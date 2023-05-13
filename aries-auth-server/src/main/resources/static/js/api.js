var Auth= window.Auth || {};
Auth.Api = Auth.Api || {};
Auth.$ = $ || {};

Auth.Api.ajax = function (url, data, async, type, dataType, contentType,withCredentials, successfn, errorfn) {
        async = (async == null || async == "" || typeof (async) == "undefined") ? "true" : async;
        type = (type == null || type == "" || typeof (type) == "undefined") ? "post" : type;
        dataType = (dataType == null || dataType == "" || typeof (dataType) == "undefined") ? "json" : dataType;
        contentType = (contentType == null || contentType == "" || typeof (contentType) == "undefined") ? "application/json" : contentType;
        data = (data == null || data == "" || typeof (data) == "undefined") ? { "date": new Date().getTime() } : data;
        withCredentials = (withCredentials == null || async == "" || typeof (async) == "undefined") ? "true" : withCredentials;
    Auth.$.ajax({
            type: type,
            async: async,
            data: data,
            url: url,
            dataType: dataType,
            crossDomain: true,  
            xhrFields: {
                withCredentials: withCredentials //允许跨域带Cookie
            },
            contentType: contentType,
            success: function (d) {
                if (successfn) {
                    successfn(d)
                };
            },
            error: function (e) {
                if (errorfn) {
                    errorfn(e);
                }
            }
        });
    };

Auth.Api.get = function (url, data, successfn, errorfn,contentType,withCredentials) {
    Auth.Api.ajax(url, data, null, "get", null, contentType,withCredentials, successfn, errorfn);
}


Auth.Api.post = function (url, data, successfn, errorfn,contentType,withCredentials) {
    Auth.Api.ajax(url, data, null, "post", null, contentType,withCredentials, successfn, errorfn);
    }

Auth.Api.buildRequest = function (kind,request) {
    let res = {
        "context": {

            "from": "zyking",
            "version": "1.0.0",
            "client": "uc.zyking.xyz",
            "timestamp": Auth.util.toDateString(new Date(), "yyyyMMddHHmmss") ,
            "kind": kind
        },
        "request": request
    };
    return res;
}
