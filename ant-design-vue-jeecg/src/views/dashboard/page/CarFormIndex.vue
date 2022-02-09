<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24" v-if="title !=='车辆出库'">
            <a-form-model-item label="车辆" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="carId">
              <a-input disabled v-model="model.carNumner" ></a-input>
              <!--              <j-search-select-tag disabled v-model="model.carNumner" :dict= this.dictCarQuery />-->
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-else>
            <a-form-model-item label="车辆" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="carId">
              <a-input disabled v-model="model.carNumner" ></a-input>
              <!--              <j-search-select-tag disabled v-model="model.carNumner" :dict= this.dictCarQuery />-->
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-if="title ==='车辆出库'">
            <a-form-model-item label="入库车位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="">
              <a-input disabled v-model="parkingLog.parkingNumber" ></a-input>
              <!--              <j-search-select-tag disabled v-model="model.carNumner" :dict= this.dictCarQuery />-->
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="支付方式"  v-if="title==='车辆出库'"  :labelCol="labelCol" :wrapperCol="wrapperCol" prop="">
              <a-radio-group style="margin-right: 8px" default-value="" v-model="selectWay" @change="handelWay"  button-style="">
                <a-radio value="1">
                  积分
                </a-radio>
                <a-radio value="2">
                  会员卡
                </a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>

          <a-col :span="24" v-if="selectWay==='1'">
            <a-form-model-item label="入库时间"  v-if="title==='车辆出库'"  :labelCol="labelCol" :wrapperCol="wrapperCol" prop="">
              <a-input disabled v-model="parkingLog.startTime" placeholder=""  ></a-input>
            </a-form-model-item>
          </a-col>

          <a-col :span="24" v-if="selectWay==='1'">
            <a-form-model-item label="支付积分"  v-if="title==='车辆出库'"  :labelCol="labelCol" :wrapperCol="wrapperCol" prop="">
              <a-input disabled v-model="parkingLog.score" placeholder=""  ></a-input>
            </a-form-model-item>
          </a-col>
         <div v-if="JSON.stringify(cardInfo) !=='{}'">
           <a-col :span="24" v-if="selectWay==='2' ">
             <a-form-model-item label="卡号"     :labelCol="labelCol" :wrapperCol="wrapperCol" prop="number">
               <j-search-select-tag   type="list"  v-model="model.number" :dict= this.dictCardQuery />
             </a-form-model-item>
           </a-col>
           <a-col :span="24"  v-if="selectWay==='2'">
             <a-form-model-item label="支付密码"  v-if="title==='车辆出库'" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="password">
               <a-input-password v-model="model.password" placeholder="请输入支付密码" />
             </a-form-model-item>
           </a-col>
         </div>
          <div v-if="selectWay==='2' && JSON.stringify(cardInfo) ==='{}'"  >
            <div>
              <a-form-model-item label="卡号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="">
                <a-input   :disabled="true" placeholder="车位未绑定会卡" />
              </a-form-model-item>
            </div>

          </div>


<!--          // 查询是否绑定车位-->
          <a-col :span="24">
            <a-form-model-item label="车位类型"  v-if="title !=='车辆出库'"  :labelCol="labelCol" :wrapperCol="wrapperCol" prop="">
              <a-radio-group style="margin-right: 8px" default-value="" v-model="selectInter" @change="handelInter"  button-style="">
                <a-radio value="1">
                  临时车位
                </a-radio>
                <a-radio value="2">
                  固定车位
                </a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-if="selectInter==='1' && title !=='车辆出库' ">
            <a-form-model-item label="临时车位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="parkingNumner">
              <j-search-select-tag v-model="model.parkingNumner" dict="car_parking,parking_number,id,status='1' and type='2'"  />
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-if="selectInter==='2' && car.fixParkingNumber !== null" >
            <a-form-model-item label="固定车位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="fixParkingNumber">
              <a-input v-model="car.fixParkingNumber"  :disabled="true" placeholder="固定车位" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-else  v-if=" car.fixParkingNumber === null && selectInter==='2'  "  >
            <a-form-model-item label="固定车位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="fixParkingNumber">
              <a-input v-model="car.fixParkingNumber"  :disabled="true" placeholder="未绑定车位" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

