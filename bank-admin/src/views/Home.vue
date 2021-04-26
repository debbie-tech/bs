<template>
    <div class="home">
        <div class="header" v-if="showPage">
            <div class="logo">
                <div style="display:flex">
                    <div style="margin-top:-15px">
                        <img src="../assets/img/logo.jpg" alt />
                    </div>
                    <div style="margin-left:4px">
                        <div style="font-weight:bold">Prix</div>
                        <div style="font-weight:bold">bank</div>
                    </div>
                </div>
            </div>
            <div class="memu">
                <span
                    v-for="(item,index) in meneData"
                    :key="index"
                    @click="changeTab(item.link)"
                    :class="activeSpan==item.link?'active':''"
                >{{item.name}}</span>
            </div>
        </div>
        <div class="banner">
            <div style="float:left;margin-left:200px;position:absolute;z-index:999">
                <p style="color:red;margin-top:40px;font-size:40px">方便快速</p>
                <div style="color:red;margin-top:40px;font-size:40px">实现快捷转账开票</div>
            </div>
            <el-carousel loop>
                <el-carousel-item>
                    <img src="../assets/img/banner.jpg" alt />
                </el-carousel-item>
                <el-carousel-item>
                    <img src="../assets/img/banner1.jpg" alt />
                </el-carousel-item>
                <el-carousel-item>
                    <img src="../assets/img/banner2.jpg" alt />
                </el-carousel-item>
            </el-carousel>
        </div>
        <router-view></router-view>
    </div>
</template>
<script>
export default {
    data() {
        return {
            showPage:true,
            activeSpan: "openAcount",
            meneData: [
                {
                    key: "1",
                    name: "开户",
                    link: "openAcount"
                },
                {
                    key: "2",
                    name: "开票",
                    link: "billing"
                },
                {
                    key: "3",
                    name: "转账",
                    link: "transferAccounts"
                },
                {
                    key: "4",
                    name: "个人中心",
                    link: "personal"
                },
                {
                    key: "5",
                    name: "登录",
                    link: "login"
                }
            ]
        };
    },
  methods: {
        changeTab(key) {
            this.activeSpan = key;
            if (
                this.activeSpan != "openAcount" &&
                !sessionStorage.getItem("token")
            ) {
                console.log(222)
                // if(!sessionStorage.getItem('token')){
                //   this.$router.push('/login')
                // }
                // this.$router.push("/home/login");
                // this.activeSpan = 'login'
                // this.$message.warning('请先登录')
                this.$router.push("/home/" + key);
            } else if (this.activeSpan == "loginOut") {
                this.$confirm("是否退出登录?", "提示", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "info",
                    center: true
                }).then(() => {
                    sessionStorage.clear();
                    this.meneData = [
                        {
                            key: "1",
                            name: "开户",
                            link: "openAcount"
                        },
                        {
                            key: "2",
                            name: "开票",
                            link: "billing"
                        },
                        {
                            key: "3",
                            name: "转账",
                            link: "transferAccounts"
                        },
                        {
                            key: "4",
                            name: "个人中心",
                            link: "personal"
                        },
                        {
                            key: "5",
                            name: "登录",
                            link: "login"
                        }
                    ];
                    this.$message({
                        type: "success",
                        message: "退出成功"
                    });
                    
                });
            } else {
                this.$router.push("/home/" + key);
            }
        },
        
        // 获取菜单数据
        async getMenuList() {
            const { data: res } = await this.$axios1.get("menus");
            if (res.meta.status !== 200)
                return this.$message.error(res.meta.msg);
            this.menuList = res.data;
        },
        // 侧边菜单的折叠与展开
        toggleCollapse() {
            this.isCollapse = !this.isCollapse;
        },
        // 保存菜单的激活状态,即使刷新页面,被激活的菜单还是刷新之前的那个菜单
        saveNavState(activePath) {
            sessionStorage.setItem("activePath", activePath);
            this.activePath = activePath;
        }
    },
 created() {
        // this.getMenuList()
        console.log(this.$route.path.split("/")[2]);
        this.activeSpan = this.$route.path.split("/")[2];
        if (sessionStorage.getItem("token")) {
            this.meneData = [
                {
                    key: "1",
                    name: "开户",
                    link: "openAcount"
                },
                {
                    key: "2",
                    name: "开票",
                    link: "billing"
                },
                {
                    key: "3",
                    name: "转账",
                    link: "transferAccounts"
                },
                {
                    key: "4",
                    name: "个人中心",
                    link: "personal"
                },
                {
                    key: "5",
                    name: "退出登录",
                    link: "loginOut"
                }
            ];
        }
    },
 watch: {
        // 一旦路径发生变化，页面对应的菜单被激活，主要用于返回页面
        $route() {
            sessionStorage.setItem("activePath", this.$route.path);
            this.activePath = this.$route.path;
            this.activeSpan = this.$route.path.split("/")[2];
            if (sessionStorage.getItem("token")) {
                this.meneData = [
                    {
                        key: "1",
                        name: "开户",
                        link: "openAcount"
                    },
                    {
                        key: "2",
                        name: "开票",
                        link: "billing"
                    },
                    {
                        key: "3",
                        name: "转账",
                        link: "transferAccounts"
                    },
                    {
                        key: "4",
                        name: "个人中心",
                        link: "personal"
                    },
                    {
                        key: "5",
                        name: "退出登录",
                        link: "loginOut"
                    }
                ];
            }
        }
    }
};
</script>



<style lang='less' scoped>
.home {
    .header {
        height: 80px;
        border-bottom: 1px solid #e6e6e6;
        padding: 0 50px;
        .logo {
            float: left;
            margin-top: 20px;
        }
        .memu {
            float: right;
            span {
                display: inline-block;
                margin-top: 30px;
                margin-right: 40px;
                &.active {
                    font-weight: bold;
                }
            }
        }
    }
    .banner {
        height: 370px;
        img {
            float: right;
        }
    }
}
</style>
