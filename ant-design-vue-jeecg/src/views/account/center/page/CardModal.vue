<template>
  <div>
    <j-modal
      :title="title"
      :width="width"
      :visible="visible"
      switchFullscreen
      @ok="handleOk"
      :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
      @cancel="handleCancel"
      cancelText="关闭">
      <card-form ref="realForm" @ok="submitCallback" :disabled="disableSubmit"></card-form>
    </j-modal>
    <j-modal
      :title="title"
      :width="width"
      :visible="visiblePassWord"
      switchFullscreen
      @ok="handleOk"
      :okButtonProps="{ class:{'jee-hidden': disableSubmit} }"
      @cancel="handleCancel"
      cancelText="关闭">
      <card-form-password ref="realFormPassword" :password="password" @ok="submitCallback" :disabled="disableSubmit"></card-form-password>
    </j-modal>
  </div>


</template>

<script>

  import CardForm from './CardForm'
  import CardFormPassword from "@views/account/center/page/CardFormPassword";
  export default {
    name: 'CardModal',
    components: {
      CardFormPassword,
      CardForm
    },
    data () {
      return {
        title:'',
        width:800,
        visible: false,
        disableSubmit: false,
        visiblePassWord:false,
        password:""
      }
    },
    methods: {
      add () {
        this.visible=true
        this.$nextTick(()=>{
          this.$refs.realForm.add();
        })
      },
      edit (record,type) {

        this.password = record.password
        if (type === 2){
          this.$nextTick(()=>{
            this.visiblePassWord=true
            console.log("MODEL")
            this.$refs.realFormPassword.edit(record);
            console.log("MODEL2")
          })
        }else {
          this.visible=true
          this.$nextTick(()=>{
            this.$refs.realForm.edit(record);
          })
        }

      },
      close () {
        this.$emit('close');
        this.visible = false;
        this.visiblePassWord = false;
      },
      handleOk () {
        this.$refs.realForm.submitForm();
      },
      submitCallback(){
        this.$emit('ok');
        this.visible = false;

      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>