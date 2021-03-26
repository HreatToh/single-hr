(function () {
    /*自定义字符串工具*/
    var StrUtils = {
        /*是否是空*/
        isNull: function (s) {
            if (s) {
                return false;
            }
            return true;
        }
        /*是否不为空*/
        , isNotNull: function (s) {
            return !this.isNull(s);
        }
        /*相等*/
        , equals: function (s1, s2) {
            let me = this;
            return (me.toEmpty(s1) == me.toEmpty(s2));
        }
        /*结束*/
        , endsWith: function (s1, s2) {
            let me = this, s;
            s1 = me.toEmpty(s1), s2 = me.toEmpty(s2);

            if (s2.length > s1.length) {
                return false;
            }
            return me.equals(me.right(s1, s2.length), s2);
        }

        , startsWith(s1, s2) {
            return s1.startsWith(s2);
        }
        /*从右边截取多少位*/
        , right: function (s, len) {
            let me = this;
            s = me.toEmpty(s);
            if (s.length > len) {
                return s.substring(s.length - len);
            } else {
                return s;
            }

        }

        /*从左边截取多少位*/
        , left: function (s, len) {
            let me = this;
            s = me.toEmpty(s);
            if (s.length > len) {
                return s.substring(0, len);
            } else {
                return s;
            }
        }

        /*过滤空的情况*/
        , toEmpty: function (s) {
            return s || '';
        }
    };

    /*自定义数值工具*/
    var NumUtils = {

        /*数字格式化*/
        format: function (n, calc) {
            let a, me = this;
            if (n instanceof String) {
                return parseFloat(n).toFixed(calc);
            }
            if (typeof (n) == 'number') {
                return n.toFixed(calc);
            }

            return n;
        }

        , int: function (s) {
            return parseInt(s);
        }

    };

    /*自定义json工具*/
    var JsonUtils = {

        /*转换json字符串*/
        toJson: function (object) {
            return JSON.stringify(object);
        }

        /*转成对象*/
        , parseJson: function (s) {
            return eval('(' + s + ')');
        }

    };

    var DateUtils = {
        now: null

        , FORMAT8 : 'yyyyMMdd'

        , FORMAT10 : 'yyyy-MM-dd'

        , FORMAT14 : 'yyyyMMddHHmmss'

        , FORMAT19 : 'yyyy-MM-dd HH:mm:ss'

        , init: function () {
            let me = this;
            me.now = new Date();
        }

        , getToDay8: function () {
            let me = this;
            return me.format(me.FORMAT8);
        }

        , getToDay10: function () {
            let me = this;
            return me.format(me.FORMAT10);
        }

        , getToDay14: function () {
            let me = this;
            return me.format(me.FORMAT14);
        }

        , getToDay19: function () {
            let me = this;
            return me.format(me.FORMAT19);
        }

        , getYear: function () {
            let me = this;
            me.init();
            return me.now.getFullYear();
        }

        , getYY: function () {
            let me = this, y;
            me.init();
            y = me.now.getYear();
            if (!(( y + '' ).length > 1)) {
                y = '0' + y;
            }
            return y;
        }

        , getMonth: function () {
            let me = this, m;
            me.init();
            m = me.now.getMonth();
            if (!(( m + '' ).length > 1)) {
                m = '0' + (m + 1);
            }
            return m
        }

        , getDay: function () {
            let me = this, d;
            me.init();
            d = me.now.getDate();

            if (!(( d +'' ).length > 1)) {
                d = '0' + d;
            }
            return d;
        }

        , format: function (format) {

            let formatStr = format, Week = ['日', '一', '二', '三', '四', '五', '六'], me = this;
            me.init();

            formatStr = formatStr.replace(/yyyy|YYYY/, me.getYear());

            formatStr = formatStr.replace(/yy|YY/, me.getYY());

            formatStr = formatStr.replace(/MM/, me.getMonth());

            formatStr = formatStr.replace(/M/g, me.now.getMonth());

            formatStr = formatStr.replace(/w|W/g, Week[me.now.getDay()]);

            formatStr = formatStr.replace(/dd|DD/, me.getDay());

            formatStr = formatStr.replace(/d|D/g, me.now.getDate());

            formatStr = formatStr.replace(/hh|HH/, me.now.getHours() > 9 ? me.now.getHours().toString() : '0' + me.now.getHours());

            formatStr = formatStr.replace(/h|H/g, me.now.getHours());

            formatStr = formatStr.replace(/mm/, me.now.getMinutes() > 9 ? me.now.getMinutes().toString() : '0' + me.now.getMinutes());

            formatStr = formatStr.replace(/m/g, me.now.getMinutes());

            formatStr = formatStr.replace(/ss|SS/, me.now.getSeconds() > 9 ? me.now.getSeconds().toString() : '0' + me.now.getSeconds());

            formatStr = formatStr.replace(/s|S/g, me.now.getSeconds());


            return formatStr;
        }
    };

    window.StrUtils = StrUtils;
    window.NumUtils = NumUtils;
    window.JsonUtils = JsonUtils;
    window.DateUtils = DateUtils;
}())