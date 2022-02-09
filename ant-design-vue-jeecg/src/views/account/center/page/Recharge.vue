<template>
  <a-modal
    :title="title"
    :width="modalWidth"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">

      <a-form :form="form">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="支付方式">
          <a-input type="" placeholder="请输入支付方式" v-decorator="[ 'way', validatorRules.way]" />
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="充值金额">
          <a-input type="" placeholder="请输入充值金额" v-decorator="[ 'money', validatorRules.way]" />
        </a-form-item>


      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

import { putAction } from '@/api/manage'

export default {
  name: "Recharge",
  data () {
    return {
      title:"充值积分",
      modalWidth:800,
      visible: false,
      confirmLoading: false,
      validatorRules:{

      },
      confirmDirty:false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },

      form:this.$form.createForm(this),
      url: "sys/user/updateScore",
      username:"",
    }
  },
  methods: {
    show(uname){
      if(!uname){
        this.$message.warning("当前系统无登录用户!");
        return
      }else{
        this.username = uname
        this.form.resetFields();
        this.visible = true;
      }
    },
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close');
      this.visible = false;
      this.disableSubmit = false;
      this.selectedRole = [];
    },
    handleOk () {
      const that = this;
      // 触发表单验证

      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true;
          let params = Object.assign({username:this.username},values)
          console.log("充值积分",params,that.url)
          putAction(this.url,params).then((res)=>{
            if(res.success){
              console.log(res)
              that.$message.success(res.message);
              that.close();
            }else{
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
          })
        }
      })
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
        callback('！');
      } else {
        callback()
      }
    },
    handleConfirmBlur  (e) {
      const value = e.target.value
      this.confirmDirty = this.confirmDirty || !!value
    }

  }
}
</script>

<style scoped>

</style>

