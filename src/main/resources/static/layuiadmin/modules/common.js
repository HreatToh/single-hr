/** layuiAdmin.std-v1.0.0 LPPL License By http://www.layui.com/admin/ */
;layui.define(function (e) {
    var i = (layui.$, layui.layer, layui.laytpl, layui.setter, layui.view, layui.admin) , t = layui.jquery;
    i.events.logout = function () {
        i.req({
            url: "logout", type: "post", data: {}, done: function (e) {

            }
        })
    }, e("common", {})
});