import {httpAction, getAction, postAction} from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'
  import user from '@/store/modules/user'
  import {getLocalUserInfo} from "@api/api";

  export default {
    name: 'CarFormIndex',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      },
      title:""
    },
    watch:{
      car(){
        // this.getCarParkingLog(this.car);
      },
      selectInter(){
        if (this.selectInter==="2"){
          this.model.parkingNumner =  this.car.fixParkingId
        }else {
          this.model.parkingNumner =  ""
        }
      },


    },
    data () {
      return {
        selectInter: "1",
        selectWay:"1",
        model:{
         },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        validatorRules: {
           userId: [
              { required: true, message: '请输入用户!'},
           ],
           number: [
              { required: true, message: '请输入卡号!'},
              { validator: (rule, value, callback) => validateDuplicateValue('card', 'number', value, this.model.id, callback)},
           ],
           password: [
              { required: true, message: '请输入支付密码!'},
              { pattern: /^.{6,16}$/, message: '请输入6到16位任意字符!'},
           ],
          carNumner: [
              { required: true, message: '请输入绑定车辆!'},
           ],
          parkingNumner: [
            { required: true, message: '请选择车位!'},
          ],
        },
        url: {
          add: "/car/card/add",
          edit: "/car/card/edit",
          queryById: "/car/card/queryById",
          inter: "/car/carParkingLog/inter",
          outer: "/car/carParkingLog/outer",
          list: "/car/carParkingLog/list",
          carInfo: "/car/carParkingLog/carInfo"

        },
        userId:"",
        dictCarQuery :"car,number,id",
        dictCardQuery :"card,number,id",
        cardInfo:{},
        car:{},
        user:{},
        parkingLog:{},
        localUserInfo:{}

      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
    },
    created () {
       //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));

      // if (this.modelDefault.number === null || undefined === this.modelDefault.number){
      //   this.modelDefault.number = this.getProjectNum("")+ Math.floor(Math.random() * 1000);
      //   this.model.number =  this.modelDefault.number
      // }
      this.localUserInfo = getLocalUserInfo()
      this.dictCarQuery = "car,number,id,status='2' and user_id =\'"+this.localUserInfo.id+"\'"
      this.dictCarQuery = "card,number,id,user_id =\'"+this.localUserInfo.id+"\'"

    },
    mounted() {

    },

    methods: {
      handelInter(){

      },

      getCarInfo(id){
        const that = this;
        postAction("/car/card/queryByCarNumber",{"car_id":id}).then((res) => {
          if (res.success) {


            that.cardInfo = res.result.records
            // alert(JSON.stringify(that.cardInfo))
          } else {
            that.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;

        })
      },
      handelWay(){

      },
      getUserDict(){
        return "sys_user,username,id,del_flag='0'"
      },
      getProjectNum (prefix) {
        const projectTime = new Date() // 当前中国标准时间
        const Year = projectTime.getFullYear() // 获取当前年份 支持IE和火狐浏览器.
        const Month = projectTime.getMonth() + 1 // 获取中国区月份
        const Day = projectTime.getDate() // 获取几号
        var CurrentDate = Year
        if (Month >= 10) { // 判断月份和几号是否大于10或者小于10
          CurrentDate += Month
        } else {
          CurrentDate += '0' + Month
        }
        if (Day >= 10) {
          CurrentDate += Day
        } else {
          CurrentDate += '0' + Day
        }
        return prefix+CurrentDate
      },
      /**
       * 获取车辆绑定卡号
       */

      /**
       * 获取车辆停车记录
       * @param car
       */
      getCarParkingLog(car){

        let that = this
        let url = this.url.carInfo
        let params = {'carId':that.car.id}
        getAction(url,params).then((res)=>{
          if(res.success){
            console.log("获取车辆停车记录",res)
            that.parkingLog = res.result
          }else{
            that.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;
        })
      },
      /**
       * 车辆出库
       * @param user
       * @param car
       */
      outer (user,car){
        this.modelDefault.carNumner = car.number
        this.modelDefault.userId = user.id
        this.modelDefault.score = user.score
        this.edit(this.modelDefault);
        this.car = car;
        this.user = user
        this.getCarParkingLog(car)
        this.dictCardQuery = "card,number,id,car_id =\'"+this.car.id+"\'"
        // alert(this.dictCardQuery)
        this.getCarInfo(this.car.id)
      },
      /**
       * 车辆入库
       * @param user
       * @param car
       */
      inter (user,car){
        this.modelDefault.userId = user.id
        this.modelDefault.carNumner = car.number
        this.edit(this.modelDefault);
        console.log("车辆入库",car)
        this.car = car;
        this.user = user
      },
      add (userId,car) {
        this.modelDefault.userId = userId
        this.modelDefault.carNumner = car.number
        this.edit(this.modelDefault);
        this.car = car;
      },
      edit (record) {
        this.model = Object.assign({}, record);
        this.visible = true;
      },
      submitForm () {
        const that = this;
        // 触发表单验证
        this.$refs.form.validate(valid => {
          if (valid) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            that.model.carNumner = that.car.id
            if(that.title === '车辆入库'){
              httpurl+=this.url.inter+"?userId="+this.localUserInfo.id
              method = 'post';
            }else{
              this.model.id = that.parkingLog.log;
              this.model.payWay = that.selectWay;
              this.model.userId = that.userId;
              this.model.carId = that.car.id;
              console.log("出库",this.model);
              httpurl+=this.url.outer;
              method = 'put';
            }
            httpurl+="?costId="+this.model.costId;
            // this.model.id = this.car.id;
            httpAction(httpurl,this.model,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
            })
          }
         
        })
      },
    }
  }
</script>