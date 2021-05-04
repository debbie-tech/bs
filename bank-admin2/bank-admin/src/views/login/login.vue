<template>
    <div class="login">
        <el-form
            :model="dynamicValidateForm"
            ref="dynamicValidateForm"
            label-width="100px"
            class="demo-dynamic"
            :rules="rules"
        >
            <el-form-item
                prop="cardNum"
                label="账号"
            
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
               
            >
                <el-input
                    style="width:100%"
                    v-model="dynamicValidateForm.pwd"
                    placeholder="请输入密码"
                     type="password"
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
        var pwdTest=(rule, value, callback) => {
            if (value === "") {
                callback(new Error("请输入密码"));
            }else if(value.length<5||value.length>15){
                callback(new Error("密码长长度为5-15位"));
            }else {
                if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{5,15}$/.test(value)) {
                    callback(new Error("密码格式为字母加数字"));
                }
                callback();
            }
        }
        return {
            dynamicValidateForm: {},
                rules: {
                cardNum: [
                    {
                        required: true,
                        message: "请输入账号",
                        
                        trigger: ["blur", "change"]
                    }
                ],
              
                pwd: [
                   { required: true,validator: pwdTest, trigger: ["blur", "change"] }
                ]
            },
            
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