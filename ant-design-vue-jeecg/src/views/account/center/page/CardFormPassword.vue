<template>

  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="卡号"  :labelCol="labelCol" :wrapperCol="wrapperCol" prop="number">
              <a-input disabled v-model="model.number" placeholder="卡号"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="旧密码">
            <a-input type="password" placeholder="请输入旧密码" v-decorator="[ 'oldpassword', validatorRules.oldpassword]" />
          </a-form-item>

          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="新密码">
            <a-input type="password" placeholder="请输入新密码" v-decorator="[ 'password', validatorRules.password]" />
          </a-form-item>

          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="确认新密码">
            <a-input type="password" @blur="handleConfirmBlur" placeholder="请确认新密码" v-decorator="[ 'confirmpassword', validatorRules.confirmpassword]"/>
          </a-form-item>
        </a-row>
      </a-form-model>
    </j-form-container>
  </a-spin>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'CardFormPassword',
    components: {
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      },
      password:""
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
           carId: [
              { required: true, message: '请输入绑定车辆!'},
           ],
          oldpassword:{
            rules: [{
              required: true, message: '请输入旧密码!',
              alidator: this.validatePassword,
            }],
          },
          password:{
            rules: [{
              required: true, message: '请输入新密码!',
            }, {
              validator: this.validateToNextPassword,
            }],
          },
          confirmpassword:{
            rules: [{
              required: true, message: '请确认新密码!',
            }, {
              validator: this.compareToFirstPassword,
            }],
          }
        },
        url: {
          add: "/car/card/add",
          edit: "/car/card/edit",
          resetPassword: "/car/card/resetPassword",
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
      validatePassword(rule, value, callback){
        const form = this.form;
        if (value && value !== this.password) {
          callback('密码错误！');
        } else {
          callback()
        }
      },
      validateToNextPassword  (rule, value, callback) {
        const form = this.form;
        if (value && this.confirmDirty) {
          form.validateFields(['confirm'], { force: true })
        }
        callback();
      },
      compareToFirstPassword  (rule, value, callback) {
        const form = this.form;
        if (value && value !== form.getFieldValue('password')) {
          callback('两次输入的密码不一样！');
        } else {
          callback()
        }
      },
      handleConfirmBlur  (e) {
        const value = e.target.value
        this.confirmDirty = this.confirmDirty || !!value
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
      add () {
        this.edit(this.modelDefault);
      },
      edit (record) {
        console.log("realWordPassword")
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
              httpurl+=this.url.resetPassword;
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