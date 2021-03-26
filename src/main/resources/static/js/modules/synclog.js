/**

 @Name：自定义异步日志框
 @Author：倾心

 */

;layui.define(['layer','table','form'],function (exports) {
    var $ = layui.$ ,layer = layui.layer , table = layui.table ,form = layui.form;

    var appName = $("appName").attr("name");
    /*自定义日志框对象*/
    var synclog = {
        localTable:'SYNCLOG_LOCALTABLE'
        ,url: appName + '/syncLog'
        ,_layer_content: ''
        ,_layer_index: ''
        ,_query_params:{}
        ,_init: function () {
            let me = this;
            let url = me.url + '?' + $.param(me._query_params);
            me._layer_index = layer.open({
                type: 2
                ,area: ['650px', '400px']
                ,skin: 'layui-layer-rim' //加上边框
                ,title: '<i class="layui-icon">&#xe62c;</i>异步日志框'
                ,maxmin: true
                ,content: [url,'no'] //这里content是一个普通的String
            });
        }
        ,setLocalData: function ( key , value ) {
            let me = this;
            layui.data(me.localTable, { key: key ,value: value });
        }
        ,getLocalData: function ( key ) {
            let me = this;
            let data = layui.data(me.localTable);
            return data[key] || '';
        }
        ,removeLocalData: function (key) {
            layui.data(me.localTable, {
                key: key
                ,remove: true
            });
        }
        ,show: function (key,callback) {
            let me = this;
            let threadId = me.getLocalData(key);
            $.extend(me._query_params,{threadId:threadId});
            me._init();
        }
        ,done: null
    };

    exports('synclog',synclog);
});