<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="用户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userId">
              <j-search-select-tag v-model="model.userId" dict="sys_user,username,id,del_flag='0'"  />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="卡号" disabled :labelCol="labelCol" :wrapperCol="wrapperCol" prop="number">
              <a-input v-model="model.number" placeholder="请输入卡号"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="支付密码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="password">
              <a-input-password v-model="model.password" placeholder="请输入支付密码" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="绑定车辆" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="carId">
              <j-search-select-tag v-model="model.carId" dict="car,number,id"  />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="购买时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="startTime">
              <j-date placeholder="请选择购买时间"  v-model="model.startTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="失效时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="endTime">
              <j-date placeholder="请选择失效时间"  v-model="model.endTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction, getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'CardForm',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
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

           carId: [
             { required: true, message: '请选择绑定车辆!'},
             { validator: (rule, value, callback) => validateDuplicateValue('card', 'car_id', value, this.model.carId, callback)},
           ],
          parkingId: [
            { required: true, message: '请选择绑定车位!'},
            { validator: (rule, value, callback) => validateDuplicateValue('card', 'parking_id', value, this.model.parkingId, callback)},
          ],
        },
        url: {
          add: "/car/card/add",
          edit: "/car/card/edit",
          queryById: "/car/card/queryById"
        }
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

      if (this.modelDefault.number === null || undefined === this.modelDefault.number){
        this.modelDefault.number = this.getProjectNum("")+ Math.floor(Math.random() * 1000);
        this.model.number =  this.modelDefault.number
      }

    },
    methods: {
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
      add () {
        this.edit(this.modelDefault);
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
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
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