<template>
    <div class="card_content">
        <div  v-if="!carddialogVisible" style="display:flex;justify-content: space-around;width:800px">
             <div
            class="student"
            v-for="(item,index) in cardInfo"
            :key="index"
            @click="openCard(item.cardType)"
        >
            <p>{{item.cardName}}</p>
            <div class="moutn_money">
                每月仅
                <span>{{item.cost}}</span>元
            </div>
            <div class="free">
                可免费转账
                <span>{{item.freeNum}}</span>次
            </div>
            <div style="text-align:right;font-size:12px;margin-top:40px;float:right">
                没有免费转账次数时,费用仅需
                <span style="color:red">{{item.rate}}</span>
            </div>
        </div>

        </div>
       
        <el-dialog title="提示" style="text-aligh:center" :visible.sync="dialogVisible" width="600px">
            <el-form
                :model="dynamicValidateForm"
                ref="dynamicValidateForm"
                label-width="80px"
                class="demo-dynamic"
                :destroy-on-close="'true'"
                :rules="rules"
                style="padding:0 29px;"
            >
                <el-form-item prop="email" label="邮箱">
                    <el-input
                        style="width:100%"
                        v-model="dynamicValidateForm.email"
                        placeholder="请输入邮箱"
                    ></el-input>
                </el-form-item>
                <el-form-item prop="name" label="姓名">
                    <el-input
                        style="width:100%"
                        v-model="dynamicValidateForm.name"
                        placeholder="请输入姓名"
                    ></el-input>
                </el-form-item>
                <el-form-item prop="phone" label="手机号">
                    <el-input
                        style="width:100%"
                        v-model="dynamicValidateForm.phone"
                        placeholder="请输入电话"
                    ></el-input>
                </el-form-item>
                <el-form-item prop="pwd" label="密码">
                    <el-input
                        style="width:100%"
                        v-model="dynamicValidateForm.pwd"
                        placeholder="请输入密码"
                        type="password"
                    ></el-input>
                </el-form-item>
            </el-form>
            <div style="margin-top:30px;text-align:center">
                <el-button @click="cancelDialog()">取 消</el-button>
                <el-button type="primary" @click="submit()">确 定</el-button>
            </div>
        </el-dialog>
        <div style="text-align:center" v-if="carddialogVisible">
              <p style="font-size:24px">开卡成功</p>
             <div style="margin-top:30px">这是您的卡号，请谨慎保存：<span style="font-size:24px;color:red">{{cardNum}}</span></div>
              <div style="margin-top:30px;text-align:center">
                  <el-button type="primary" @click="$router.push('/home/login')">去登录</el-button>
              </div>
        </div>
    </div>
</template>
<script>
export default {
    data() {
        var phoneTest = (rule, value, callback) => {
            if (value === "") {
                callback(new Error("请输入手机号"));
            } else {
                if (!/^1[3456789]\d{9}$/.test(value)) {
                    callback(new Error("请输入正确的手机号"));
                }
                callback();
            }
        };
         var pwdTest = (rule, value, callback) => {
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
        };
        return {
            cardNum:'',
            carddialogVisible:false,
            cardInfo: [],
            dynamicValidateForm: {},
            dialogVisible: false,
            cardType: "1",
            rules: {
                email: [
                    {
                        required: true,
                        message: "请输入邮箱",
                        trigger: ["blur", "change"]
                    },
                    {
                        type: "email",
                        message: "请输入正确的邮箱",
                        trigger: ["blur", "change"]
                    }
                ],
                name: [
                    {
                        required: true,
                        message: "请输入姓名",
                        
                        trigger: ["blur", "change"]
                    }
                ],
                phone: [{ required: true,validator: phoneTest, trigger: ["blur", "change"] }],
                pwd: [
                   { required: true,validator: pwdTest, trigger: ["blur", "change"] }
                ]
            }
        };
    },
        methods: {
        openCard(type) {
            console.log(type);
            this.dialogVisible = true;
            this.cardType = type;
        },
        cancelDialog() {
            this.dialogVisible = false;
            this.$refs.dynamicValidateForm.resetFields();
        },
        submit() {
            this.$refs.dynamicValidateForm.validate(valid => {
                if (valid) {
                    this.dynamicValidateForm.cardType = this.cardType;
                    console.log(this.dynamicValidateForm);
                    this.$axios1
                        .post(
                            "/account/userAccount/ignore/userAccountReg",
                            this.dynamicValidateForm
                        )
                        .then(res => {
                            console.log(res);
                            if (res.code == "T") {
                                this.carddialogVisible = true
                                this.dialogVisible = false
                                this.cardNum = res.data.cardNum
                               
                            } else {
                                this.$message.error(res.msg);
                            }
                        });
                } else {
                    console.log("error submit!!");
                    return false;
                }
            });
        },
        GetcarInfo() {
            this.$axios1
                .post("/account/card/ignore/query", { data: 1 })
                .then(res => {
                    console.log(res);
                    console.log(res.code == "T")
                    if (res.code == "T") {
                        this.cardInfo = res.data;
                    } else {
                        this.$message.error(res.msg);
                    }
                });
        }
    },
    created() {
        this.GetcarInfo();
    }
};
</script>
<style lang="less">
.card_content {
    padding: 0 450px;
    .student,
    .person {
        background: #f3f2ed;
        width: 300px;
        height: 400px;
        border: 1px solid #e6e6e6;
        text-align: center;
        padding: 20px;
        box-sizing: border-box;
        p {
            font-size: 36px;
        }
        .moutn_money {
            margin-top: 80px;
            font-size: 20px;
            span {
                font-size: 30px;
                color: red;
            }
        }
        .free {
            margin-top: 100px;
            font-size: 20px;
            span {
                font-size: 30px;
                color: red;
            }
        }
    }
}
</style>