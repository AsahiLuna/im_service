console.log("前端代码写的不好，主要为了实现功能，欢迎大神们指点！")
var vm = new Vue({
    el: '#app',
    data: {
        "groupStatus": "init",
        // "newUserStatus": "initDone",
        "newUserStatus": "init",
        "selectedGender": "男",
        // "userName": "1",
        "userName": "",
        "userAge": 0,
        "userSign": "哥的传说",
        "notifyMessage": "",
        "to": "",
        "message": "",
        "selectedMessageType": "onePeople",
        "sendBtnType": "单人聊天 --> 点击发送",
        "receiveLabelType": "收信人",
        "syncData": {},
        "newGroupName": "",
        "newGroupType": "",
        "newGroupDescription": "",
        "groupSearchData": {},
        "joinGroupName": "",
        "leaveGroupName": ""
    },
    methods: {
        leaveGroup: function () {
            $.ajax({
                type: 'post',
                url: "UserAndGroup/userLeaveGroup",
                dataType: "json",
                data: {
                    groupName: vm.joinGroupName, userName: vm.userName
                },
                success: function (data) {
                    console.log(data)
                    $('#groupModal').modal('hide')
                    vm.groupStatus = 'init'
                    vm.notifyModal("退出群成功！")
                },
                error: function (req, data, e) {
                    console.log(req)
                    vm.notifyModal(JSON.stringify(req, null, 2))
                }
            })
        },
        joinGroup: function () {
            $.ajax({
                type: 'post',
                url: "UserAndGroup/userJoinGroup",
                dataType: "json",
                data: {
                    groupName: vm.joinGroupName, userName: vm.userName
                },
                success: function (data) {
                    console.log(data)
                    $('#groupModal').modal('hide')
                    vm.groupStatus = 'init'
                    vm.notifyModal("加入成功！")
                },
                error: function (req, data, e) {
                    console.log(req)
                    vm.notifyModal(JSON.stringify(req, null, 2))
                }
            })
        },
        sendMessage: function () {
            $.ajax({
                type: 'post',
                url: "Timeline/sendMessage",
                dataType: "json",
                data: {from: vm.userName, message: vm.message, to: vm.to, type: vm.selectedMessageType},
                success: function (data) {
                    vm.notifyModal("发送成功")
                },
                error: function (req, data, e) {
                    console.log(req)
                    vm.notifyModal(JSON.stringify(req, null, 2))
                }
            });
        },
        createGroup: function () {
            $.ajax({
                type: 'post',
                url: "UserAndGroup/addGroup",
                dataType: "json",
                data: {
                    groupDescription: vm.newGroupDescription,
                    groupName: vm.newGroupName,
                    groupType: vm.newGroupType
                },
                success: function (data) {
                    $('#groupModal').modal('hide')
                    vm.groupStatus = 'init'
                    vm.notifyModal("新群创建成功！")

                },
                error: function (req, data, e) {
                    console.log(req)
                    vm.notifyModal(JSON.stringify(req, null, 2))
                }
            });
        },
        getSyncMessage: function () {
            $.ajax({
                type: 'get',
                url: "Timeline/getSyncMessage",
                dataType: "json",
                data: {user: vm.userName},
                success: function (data) {
                    vm.syncData = data
                },
                error: function (req, data, e) {
                    console.error(req)
                }
            });
        },
        toggleSendBtnType: function () {
            if (vm.selectedMessageType === "onePeople") {
                vm.sendBtnType = "单人聊天 --> 点击发送"
                vm.receiveLabelType = "收信-用户名"
            }
            if (vm.selectedMessageType === "group") {
                vm.sendBtnType = "群聊 --> 点击发送"
                vm.receiveLabelType = "收信-群名"
            }
        },
        userLogin: function () {
            $.ajax({
                type: 'post',
                url: "UserAndGroup/existUser",
                dataType: "json",
                data: {userName: vm.userName},
                success: function (data) {
                    console.log(data)
                    if (data) {
                        vm.newUserStatus = "initDone"
                        $('#initModal').modal('toggle')
                    }
                    else {
                        vm.notifyModal("您输入的用户名不存在！")
                    }
                },
                error: function (req, data, e) {
                    console.log(data)
                    vm.notifyModal(JSON.stringify(req, null, 2))
                }
            });
        },
        userRegister: function () {
            $.ajax({
                type: 'post',
                url: "UserAndGroup/addUser",
                dataType: "json",
                data: {
                    age: vm.userAge,
                    gender: vm.selectedGender,
                    sign: vm.userSign,
                    userName: vm.userName
                },
                success: function (data) {
                    vm.newUserStatus = "initDone"
                    $('#initModal').modal('hide')
                    vm.notifyModal("创建成功！")

                },
                error: function (req, data, e) {
                    console.log(req)
                    vm.notifyModal(JSON.stringify(req, null, 2))
                }

            });
            $('#initModal').modal('toggle')
        },
        notifyModal: function (message) {
            vm.notifyMessage = message
            $('#infoModal').modal('toggle')
        }
    }
})

console.log(vm.newUserStatus)
if (vm.newUserStatus === "init") {
    $('#initModal').modal('toggle')
}

function sync() {
    if (vm.newUserStatus === 'initDone') {
        vm.getSyncMessage()
    }
}

//定时取消息
setInterval("sync()", 2000);

//取消enter键绑定
$(document).keydown(function (event) {
    switch (event.keyCode) {
        case 13:
            return false;
    }
});