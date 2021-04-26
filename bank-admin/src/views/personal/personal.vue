<template>
    <div class="personal">
        <div class="personal_tab">
            <span
                class="span mine"
                :class="activeSpan=='mine'?'active':''"
                @click="activeSpan='mine'"
            >我的留言</span>
            <span
                class="span mine_second"
                :class="activeSpan=='mineSecond'?'active':''"
                @click="activeSpan='mineSecond'"
            >我的账户</span>
        </div>
        <div class="table_wrap">
            <el-table :data="tableData" border style="width: 100%" v-if="activeSpan == 'mine'">
                <el-table-column prop="date" label="日期"></el-table-column>
                <el-table-column prop="name" label="留言人"></el-table-column>
                <el-table-column prop="name" label="留言内容"></el-table-column>
                <el-table-column prop="name" label="操作">
                    <template slot-scope="scope">
                        <el-button @click="pay(scope.row)" type="text" size="small">查看留言</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div v-else>
                <div>
                    <div class="acount_total">
                        账户总余额：
                        <span class="number">100000</span>
                    </div>
                    <div style="margin-top:20px">
                        <span class="other_span">
                            支票：
                            <span class="number">20000</span>
                        </span>
                        <span class="other_span">
                            储蓄：
                            <span class="number">20000</span>
                        </span>
                        <span class="other_span">
                            信用卡：
                            <span class="number">20000</span>
                        </span>
                    </div>
                </div>
                <el-table :data="tableData" border style="width: 100%">
                    <el-table-column prop="date" label="日期"></el-table-column>
                    <el-table-column prop="name" label="方式"></el-table-column>
                    <el-table-column prop="name" label="账户类型"></el-table-column>
                    <el-table-column prop="name" label="金额"></el-table-column>
                    <el-table-column prop="name" label="收支记录"></el-table-column>
                    <el-table-column prop="name" label="操作">
                        <template slot-scope="scope">
                            <el-button @click="pay(scope.row)" type="text" size="small">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div style="text-align:right">
                    <el-pagination background  @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page.sync="pageData.currentPage"
      :page-size="pageData.pageSize" layout="prev, pager, next" :total="pageData.total"></el-pagination>
                </div>
            </div>
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
    </div>
</template>
<script>
export default {
    data() {
        return {
            dialogVisible: false,
            activeSpan: "mine",
            answerContent: "",
            pageData:{
                total:100,
                pageSize:5,
                currentPage:1

            },
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
        handleSizeChange(value){
            console.log(value)
        },
        handleCurrentChange(value){
            console.log(value)

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
        },
        getAcountInfo() {
            this.$axios1.post("/account/account/accountQuery", {}).then(res => {
                console.log(res);
            });
        },
        getInfo() {
            this.$axios1.post("/account/bill/billQuery", this.pageData).then(res => {
                console.log(res);
            });
        }
    },
    created() {
        // this.getInfo();
        this.getAcountInfo();
    }
};
</script>
<style lang="less">
.personal {
    tr,
    td,
    th {
        text-align: center;
    }
    .acount_total {
        font-size: 26px;
        span {
            font-size: 26px;
        }
    }
    .other_span {
        font-size: 16px;
        margin-right: 30px;
    }
    .number {
        color: red;
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
    .personal_tab {
        text-align: center;
    }
    .table_wrap {
        padding: 0 70px;
    }
}
</style>
