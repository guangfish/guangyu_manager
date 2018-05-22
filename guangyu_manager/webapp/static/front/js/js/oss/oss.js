UE.registerUI('button1', function (editor, uiName) {
    var btn = new UE.ui.Button({
        name: 'oss_img',
        title: '图片上传',
        cssRules: 'background-position: -380px 0;'
    });
    setTimeout(function () {
        var btnId = $('.edui-button.edui-for-oss_img').attr('id');
        var uploader = pluploadCreate(_staticPath + '/index/js/oss/get.php', btnId, _staticPath + '/index/js/oss/lib/plupload-2.1.2/', 'http://weizhanimg.new4s.com', function (imgUrl) {
            editor.execCommand('insertimage', {
                src: imgUrl
            });
        }, function (err) {
            if (err.code == -600) {
                alert('选择的文件太大了'); 
            } else if (err.code == -601) {
                alert('请选择图片文件');
            } else if (err.code == -602) {
                alert('该文件已上传,请不要重复上传');
            } else {
                alert(err.response);
            }
        });
    }, 500);
    return btn;
});

UE.registerUI('button2', function (editor, uiName) {
    var btn = new UE.ui.Button({
        name: 'oss_img_batch',
        title: '批量图片上传',
        cssRules: 'background-position: -726px -77px;'
    });
    setTimeout(function () {
        var btnId = $('.edui-button.edui-for-oss_img_batch').attr('id');
        var uploader = pluploadBatchCreate(_staticPath + '/index/js/oss/get.php', btnId, _staticPath + '/index/js/oss/lib/plupload-2.1.2/', 'http://weizhanimg.new4s.com', function (imgUrl) {
            editor.execCommand('insertimage', {
                src: imgUrl
            });
        }, function (err) {
            if (err.code == -600) {
                alert('选择的文件太大了'); 
            } else if (err.code == -601) {
                alert('请选择图片文件');
            } else if (err.code == -602) {
                alert('该文件已上传,请不要重复上传');
            } else {
                alert(err.response);
            }
        });
    }, 500);
    return btn;
});