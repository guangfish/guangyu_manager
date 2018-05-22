function pluploadCreate(serverUrl, browse_button, pluploadPath, url, success, fail, isBatch) {

    var options = pluploadOptionCreate(serverUrl, browse_button, pluploadPath, url, success, fail);

    var accessid = '',
        accesskey = '',
        host = '',
        policyBase64 = '',
        signature = '',
        callbackbody = '',
        filename = '',
        key = '',
        expire = 0,
        g_object_name = '',
        g_object_name_type = 'random_name',
        now = ((new Date()) / 1000).toFixed(0) * 1;

    function send_request() {
        /*var xmlhttp = null;
        if(window.XMLHttpRequest) {
            xmlhttp = new XMLHttpRequest();
        } else if(window.ActiveXObject) {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        if(xmlhttp != null) {
            var serverUrl = options.serverUrl;
            xmlhttp.open("GET", serverUrl, false);
            xmlhttp.send(null);
            return xmlhttp.responseText;
        } else {
            alert("Your browser does not support XMLHTTP.");
        }*/

        var serverUrl = options.serverUrl;
        var result = '';
        $.ajax({
            url: serverUrl,
            type: 'get',
            dataType: 'text',
            async: false,
            success: function(data) {
                result = data;
                console.log(result);
            }
        });
        return result;
    }

    function get_signature() {
        //可以判断当前expire是否超过了当前时间,如果超过了当前时间,就重新取一下.3s 做为缓冲
        now = ((new Date()) / 1000).toFixed(0) * 1;
        if(expire < now + 3) {
            var body = send_request();
            var obj = eval("(" + body + ")");
            host = obj['host'];
            policyBase64 = obj['policy'];
            accessid = obj['accessid'];
            signature = obj['signature'];
            expire = parseInt(obj['expire']);
            callbackbody = obj['callback'];
            key = obj['dir'];
            return true;
        }
        return false;
    }

    function get_suffix(filename) {
        var pos = filename.lastIndexOf('.');
        var suffix = '';
        if(pos != -1) {
            suffix = filename.substring(pos)
        }
        return suffix;
    }

    function random_string(len) {
        len = len || 32;
        var chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
        var maxPos = chars.length;
        var pwd = '';
        for(i = 0; i < len; i++) {
            pwd += chars.charAt(Math.floor(Math.random() * maxPos));
        }
        return pwd;
    }

    function set_upload_param(up, filename, ret) {
        if(ret == false) {
            ret = get_signature()
        }
        g_object_name = key;
        if(filename != '') {
            var suffix = get_suffix(filename);
            g_object_name = key + random_string(10) + suffix;
        }
        var new_multipart_params = {
            'key': g_object_name,
            'policy': policyBase64,
            'OSSAccessKeyId': accessid,
            'success_action_status': '200', //让服务端返回200,不然，默认会返回204
            'callback': callbackbody,
            'signature': signature
        };

        up.setOption({
            'url': host,
            'multipart_params': new_multipart_params
        });

        up.start();
    }

    var initSetting = {
        runtimes: 'html5,flash,silverlight,html4',
        browse_button: options.elem,
        multi_selection: !!isBatch, //是否允许多选
        flash_swf_url: 'lib/plupload-2.1.2/js/Moxie.swf',
        silverlight_xap_url: 'lib/plupload-2.1.2/js/Moxie.xap',
        url: '',
        filters: {
            mime_types: [ //只允许上传图片
                {
                    title: "Image files",
                    extensions: "jpg,gif,png,bmp,ico,jpeg"
                }
            ],
            max_file_size: '10mb', //最大只能上传10mb的文件
            prevent_duplicates: true //不允许选取重复文件
        },
        init: {
            FilesAdded: function(up, files) {
                setTimeout(function() {
                    set_upload_param(uploader, '', false);
                }, 150);
                options.uploaderSetting.init.FilesAdded && options.uploaderSetting.init.FilesAdded(up, files);
            },
            BeforeUpload: function(up, file) {
                set_upload_param(up, file.name, true);
                options.uploaderSetting.init.BeforeUpload && options.uploaderSetting.init.BeforeUpload(up, file);
            },
            FileUploaded: function(up, file, info) {
                options.uploaderSetting.init.FileUploaded && options.uploaderSetting.init.FileUploaded(up, file, info);
            },
            Error: function(up, err) {
                options.uploaderSetting.init.Error && options.uploaderSetting.init.Error(up, err);
            }
        }
    }; 
    for(var i in options.uploaderSetting.init) {
        initSetting.init[i] = options.uploaderSetting.init[i];
    }
    for(var i in options.uploaderSetting.filters) {
        initSetting.filters[i] = options.uploaderSetting.filters[i];
    }
    for(var i in options.uploaderSetting) {
        if(i != 'init' && i != 'filters') {
            initSetting[i] = options.uploaderSetting[i];
        }
    }
    var uploader = new plupload.Uploader(initSetting);
    uploader.init();
    return uploader;
};

function pluploadBatchCreate(serverUrl, browse_button, pluploadPath, url, success, fail) {
    return pluploadCreate(serverUrl, browse_button, pluploadPath, url, success, fail, true);
}

function pluploadOptionCreate(serverUrl, browse_button, pluploadPath, url, success, fail) {
    return {
        serverUrl: serverUrl,
        uploaderSetting: {
            browse_button: browse_button,
            flash_swf_url: pluploadPath.replace(/\/$/, '') + '/js/Moxie.swf',
            silverlight_xap_url: pluploadPath.replace(/\/$/, '') + '/js/Moxie.xap',
            url: url,
            init: {
                FileUploaded: function(up, file, info) {
                    var fileName = up.getOption().multipart_params.key;
                    success(url + '/' + fileName);
                },
                Error: function(up, err) {
                    fail(err);
                }
            }
        }
    }
}