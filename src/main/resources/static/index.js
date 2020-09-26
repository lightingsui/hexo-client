new Vue({
    el: '#app',
    data: {
        // 服务器输入信息
        serverInput: {
            ipInput: null,
            port: 22,
            password: null,
            hexoUrl: null,
        },

        // 服务器显示信息
        serverMessage: {},

        // 新文件名称
        newFileInput: null,

        // 进度条隐藏 true: 隐藏   false：显示
        processBarIsHidden: true,

        // 连接标志
        connect: false,

        // 连接按钮显示文字
        connectBtnText: "连接 Hexo 服务器",

        // 连接按钮禁用  true 禁用   false 启用
        connectBtnDisabled: false,

        // 富文本编辑器内容
        mdValue: null,

        // 富文本编辑器对象
        editor: null,

        // 正在上传博客中
        isUploadBlog: false,

        iconBtn: null,

    },

    methods: {
        // 连接按钮点击事件
        connectBtnListener() {
            this.connectBtnDisabled = true;
            if (this.connect) {
                if (this.isUploadBlog) {
                    this.$message.error('正在上传博客中，禁止断开连接 ~~~');
                    return;
                }

                this.initServerMessage();

                this.connect = false;

                this.connectBtnText = "连接 Hexo 服务器";

                this.connectBtnDisabled = false;

                this.$message({
                    message: '断开连接',
                    type: 'warning'
                });
            } else {
                // 信息输入框检测
                if (!this.inputCheck()) {
                    this.$message.error('请输入服务器连接信息');
                    this.connectBtnDisabled = false;
                    return;
                }

                this.iconBtn = "el-icon-loading";
                this.connectBtnText = null;

                // 发送请求连接
                axios.get("./server/connect", {
                    params: {
                        host: this.serverInput.ipInput,
                        port: this.serverInput.port,
                        password: this.serverInput.password,
                        path: this.serverInput.hexoUrl
                    }
                }).then(res => {
                    if (res.data.responseCode == "200") {
                        this.connectBtnText = "断开连接";
                        this.connectBtnDisabled = false;
                        this.connect = true;
                        this.iconBtn = null;

                        this.$message({
                            message: '登陆成功，正在加载服务器信息',
                            type: 'success'
                        });

                        // 加载服务器信息
                        axios.get("./server/server-message")
                            .then(res => {
                                if (res.data.responseCode == "200") {
                                    this.serverMessage = res.data.data;
                                    this.$message({
                                        message: '服务器信息加载成功',
                                        type: 'success'
                                    });
                                } else {
                                    this.$message.error('loading failed, please try');
                                }
                            })
                            .catch(err => {
                                this.$message.error('loading failed, please try');
                            })
                    } else {
                        this.connectBtnText = "连接 Hexo 服务器";
                        this.iconBtn = null;
                        this.connectBtnDisabled = false;
                        this.$message.error('connect failed');
                    }

                }).catch(err => {
                    this.connectBtnText = "连接 Hexo 服务器";
                    this.iconBtn = null;
                    this.$message.error('connect failed');
                    this.connectBtnDisabled = false;
                })
            }

        },

        // 新建文件按钮点击事件
        newFIleBtnListener() {
            if (this.newFileInput == null || this.newFileInput == "") {
                this.$message.error('请输入文件名称 ~~~');
                return;
            }

            // 设置默认值
            this.editor.setValue("---\n" +
                "title: "+ this.newFileInput +"\n" +
                "date: "+ this.dateFormat("yyyy-MM-dd hh:mm:ss", new Date()) +"\n" +
                "tags: \n" +
                "categories: \n" +
                "---");
        },

        // 上传博客按钮点击事件
        uploadFileBtnListener() {
            let content = this.editor.getValue();
            if (content == null || content == "") {
                this.$message.error('请填写博客内容 ~~~');
                return;
            }

            // 没有连接服务器
            if (!this.connect) {
                this.$message.error('清先连接服务器 ~~~');
                return;
            }

            // 上传博客逻辑
            this.isUploadBlog = true;
            this.processBarIsHidden = false;

            // 发送请求
            axios({
                method: 'post',
                url: "./server/upload-blog",
                data: {
                    content: content,
                    fileName: this.newFileInput
                }
            }).then(res => {
                if (res.data.responseCode == "200") {
                    if (res.data.data) {
                        this.$message({
                            message: '上传成功 ~~~',
                            type: 'success'
                        });
                    }
                    this.processBarIsHidden = true;
                } else {
                    this.processBarIsHidden = true;
                    this.$message.error('上传失败，请稍后重试 ~~~');
                }
            }).catch(error => {
                this.processBarIsHidden = true;
                this.$message.error('上传失败，请稍后重试 ~~~');
            });
            this.isUploadBlog = false;
        },

        /**
         * 返回 true 代表检测通过
         * 返回 false 代表检测不通过
         */
        inputCheck() {
            let parmasCheck = this.serverInput.ipInput == null || this.serverInput.password == null
                || this.serverInput.hexoUrl == null;

            return !parmasCheck;
        },

        // 初始化服务器信息
        initServerMessage() {
            this.serverMessage = {
                kernelName: null,
                nodeName: null,
                kernelRelease: null,
                machine: null,
                processor: null,
                operatingSystem: null,
                hardwarePlatform: null,
                kernelVersion: null
            }
        },

        // 时间格式化
        dateFormat (fmt, date) { //author: meizz
            var o = {
                "M+": date.getMonth() + 1, //月份
                "d+": date.getDate(), //日
                "h+": date.getHours(), //小时
                "m+": date.getMinutes(), //分
                "s+": date.getSeconds(), //秒
                "q+": Math.floor((date.getMonth() + 3) / 3), //季度
                "S": date.getMilliseconds() //毫秒
            };
            if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            return fmt;
        }
    },

    created() {
        this.initServerMessage();
    },

    mounted() {
        this.editor = editormd("editor", {
            width: "100%",
            height: 640,

            path: "lib/js/editor.md-master/lib/", //你的path路径（原资源文件中lib包在我们项目中所放的位置）
            // theme: "dark", //工具栏主题
            // previewTheme: "dark", //预览主题
            // editorTheme: "pastel-on-dark", //编辑主题
            saveHTMLToTextarea: true,
            emoji: false,
            taskList: true,
            tocm: true, // Using [TOCM]
            tex: true, // 开启科学公式TeX语言支持，默认关闭
            flowChart: true, // 开启流程图支持，默认关闭
            sequenceDiagram: true, // 开启时序/序列图支持，默认关闭,
            toolbarIcons: function () { //自定义工具栏，后面有详细介绍
                return editormd.toolbarModes['simple']; // full, simple, mini
            },
        });


    }

})