<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="用户名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userId">
              <j-search-select-tag v-model="model.userId" dict="sys_user,username,id,del_flag='0'"  />
            </a-form-model-item>
<!--            <a-form-model-item label="用户名" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userId">-->
<!--              <a-input v-model="model.userId" placeholder="请输入用户名"  ></a-input>-->
<!--            </a-form-model-item>-->
          </a-col>
<!--          <a-col :span="24">-->
<!--            <a-form-model-item label="密码" v-if="" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="userId">-->
<!--              <a-input v-model="model.userId" placeholder="请输入用户名"  ></a-input>-->
<!--            </a-form-model-item>-->
<!--          </a-col>-->
          <a-col :span="24">

            <a-form-model-item label="车位号"  :labelCol="labelCol" :wrapperCol="wrapperCol" prop="parkingNumner">
              <j-search-select-tag v-model="model.parkingNumner" dict="car_parking,parking_number,id,status='1'"  />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="车牌号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="parkingNumner">
              <j-search-select-tag v-model="model.carNumner" :dict="'car,number,id'"  />
            </a-form-model-item>
<!--            -->
<!--            <a-form-model-item label="车牌号"   :labelCol="labelCol" :wrapperCol="wrapperCol" prop="carNumner">-->
<!--              <a-input v-model="model.carNumner" placeholder="请输入车牌号"  ></a-input>-->
<!--            </a-form-model-item>-->
          </a-col>
          <a-col :span="24" v-if="!this.model.id">
            <a-form-model-item label="入库时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="startTime">
              <j-date placeholder="请选择入库时间"  v-model="model.startTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-if="this.model.id">
            <a-form-model-item label="出库时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="endTime">
              <j-date placeholder="请选择出库时间"  v-model="model.endTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
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
    name: 'CarParkingLogForm',
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
              { required: true, message: '请输入用户名!'},
           ],
           parkingNumner: [
              { required: true, message: '请输入车位号!'},
           ],
           carNumner: [
              { required: true, message: '请输入车牌号!'},
           ],
        },
        url: {
          add: "/car/carParkingLog/inter",
          edit: "/car/carParkingLog/edit",
          queryById: "/car/carParkingLog/queryById"
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
      getTime(){
        let yy = new Date().getFullYear();
        let mm = new Date().getMonth()+1;
        let dd = new Date().getDate();
        let hh = new Date().getHours();
        let mf = new Date().getMinutes()<10 ? '0'+new Date().getMinutes() : new Date().getMinutes();
        let ss = new Date().getSeconds()<10 ? '0'+new Date().getSeconds() : new Date().getSeconds();
        return  yy+'-'+mm+'-'+dd+' '+hh+':'+mf+':'+ss;
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