<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="费用名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name" placeholder="请输入费用名称"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="最长停车时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="time">
              <a-input-number v-model="model.time" placeholder="请输入最长停车时间" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="单位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="unit">
              <j-dict-select-tag type="list" v-model="model.unit" dictCode="paking_time_unit" placeholder="请选择单位" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="费用" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="cost">
              <a-input v-model="model.cost" placeholder="请输入费用"  ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="type">
              <j-dict-select-tag type="list" v-model="model.type" dictCode="car_parking_cost_type" placeholder="请选择类型" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="notes">
              <a-input v-model="model.notes" placeholder="请输入备注"  ></a-input>
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
    name: 'CarParkingCostForm',
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
           name: [
              { required: true, message: '请输入费用名称!'},
           ],
           time: [
              { required: true, message: '请输入最长停车时间!'},
              { pattern: /^-?\d+\.?\d*$/, message: '请输入数字!'},
           ],
           unit: [
              { required: true, message: '请输入单位!'},
           ],
           cost: [
              { required: true, message: '请输入费用!'},
              { pattern: /^-?\d+\.?\d*$/, message: '请输入数字!'},
           ],
           type: [
              { required: true, message: '请输入类型!'},
           ],
        },
        url: {
          add: "/car/carParkingCost/add",
          edit: "/car/carParkingCost/edit",
          queryById: "/car/carParkingCost/queryById"
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
    },
    methods: {
      /**
       * 秒数转为时间
       * @param time
       * @param unit
       */
      formatTimeSecond(time,unit) {
        // 天
        if (unit === "d") {
          return  time / 24 / 3600
        }
        // 日
        if (unit === "h") {
          return time / 3600
        }
      },
      /**
       * 格式化时间 转为秒数
       * @param time
       * @param unit
       */
      formatTime(time,unit) {
        console.log(unit === 1)
        console.log("formatTime",time,unit,3600 * time)
        // 天
        if (unit === 'd') {
          this.model.time = 3600 * 24 * time
        }
        // 日
        if (unit === "h") {
          this.model.time = 3600 * time
        }
      },
      add () {
        this.edit(this.modelDefault);
      },
      edit (record) {
        this.model = Object.assign({}, record);
        this.model.time = this.formatTimeSecond(this.model.time,this.model.unit)
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
            this.formatTime(this.model.time,this.model.unit)

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