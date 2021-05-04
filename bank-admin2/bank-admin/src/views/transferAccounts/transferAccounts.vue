<template>
  <div class="transferAccounts">
    <div class="transferAccounts_tab">
      <span
        class="span mine"
        :class="activeSpan == 'mine' ? 'active' : ''"
        @click="activeSpan = 'mine'"
        >我要转账</span
      >
      <span
        class="span mine_second"
        :class="activeSpan == 'mineSecond' ? 'active' : ''"
        @click="activeSpan = 'mineSecond'"
        >收到转账</span
      >
    </div>
    <div class="table_wrap">
      <div
        style="width: 600px; margin: 0 auto; margin-top: 20px"
        v-if="activeSpan === 'mine'"
      >
        <el-form
          :model="dynamicValidateForm"
          ref="dynamicValidateForm"
          label-width="140px"
          class="demo-dynamic"
        >
          <el-form-item
            prop="timeType"
            label="transfer type"
            :rules="[
              {
                required: true,
                message: 'transfer type',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-select
              style="width: 100%"
              v-model="dynamicValidateForm.timeType"
              placeholder="Please select the transfer type"
            >
              <el-option label="Transfer now" value="1"></el-option>
              <el-option label="Time transfer" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="dynamicValidateForm.timeType == '2'"
            prop="time"
            label="转账时间"
            :rules="[
              {
                required: true,
                message: '请选择转账时间',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-date-picker
              style="width: 100%"
              v-model="dynamicValidateForm.time"
              type="datetime"
              placeholder="选择日期时间"
            ></el-date-picker>
          </el-form-item>
          <el-form-item
            prop="accountType"
            label="Transfer account type"
            :rules="[
              {
                required: true,
                message: '请选择转账账户类型',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-select
              style="width: 100%"
              v-model="dynamicValidateForm.accountType"
              placeholder="select the transfer account type"
            >
              <el-option label="Check" value="1"></el-option>
              <el-option label="saving" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            prop="money"
            label="转账金额"
            :rules="[
              {
                required: true,
                message: '请输入转账金额',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-input
              style="width: 100%"
              v-model="dynamicValidateForm.money"
              placeholder="Please enter the transfer amount"
            ></el-input>
          </el-form-item>
          <el-form-item
            prop="transactionAccountType"
            label="Payee receiving mode"
            :rules="[
              {
                required: true,
                message: 'Please select the recipient receiving method',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-select
              style="width: 100%"
              v-model="dynamicValidateForm.transactionAccountType"
              placeholder="Please select the recipient receiving method"
            >
              <el-option label="check" value="1"></el-option>
              <el-option label="savings" value="2"></el-option>
              <el-option label="credit card" value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            prop="transactionCardNum"
            label="Transfer account"
            :rules="[
              {
                required: true,
                message: 'Please enter the transfer account',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-input
              style="width: 100%"
              v-model="dynamicValidateForm.transactionCardNum"
              placeholder="Please enter the transfer account"
            ></el-input>
          </el-form-item>
          <el-form-item
            prop="question"
            label="set the question"
            :rules="[
              {
                required: true,
                message: 'set the question',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-input
              style="width: 100%"
              v-model="dynamicValidateForm.question"
              placeholder="set the question"
            ></el-input>
          </el-form-item>
          <el-form-item
            prop="answer"
            label="set the answer"
            :rules="[
              {
                required: true,
                message: 'set the answer',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-input
              style="width: 100%"
              v-model="dynamicValidateForm.answer"
              placeholder="请设置答案"
            ></el-input>
          </el-form-item>
          <el-form-item
            prop="email"
            label="通知邮箱"
            :rules="[
              {
                required: true,
                message: '请输入通知邮箱',
                trigger: ['blur', 'change'],
              },
              {
                type: 'email',
                message: '请输入正确的通知邮箱',
                trigger: ['blur', 'change'],
              },
            ]"
          >
            <el-input
              style="width: 100%"
              v-model="dynamicValidateForm.email"
              placeholder="请设置答案"
            ></el-input>
          </el-form-item>
          <div style="margin-top: 30px; text-align: center; padding-left: 100px">
            <el-button type="primary" @click="submit()">提交</el-button>
          </div>
        </el-form>
      </div>
      <el-table :data="tableData1" border style="width: 100%" v-else>
        <el-table-column prop="createTime" label="日期"></el-table-column>
            <el-table-column prop="money" label="金额"></el-table-column>
         <el-table-column prop="accountType" label="转账账户类型">
             <template slot-scope="scope">
                            <span v-if="scope.row.accountType == '1'">支票</span>
                            <span v-if="scope.row.accountType == '2'">储蓄</span>
                            <span v-if="scope.row.accountType == '3'">信用卡</span>
                        </template>
        </el-table-column>
        <el-table-column prop="transactionAccountType" label="收款账户方式">
          <template slot-scope="scope">
            <span v-if="scope.row.transactionAccountType == '1'">支票</span>
            <span v-if="scope.row.transactionAccountType == '2'">储蓄卡</span>
            <span v-if="scope.row.transactionAccountType == '3'">信用卡</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="交易状态">
          <template slot-scope="scope">
            <span v-if="scope.row.status == '0'">创建成功</span>
            <span v-if="scope.row.status == '1'">已取消</span>
            <span v-if="scope.row.status == '2'">开票中</span>
            <span v-if="scope.row.status == '3'">开票成功</span>
          </template>
        </el-table-column>
      
        <el-table-column prop="name" label="操作">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status == '2'" @click="anwser(scope.row)" type="text" size="small"
              >回答问题</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:right" v-if="activeSpan !== 'mine'">
                    <el-pagination background  @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page.sync="pageData.currentPage"
      :page-size="pageData.pageSize" layout="prev, pager, next" :total="pageData.total"></el-pagination>
                </div> 
    </div>
    <el-dialog title="提示" :visible.sync="dialogVisible" width="30%">
      <div style="text-align: center; padding: 0 50px">
        <span>问题：{{qusetion}}</span>
        <el-input
          style="margin-top: 20px"
          placeholder="输入答案"
          v-model="answerContent"
        ></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false;answerContent=null">取 消</el-button>
        <el-button type="primary" @click="anwserOk()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { formatTimeToStr } from "../../utils/utils.js";
export default {
  data() {
    return {
      dialogVisible: false,
      activeSpan: "mine",
      answerContent: "",
      qusetion:'',
      dynamicValidateForm: {
        email: "",
      },
       pageData:{
                total:100,
                pageSize:5,
                currentPage:1

            },
      tableData: [
        {
          date: "2016-05-02",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1518 弄",
        },
        {
          date: "2016-05-04",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1517 弄",
        },
        {
          date: "2016-05-01",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1519 弄",
        },
        {
          date: "2016-05-03",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1516 弄",
        },
      ],
      tableData1: [
        {
          date: "2016-05-02",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1516 弄",
        },
        {
          date: "2016-05-04",
          name: "王小虎",
          address: "上海市普陀区金沙江路 1516 弄",
        },
      ],
      transactionId:''
    };
  },
  created(){
    this.gettransactionInQuery()
  },
  methods: {
    gettransactionInQuery(){
      this.pageData.transactionType='1'
      this.$axios1.post("/account/transaction/transactionInQuery", this.pageData).then(res => {
                console.log(res)
               if(res.code == 'T'){
                   this.tableData1 = res.data.content
                   this.pageData.total = res.data.totalElements
                   
               }else {
                        this.$message.error(res.msg);
                    }
            });

    },
     handleSizeChange(value){
            console.log(value)
            this.pageData.pageSize =value
            this.gettransactionInQuery()
        },
        handleCurrentChange(value){
           this.pageData.currentPage =value
            this.gettransactionInQuery()

        },
    pay() {
      this.$confirm("您的账户余额为100，确认支付吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
      })
        .then(() => {
          this.$message({
            type: "success",
            message: "确认支付!",
          });
        })
        .catch(() => {});
    },
    cancel() {
      this.$confirm("确认取消发票吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
      })
        .then(() => {
          this.$message({
            type: "success",
            message: "确认取消!",
          });
        })
        .catch(() => {});
    },
    anwserOk(){
      if(!this.answerContent){
         this.$message.error('请输入答案！');
         return;
      }
      this.$axios1
            .post("/account/transaction/transactionConfirm", {transactionId:this.transactionId,answer:this.answerContent})
            .then(res=>{
              console.log(res)
              if(res.code == "T"){
                 this.$message.success('答案正确，转账成功');
                  this.dialogVisible = false;
                  this.gettransactionInQuery()
              }else{
                this.$message.error(res.msg);
              }
             
            })
    },
    anwser(row) {
      console.log(row)
      
      this.$axios1
            .post("/account/question/questionQuery", {transactionId:row.id})
            .then(res=>{
              console.log(res)
              if(res.code == "T"){
                 this.qusetion = res.data.question
                 this.transactionId = res.data.transactionId
                 this.dialogVisible = true;
              }else{
                this.$message.error(res.msg);
              }
             
            })
    },
    submit() {
      this.$refs.dynamicValidateForm.validate((valid) => {
        if (valid) {
          console.log(this.dynamicValidateForm);
          this.dynamicValidateForm.money = +this.dynamicValidateForm.money;
          this.dynamicValidateForm.transactionType = "1";
            this.dynamicValidateForm.time =this.dynamicValidateForm.time? formatTimeToStr(
            this.dynamicValidateForm.time,
            "yyyy-MM-dd hh:mm:ss"
          ):'';
          this.$axios1
            .post("/account/transaction/transaction", this.dynamicValidateForm)
            .then((res) => {
              console.log(res);
              console.log(res.code == "T");
              if (res.code == "T") {
                this.$confirm(
                  "确认要给" +
                    res.data.name +
                    "转账" +
                    res.data.money +
                    "(其中服务费为" +
                    res.data.serviceFee +
                    ")元吗？",
                  "提示",
                  {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning",
                  }
                )
                  .then(() => {
                    this.$axios1
                      .post("/account/transaction/transactionTwo", res.data)
                      .then((res) => {
                        if (res.code == "T") {
                          this.$message({
                            type: "success",
                            message: "转账成功!",
                          });
                          this.$refs.dynamicValidateForm.resetFields();
                          this.gettransactionInQuery()
                        }
                         
                      });
                  })
                  .catch(() => {
                    this.$message({
                      type: "info",
                      message: "取消转账",
                    });
                  });
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
  },
};
</script>
<style lang="less">
.transferAccounts {
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
  .transferAccounts_tab {
    text-align: center;
  }
  .table_wrap {
    padding: 0 70px;
  }
}
</style>