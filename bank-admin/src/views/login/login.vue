<template>
    <div class="login">
        <el-form
            :model="dynamicValidateForm"
            ref="dynamicValidateForm"
            label-width="100px"
            class="demo-dynamic"
        >
            <el-form-item
                prop="cardNum"
                label="账号"
                :rules="[
                            { required: true, message: '请输入账号', trigger: ['blur','change'] },
                            ]"
            >
                <el-input
                    style="width:100%"
                    v-model="dynamicValidateForm.cardNum"
                    placeholder="请输入账号"
                ></el-input>
            </el-form-item>
            <el-form-item
                prop="pwd"
                label="密码"
                :rules="[
                            { required: true, message: '请输入密码', trigger: ['blur','change'] },
                            ]"
            >
                <el-input
                    style="width:100%"
                    v-model="dynamicValidateForm.pwd"
                    placeholder="请输入密码"
                ></el-input>
            </el-form-item>
            <div style="margin-top:30px;text-align:center;padding-left:80px">
                <el-button type="primary" @click="submit()">登录</el-button>
            </div>
        </el-form>
    </div>
</template>
<script>
export default {
    data() {
        return {
            dynamicValidateForm: {}
        };
    },
    methods: {
        submit() {
            this.$refs.dynamicValidateForm.validate(valid => {
                if (valid) {
                      this.$axios1
                        .post(
                            "/account/userAccount/ignore/login",
                            this.dynamicValidateForm
                        )
                        .then(res=>{
                            console.log(res)
                            if(res.code == 'T'){
                                sessionStorage.setItem('token',res.data.token)
                                this.$router.push('/home/personal')
                            }else{
                                this.$message.error(res.msg);
                            }
                        })
                } else {
                    console.log("error submit!!");
                    return false;
                }
            });
        }
    }
};
</script>
<style lang="less">
.login{
    width:600px;
    margin:0 auto;
    padding-top:100px;
}

</style>