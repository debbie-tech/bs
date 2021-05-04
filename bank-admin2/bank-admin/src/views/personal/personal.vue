<template>
    <div class="personal">
        <div class="personal_tab">
            <!-- <span
                class="span mine"
                :class="activeSpan=='mine'?'active':''"
                @click="activeSpan='mine'"
            >我的留言</span> -->
            <span
                class="span mine_second active"
                @click="activeSpan='mineSecond'"
            >我的账户</span>
        </div>
        <div class="table_wrap">
            <el-table :data="tableData" border style="width: 100%" v-if="activeSpan == 'mine'">
                <el-table-column prop="date" label="date"></el-table-column>
                <el-table-column prop="name" label="name"></el-table-column>
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
                      Total account balance：
                        <span class="number">{{billing+saveCard+postCard}}</span>
                    </div>
                    <div style="margin-top:20px">
                        <span class="other_span">
                            支票：
                            <span class="number">{{billing}}  (冻结金额{{billingFreese}})</span>
                        </span>
                        <span class="other_span">
                            储蓄：
                            <span class="number">{{saveCard}}   (冻结金额{{saveCardFreese}})</span>
                        </span>
                        <span class="other_span">
                            信用卡：
                            <span class="number">{{postCard}}   (冻结金额{{postCardFreese}})</span>
                        </span>
                    </div>
                </div>
                <el-table :data="tableData1" border style="width: 100%">
                    <el-table-column prop="createTime" label="日期"></el-table-column>
                    <el-table-column prop="type" label="收支记录">
                        <template slot-scope="scope">
                            {{scope.row.type == '1'?'收入':'支出'}} 
                        </template>
                    </el-table-column>
                    <el-table-column prop="accountType" label="账户类型">
                         <template slot-scope="scope">
                            <span v-if="scope.row.accountType == '1'">支票</span>
                            <span v-if="scope.row.accountType == '2'">储蓄</span>
                            <span v-if="scope.row.accountType == '3'">信用卡</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="money" label="金额"></el-table-column>
                     <el-table-column prop="status" label="交易状态">
          <template slot-scope="scope">
            <span v-if="scope.row.status == '0'">创建成功</span>
            <span v-if="scope.row.status == '1'">已取消</span>
            <span v-if="scope.row.status == '2'">交易中</span>
            <span v-if="scope.row.status == '3'">交易成功</span>
          </template>
        </el-table-column>
                    <!-- <el-table-column prop="name" label="操作">
                        <template slot-scope="scope">
                            <el-button @click="pay(scope.row)" type="text" size="small">删除</el-button>
                        </template>
                    </el-table-column> -->
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
            billing:'',
            billingFreese:0,
            saveCard:'',
            saveCardFreese:0,
            postCard:'',
            postCardFreese:0,
            dialogVisible: false,
            activeSpan: "mineSecond",
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
            this.pageData.pageSize =value
            this.getInfo()
        },
        handleCurrentChange(value){
           this.pageData.currentPage =value
            this.getInfo()

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
                if (res.code == "T") {
                        this.cardInfo = res.data;
                         res.data.map((item,index)=>{
                             if(item.type == '1'){
                                this.billing = item.balance
                                this.billingFreese = item.frozenMoney
                             }else if(item.type == '2'){
                                this.saveCard = item.balance
                                this.saveCardFreese = item.frozenMoney
                             }else{
                                this.postCard = item.balance
                                this.postCardFreese = item.frozenMoney
                             }
                             })
                    } else {
                        this.$message.error(res.msg);
                    }
            });
        },
        getInfo() {
            this.$axios1.post("/account/bill/billQuery", this.pageData).then(res => {
                console.log(res)
               if(res.code == 'T'){
                   this.tableData1 = res.data.content
                   this.pageData.total = res.data.totalElements
                   
               }else {
                        this.$message.error(res.msg);
                    }
            });
        }
    },
    created() {
        this.getInfo();
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
