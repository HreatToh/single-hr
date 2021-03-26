(function () {

    var appName = $("appName").attr("name");

    var Dict = {

        config: {
            url: appName + '/dict/getDict'
            , type: 'post'
            , dataType: 'json'
            , data: {}
            , async: false
        }
        /*初始化字典*/
        , init: function () {
            let me = this;
            let config = $.extend({}, me.config);
            config.url = appName + '/dict/getAllDict';
            config.data = {};
            $.ajax(config).done(function (data) {
                let d = data || {};
                if (d instanceof Object) {
                    me.dict = d;
                }
            });
        }
        , dict: {}
        , getDict: function (type) {
            let me = this;
            if (me.dict[type]) {
                return me.dict[type]
            } else {
                $.extend(me.config.data, {dictType: type});
                $.ajax(me.config).done(function (data) {
                    me.load(type, data)
                });
            }
        }
        , load: function (type, data) {
            let d = data || {}, me = this;
            if (d instanceof Object) {
                me.dict[type] = d;
            } else {
                me.dict[type] = eval('(' + d + ')');
            }
        }
        , getValue: function (type, key) {
            let me = this, value;
            let d = me.getDict(type);
            if (d) {
                value = d[key];
            }
            return (value || key);
        }
    };

    window.Dict = Dict;
}())