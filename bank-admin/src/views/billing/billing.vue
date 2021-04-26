<template>
    <div class="billing">
        <div class="billing_tab">
            <span
                class="span mine"
                :class="activeSpan=='mine'?'active':''"
                @click="activeSpan='mine'"
            >我的开票</span>
            <span
                class="span mine_second"
                :class="activeSpan=='mineSecond'?'active':''"
                @click="activeSpan='mineSecond'"
            >我的收票</span>
        </div>
        <div class="table_wrap">
            <span
                class="span active"
                style="padding:0 30px;height:40px;line-height:40px"
                @click="BuilddialogVisible=true"
            >新建发票</span>
            <el-table :data="tableData" border style="width: 100%" v-if="activeSpan == 'mine'">
                <el-table-column prop="date" label="日期"></el-table-column>
                <el-table-column prop="name" label="金额"></el-table-column>
                <el-table-column prop="name" label="开票人账户"></el-table-column>
                <el-table-column prop="name" label="截至时间"></el-table-column>
                <el-table-column prop="name" label="操作">
                    <template slot-scope="scope">
                        <el-button @click="pay(scope.row)" type="text" size="small">支付</el-button>
                        <el-button type="text" size="small" @click="cancel()">取消</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-table :data="tableData1" border style="width: 100%" v-else>
                <el-table-column prop="date" label="日期"></el-table-column>
                <el-table-column prop="name" label="金额"></el-table-column>
                <el-table-column prop="name" label="开票人"></el-table-column>
                <el-table-column prop="name" label="问题"></el-table-column>
                <el-table-column prop="name" label="操作">
                    <template slot-scope="scope">
                        <el-button @click="anwser(scope.row)" type="text" size="small">确认收票</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
         <el-dialog title="提示" :visible.sync="dialogVisible" width="30%">
            <div style="text-align:center;padding:0 50px">
                <span>问题：您的姓名是什么？</span>
                <el-input style="margin-top:20px" placeholder="输入答案" v-model="answerContent"></el-input>
            </div>

            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="anwserOk()">确 定</el-button>
            </span>
        </el-dialog>
        <el-dialog title="提示" :visible.sync="BuilddialogVisible" width="600px">
            <el-form
                :model="dynamicValidateForm"
                ref="dynamicValidateForm"
                label-width="100px"
                class="demo-dynamic"
                style="padding:0 20px"
            >
                <el-form-item
                    prop="money"
                    label="开票金额"
                    :rules="[
                            { required: true, message: '请输入开票金额', trigger: ['blur','change'] },
                            ]"
                >
                    <el-input
                        style="width:100%"
                        v-model="dynamicValidateForm.money"
                        placeholder="请输入开票金额"
                    ></el-input>
                </el-form-item>
                <el-form-item
                    prop="time"
                    label="开票时间"
                    :rules="[
                            { required: true, message: '请选择开票时间', trigger: ['blur','change'] },
                            ]"
                >
                    <el-date-picker
                        style="width:100%"
                        v-model="dynamicValidateForm.time"
                        type="datetime"
                        placeholder="请选择开票时间"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item
                    prop="account"
                    label="开票账户"
                    :rules="[
                            { required: true, message: '请输入转账账户', trigger: ['blur','change'] },
                            ]"
                >
                    <el-input
                        style="width:100%"
                        v-model="dynamicValidateForm.account"
                        placeholder="请输入转账账户"
                    ></el-input>
                </el-form-item>
                <!-- <el-form-item
                        prop="account"
                        label="设置问题"
                        :rules="[
                            { required: true, message: '请设置问题', trigger: ['blur','change'] },
                            ]"
                    >
                        <el-input
                            style="width:100%"
                            v-model="dynamicValidateForm.account"
                            placeholder="请设置问题"
                        ></el-input>
                    </el-form-item>
                    <el-form-item
                        prop="account"
                        label="设置答案"
                        :rules="[
                            { required: true, message: '请设置答案', trigger: ['blur','change'] },
                            ]"
                    >
                        <el-input
                            style="width:100%"
                            v-model="dynamicValidateForm.account"
                            placeholder="请设置答案"
                        ></el-input>
                </el-form-item>-->
                <el-form-item
                    prop="email"
                    label="收款人邮箱"
                    :rules="[
                            { required: true, message: '请输入收款人邮箱', trigger: ['blur','change']},
                            { type: 'email', message: '请输入正确的邮箱', trigger: ['blur','change'] },
                            ]"
                >
                    <el-input
                        style="width:100%"
                        v-model="dynamicValidateForm.email"
                        placeholder="请输入收款人邮箱"
                    ></el-input>
                </el-form-item>
                <div style="margin-top:30px;text-align:center">
                    <el-button @click="BuilddialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="buildSubmit()">确 定</el-button>
                </div>
            </el-form>
        </el-dialog>
    </div>
</template>
<script>
export default {
    data() {
        return {
            dynamicValidateForm: {},
            BuilddialogVisible: false,
            dialogVisible: false,
            activeSpan: "mine",
            answerContent: "",
            tableData: [
                {
                    date: "2016-05-02",
                    name: "王小虎",
                    address: "上海市普陀区金沙江路 1518 弄"
                },
                {
                    date: "2016-05-04",
                    name: "王小虎",
                    address: "上海市普陀区金沙江路 1517 弄"
                },
                {
                    date: "2016-05-01",
                    name: "王小虎",
                    address: "上海市普陀区金沙江路 1519 弄"
                },
                {
                    date: "2016-05-03",
                    name: "王小虎",
                    address: "上海市普陀区金沙江路 1516 弄"
                }
            ],
            tableData1: [
                {
                    date: "2016-05-02",
                    name: "王小虎",
                    address: "上海市普陀区金沙江路 1516 弄"
                },
                {
                    date: "2016-05-04",
                    name: "王小虎",
                    address: "上海市普陀区金沙江路 1516 弄"
                }
            ]
        };
    },
    methods: {
        buildSubmit() {
            this.$refs.dynamicValidateForm.validate(valid => {
                if (valid) {
                    alert("submit!");
                } else {
                    console.log("error submit!!");
                    return false;
                }
            });
        },
        pay() {
            this.$confirm("您的账户余额为100，确认支付吗?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
                center: true
            })
                .then(() => {
                    this.$message({
                        type: "success",
                        message: "确认支付!"
                    });
                })
                .catch(() => {});
        },
        cancel() {
            this.$confirm("确认取消发票吗?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
                center: true
            })
                .then(() => {
                    this.$message({
                        type: "success",
                        message: "确认取消!"
                    });
                })
                .catch(() => {});
        },
        anwser() {
            this.dialogVisible = true;
        }
    }
};
</script>
<style lang="less">
.billing {
    tr,
    td,
    th {
        text-align: center;
    }
    .span {
        display: inline-block;
        padding: 0 50px;
        height: 60px;
        text-align: center;
        line-height: 60px;

        border: 1px solid #cacaca;
        border-radius: 6px;
        &.mine {
            border-top-right-radius: 0;
            border-bottom-right-radius: 0;
            border-right: 0;
        }
        &.mine_second {
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;
            border-left: 0;
        }
        &.active {
            background: #00324d !important;
            color: #fff;
        }
    }
    .billing_tab {
        text-align: center;
    }
    .table_wrap {
        padding: 0 70px;
    }
}
</style>