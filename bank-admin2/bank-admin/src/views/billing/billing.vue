<template>
  <div class="billing" v-if="showPage">
    <div class="billing_tab">
      <span
        class="span mine"
        :class="activeSpan == 'mine' ? 'active' : ''"
        @click="activeSpan = 'mine';changeShowPage()"
        >我的开票</span
      >
      <span
        class="span mine_second"
        :class="activeSpan == 'mineSecond' ? 'active' : ''"
        @click="activeSpan = 'mineSecond';changeShowPage()"
        >我的收票</span
      >
    </div>
    <div class="table_wrap">
      <span
        class="span active"
        style="padding: 0 30px; height: 40px; line-height: 40px"
        @click="BuilddialogVisible = true"
        >新建发票</span
      >
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-if="activeSpan == 'mine'"
      >
        <el-table-column prop="createTime" label="日期"></el-table-column>
        <el-table-column prop="money" label="金额"></el-table-column>
        <el-table-column
          prop="transactionCardNum"
          label="开票人账户"
        ></el-table-column>
        <el-table-column prop="time" label="截至时间"> </el-table-column>
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
            <el-button  v-if="scope.row.status =='2'"  @click="pay(scope.row)" type="text" size="small"
              >支付</el-button
            >
            <el-button  v-if="scope.row.status =='0'" type="text" size="small" @click="cancel(scope.row)"
              >取消</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align: right" v-if="activeSpan == 'mine'">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page.sync="pageData.currentPage"
          :page-size="pageData.pageSize"
          layout="prev, pager, next"
          :total="pageData.total"
        ></el-pagination>
      </div>
      <el-table :data="tableData1" border style="width: 100%" v-else>
        <el-table-column prop="createTime" label="日期"></el-table-column>
        <el-table-column prop="money" label="金额"></el-table-column>
        <el-table-column prop="accountType" label="开票方式">
          <template slot-scope="scope">
            <span v-if="scope.row.accountType == '1'">支票</span>
            <span v-if="scope.row.accountType == '2'">储蓄卡</span>
          </template>
        </el-table-column>
        <el-table-column prop="transactionAccountType" label="收票方式">
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
          <template slot-scope="scope" >
            <el-button v-if="scope.row.status =='0'" @click="getBilling(scope.row)" type="text" size="small"
              >确认收票</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align: right" v-if="activeSpan !== 'mine'">
        <el-pagination
          background
          @size-change="handleSizeChange1"
          @current-change="handleCurrentChange1"
          :current-page.sync="pageData1.currentPage"
          :page-size="pageData1.pageSize"
          layout="prev, pager, next"
          :total="pageData1.total"
        ></el-pagination>
      </div>
    </div>
    <el-dialog title="提示" :visible.sync="dialogVisible" width="30%">
      <div style="text-align: center; padding: 0 50px">
        <span>问题：您的姓名是什么？</span>
        <el-input
          style="margin-top: 20px"
          placeholder="输入答案"
          v-model="answerContent"
        ></el-input>
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
        label-width="140px"
        class="demo-dynamic"
        style="padding: 0 20px"
      >
        <el-form-item
          prop="money"
          label="开票金额"
          :rules="[
            {
              required: true,
              message: '请输入开票金额',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-input
            style="width: 100%"
            v-model="dynamicValidateForm.money"
            placeholder="请输入开票金额"
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="accountType"
          label="Please select billing method"
          :rules="[
            {
              required: true,
              message: '请选择开票方式',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-select
            style="width: 100%"
            v-model="dynamicValidateForm.accountType"
            placeholder="billing"
          >
            <el-option label="check" value="1"></el-option>
            <el-option label="savings" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="time"
          label="Date"
          :rules="[
            {
              required: true,
              message: '请选择开票时间',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-date-picker
            style="width: 100%"
            v-model="dynamicValidateForm.time"
            type="datetime"
            placeholder="Please select the billing time"
          ></el-date-picker>
        </el-form-item>
        <el-form-item
          prop="transactionCardNum"
          label="billing account"
          :rules="[
            {
              required: true,
              message: '请输入开票账户',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-input
            style="width: 100%"
            v-model="dynamicValidateForm.transactionCardNum"
            placeholder="请输入开票账户"
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="transactionAccountType"
          label="收票人接收方式"
          :rules="[
            {
              required: true,
              message: '请选择收票人接收方式',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-select
            style="width: 100%"
            v-model="dynamicValidateForm.transactionAccountType"
            placeholder="收票人接收方式"
          >
            <el-option label="check" value="1"></el-option>
            <el-option label="savings" value="2"></el-option>
          </el-select>
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
            {
              required: true,
              message: '请输入收款人邮箱',
              trigger: ['blur', 'change'],
            },
            {
              type: 'email',
              message: '请输入正确的邮箱',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-input
            style="width: 100%"
            v-model="dynamicValidateForm.email"
            placeholder="请输入收款人邮箱"
          ></el-input>
        </el-form-item>
        <div style="margin-top: 30px; text-align: center; padding-left: 40px">
          <el-button @click="BuilddialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="buildSubmit()">确 定</el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { formatTimeToStr } from "../../utils/utils.js";
export default {
  data() {
    return {
      dynamicValidateForm: {},
      BuilddialogVisible: false,
      dialogVisible: false,
      showPage:true,
      activeSpan: "mine",
      answerContent: "",
      pageData: {
        total: 100,
        pageSize: 5,
        currentPage: 1,
      },
      pageData1: {
        total: 100,
        pageSize: 5,
        currentPage: 1,
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
    };
  },
  created() {
    this.gettransactionFp();
    this.getMyBilling();
  },
  methods: {
    getBilling(row){
      console.log(row)
       this.$confirm(
        "确认收票吗？",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          center: true,
        }
      )
        .then(() => {
          this.$axios1
            .post("/account/transaction/transactionFpjs", {
              transactionId: row.id,
            })
            .then((res) => {
              if (res.code == "T") {
                this.gettransactionFp();
                this.getMyBilling();
                this.$message({
                  type: "success",
                  message: "收票成功!",
                });
              } else {
                this.$message.error(res.msg);
              }
            });
        })
        .catch(() => {});
    },
      changeShowPage(){
          this.showPage=false
          this.$nextTick(()=>{
              this.showPage=true
          })
      },
    getMyBilling() {
      this.pageData.transactionType = "2";
      this.$axios1
        .post("/account/transaction/transactionInQuery", this.pageData)
        .then((res) => {
          if (res.code == "T") {
            this.tableData1 = res.data.content;
            this.pageData1.total = res.data.totalElements;
          } else {
            this.$message.error(res.msg);
          }
        });
    },
    handleSizeChange1(value) {
      console.log(value);
      this.pageData.pageSize = value;
      this.getMyBilling();
    },
    handleCurrentChange1(value) {
      this.pageData.currentPage = value;
      this.getMyBilling();
    },
    handleSizeChange(value) {
      console.log(value);
      this.pageData.pageSize = value;
      this.gettransactionFp();
    },
    handleCurrentChange(value) {
      this.pageData.currentPage = value;
      this.gettransactionFp();
    },
    gettransactionFp() {
      this.$axios1
        .post("/account/transaction/transactionFp", this.pageData1)
        .then((res) => {
          if (res.code == "T") {
            res.data.content.map((item, index) => {
              item.time = formatTimeToStr(item.time, "yyyy-MM-dd hh:mm:ss");
            });
            this.tableData = res.data.content;
            this.pageData.total = res.data.totalElements;
          } else {
            this.$message.error(res.msg);
          }
        });
    },

    buildSubmit() {
      this.$refs.dynamicValidateForm.validate((valid) => {
        if (valid) {
          console.log(this.dynamicValidateForm);
          this.dynamicValidateForm.money = +this.dynamicValidateForm.money;
          this.dynamicValidateForm.transactionType = "2";
          this.dynamicValidateForm.timeType = "2";
          this.dynamicValidateForm.time = formatTimeToStr(
            this.dynamicValidateForm.time,
            "yyyy-MM-dd hh:mm:ss"
          );
          console.log(this.dynamicValidateForm.time);
          this.$axios1
            .post("/account/transaction/transaction", this.dynamicValidateForm)
            .then((res) => {
              console.log(res);
              if (res.code == "T") {
                this.$message.success("成功创建发票");
                this.gettransactionFp();
                 this.getMyBilling()
                this.BuilddialogVisible = false;
                this.$refs.dynamicValidateForm.resetFields();
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
    pay(row) {
      console.log(row);

      this.$confirm(
        "确认给账户" +
          row.transactionCardNum +
          "支付" +
          row.money +
          "(其中包括" +
          row.serviceFee +
          "服务费）" +
          "元发票吗？",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          center: true,
        }
      )
        .then(() => {
          let data = {
            transactionId: row.id,
          };
          this.$axios1
            .post("/account/transaction/transactionFpConfirm", {
              transactionId: row.id,
            })
            .then((res) => {
              if (res.code == "T") {
                this.gettransactionFp();
                this.getMyBilling()
                this.$message({
                  type: "success",
                  message: "支付成功!",
                });
              } else {
                this.$message.error(res.msg);
              }
            });
        })
        .catch(() => {});
    },
    cancel(row) {
      console.log(row);
      this.$confirm("确认取消发票吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        center: true,
      })
        .then(() => {
          this.$axios1
            .post("/account/transaction/transactionCancel", {
              transactionId: row.id,
            })
            .then((res) => {
              if (res.code == "T") {
                this.gettransactionFp();
                this.getMyBilling()
                this.$message({
                  type: "success",
                  message: "取消成功!",
                });
              } else {
                this.$message.error(res.msg);
              }
            });
        })
        .catch(() => {});
    },
    anwser() {
      this.dialogVisible = true;
    },
  },
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