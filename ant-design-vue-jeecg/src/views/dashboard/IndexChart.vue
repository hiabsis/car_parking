<template>
  <div class="page-header-index-wide">
    <a-row :gutter="12">
      <a-col :sm="24" :md="24" :xl="24" style="margin-bottom: 10px">
        <a-card title="车库车位" >
          <a-list :grid="{ gutter: 16, xs: 1, sm: 2, md: 4, lg: 4, xl: 6, xxl: 3  }" :pagination="pagination" :data-source="parkings">

            <a-list-item slot="renderItem" slot-scope="item, index">
              <a-card :title="'车位 '+item.parkingNumber" style="">
                <a-card-grid  v-if="item.stopCarNumber !==null" style="width:100%;text-align:center;background-color: #8cc8ff">
                  <p >已停</p>
                </a-card-grid>
                <a-card-grid v-else style="width:100%;text-align:center;">
                  <p >空置</p>
                </a-card-grid>
              </a-card>
            </a-list-item>
          </a-list>
          <div>
            <trend flag="" style="margin-right: 16px;">
              <span slot="term">总车位数 {{ this.parkingInfo[0] }}</span>
            </trend>
            <trend flag="">
              <span slot="term">闲置车位数 {{ this.parkingInfo[1] }}</span>
            </trend>
          </div>
        </a-card>
<!--        <chart-card :loading="loading" title="小区车库车位情况">-->
<!--         -->

<!--          <template slot="footer">-->
<!--          </template>-->
<!--        </chart-card>-->
      </a-col>
      <a-col :span="24">

        <div style="background-color: white; padding: 0px;">
          <a-card title="我的车辆" :bordered="false" v-if=""  style="">

             <a @click="handleAddCars()"  slot="extra" >添加车辆</a>

            <a-row :gutter="16" >

              <a-col :span="6" v-for="item in cars">

                <a-card  style="width: 300px ; margin-left: 3px" >
                  <img
                    v-if="item.status === '1'"
                    slot="cover"
                    alt="example"
                    src="~@/assets/car-inter.jpg"
                    width="100%"
                  />
                  <img
                    v-else
                    slot="cover"
                    alt="example"
                    src="~@/assets/car-outer.jpg"
                    width="100%"
                  />

                  <a-card-meta :title="'车牌 '+item.number" description="">
                  </a-card-meta>
                  <a-card-meta  v-if="item.parkingNumber" :title="'位置 '+item.parkingNumber" description="">parkingNumber

                  </a-card-meta>
                  <a-card-meta  v-else :title="'位置  未入库'" description="">parkingNumber

                  </a-card-meta>
                  <a-button v-if="item.status === '2'" type="primary" slot="actions"  @click="carInter(item)"  >车辆入库</a-button>
                  <a-button v-else  type="primary" slot="actions"  @click="carOuter(item)"   >车辆出库</a-button>

                  <a-popconfirm title="确定移除车辆吗?"  slot="actions" @confirm="() => handleDeleteCars(item.id)">
                    <a-button   type="danger"    >移除车辆</a-button>
                  </a-popconfirm>

                </a-card>
              </a-col>

            </a-row>
          </a-card>

        </div>

      </a-col>
      <a-col :span="24">
        <a-card :loading="loading" :bordered="false" title="停车费用明细" :style="{ marginTop: '24px' }">
          <a-button type="text" slot="actions"  @click="bugCard"  style="">购买</a-button>
          <a-list
            :data-source="cost"
          >
            <a-list-item slot="renderItem" :key="`a-${item.id}`" slot-scope="item, index">

              <a-list-item-meta :description="item.notes">
                <span slot="title" >{{ item.name }}</span>
              </a-list-item-meta>
            </a-list-item>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
    <div>
      <car-modal-index-add :user_id="loginUserInfo.id" ref="modalCarFormAdd" @ok="modalFormOk"></car-modal-index-add>
     <card-modal-index ref="modalCardForm" @ok="modalFormOk">  </card-modal-index>
     <car-modal-index ref="modalCarForm" @ok="modalFormOk">  </car-modal-index>
      <car-modal-index :user_id="id()" ref="modalAddCarForm" @ok="modalFormOk"></car-modal-index>

    </div>
  </div>


</template>

<script>
import user from '@/store/modules/user'
import ChartCard from '@/components/ChartCard'
import ACol from "ant-design-vue/es/grid/Col"
import ATooltip from "ant-design-vue/es/tooltip/Tooltip"
import MiniArea from '@/components/chart/MiniArea'
import MiniBar from '@/components/chart/MiniBar'
import MiniProgress from '@/components/chart/MiniProgress'
import RankList from '@/components/chart/RankList'
import Bar from '@/components/chart/Bar'
import LineChartMultid from '@/components/chart/LineChartMultid'
import HeadInfo from '@/components/tools/HeadInfo.vue'

import Trend from '@/components/Trend'
import {getLocalUserInfo, getLoginfo, getVisitInfo} from '@/api/api'
import {deleteAction, getAction} from "@api/manage";

import CardModalIndex from "@views/dashboard/page/CardModal.Style#Drawer";
import CarModalIndexAdd from "@views/account/center/page/CarModelIndex";
import CarModalIndex from "@views/dashboard/page/CarModalIndex.Style#Drawer.vue";
import {mapGetters} from "vuex";

const rankList = []
for (let i = 0; i < 7; i++) {
  rankList.push({
    name: '白鹭岛 ' + (i + 1) + ' 号店',
    total: 1234.56 - i * 100
  })
}
const barData = []
for (let i = 0; i < 12; i += 1) {
  barData.push({
    x: `${i + 1}月`,
    y: Math.floor(Math.random() * 1000) + 200
  })
}
export default {
  name: "IndexChart",
  components: {
    CarModalIndexAdd,
    CarModalIndex,
    CardModalIndex,
    ATooltip,
    ACol,
    ChartCard,
    MiniArea,
    MiniBar,
    MiniProgress,
    RankList,
    Bar,
    Trend,
    LineChartMultid,
    HeadInfo
  },
  data() {
    return {
      parkingPage:{
        // pageNo:1,
        // pageSize:8
      },
      cars:[],
      parkings :[],
      parkingInfo: {},
      loading: true,
      center: null,
      rankList,
      barData,
      loginUserInfo: {},
      visitFields: ['ip', 'visit'],
      visitInfo: [],
      indicator: <a-icon type="loading" style="font-size: 24px" spin/>,
      cost: [],
      carUrl: {
        list: "/car/car/list",
        delete: "/car/car/delete",
        deleteBatch: "/car/car/deleteBatch",
        exportXlsUrl: "/car/car/exportXls",
        importExcelUrl: "car/car/importExcel",
      },
      pagination: {
        onChange: page => {
          this.parkingPage.pageNo = page
          this.getCarParking()
          console.log(page);
        },
        pageSize: 9,
        total:8,
      },
    }
  },
  created() {
    setTimeout(() => {
      this.loading = !this.loading
    }, 1000)
    this.loginUserInfo = getLocalUserInfo()
    this.initLogInfo()
    this.getParkingTotal()
    this.getCostListInfo()
    this.getCarParking()

  },
  activated() {
    let id = this.loginUserInfo.id
    this.getUserCars(id)
  },
  methods: {
    submitCallback(){
      this.getCarParking()
    },
    getStopCarNumber(number){
      if(number){
        return number
      }
      return  ""
    },
    // 加载车位信息
    getCarParking(){

      getAction(
        "/car/carParking/list",
        this.parkingPage
      ) .then((res)=>{

        if(res.success){
          this.parkings = res.result.records
          this.pagination.total =Math.floor( res.result.total)
        }else{
          this.$message.warning(res.message);
        }
      }).finally(() => {
        this.confirmLoading = false;
      })
    },
    handleDeleteCars: function (id) {
      if(!this.carUrl.delete){
        this.$message.error("请设置url.delete属性!")
      }
      var that = this;
      deleteAction(that.carUrl.delete, {id: id}).then((res) => {
        if (res.success) {
          //重新计算分页问题
          that.$message.success(res.message);
          this.loginUserInfo = getLocalUserInfo()

          this.getUserCars(this.loginUserInfo.id)
          this.getCarParking()
        } else {
          that.$message.warning(res.message);
        }
      });


    },

    /**
     * 添加用户车辆
     */
    handleAddCars(){
      this.$refs.modalCarFormAdd.add();
      this.$refs.modalCarFormAdd.title = "每个用户最多绑定5辆车";
      this.$refs.modalCarFormAdd.disableSubmit = false;
    },
    ...mapGetters(["id"]),
    getUserCars(id){
      console.log("获取用户车辆",id)
      const that = this;
      getAction("/car/car/list",{userId:id})
        .then((res)=>{

        if(res.success){

          that.cars = res.result.records
        }else{
          that.$message.warning(res.message);
        }
      }).finally(() => {
        that.confirmLoading = false;
      })
    },
    /**
     * 车辆入库
     */
    carInter(car){
      console.log('车辆入库',car)
      this.$refs.modalCarForm.inter(this.loginUserInfo,car);
      this.$refs.modalCarForm.title = "车辆入库"
      this.$refs.modalCarForm.visible = true


    },
    /**
     * 车辆出库
     */
    carOuter(car){
      this.$refs.modalCarForm.outer(this.loginUserInfo,car);
      this.$refs.modalCarForm.title = "车辆出库"
      this.$refs.modalCarForm.visible = true

    },
    modalFormOk(){
      this.initLogInfo()
      this.getParkingTotal()
      this.getCostListInfo()
      this.getUserCars(this.loginUserInfo.id)
      this.getCarParking()
    },

    /**
     * 购买年卡/月卡
     */
    bugCard(){
      console.log("购买年卡/月卡")
      this.$refs.modalCardForm.add(this.loginUserInfo.id);
      this.$refs.modalCardForm.title = "购买年卡/月卡";
      this.$refs.modalCardForm.disableSubmit = false;
    },

    getCostNames(){
      let res = [];

    },
    /**
     * 获取费用明细
     */
    getCostListInfo() {
      const that = this;
      getAction("/car/carParkingCost/list", null).then((res) => {

        if (res.success) {
          that.cost = res.result.records
        } else {
          that.$message.warning(res.message);
        }
      }).finally(() => {
        that.confirmLoading = false;

      })
    },
    /**
     * 获取车位信息
     */
    getParkingTotal() {
      const that = this;
      getAction("/car/carParking/total", null).then((res) => {

        if (res.success) {
          that.parkingInfo = res.result
        } else {
          that.$message.warning(res.message);
        }
      }).finally(() => {
        that.confirmLoading = false;
      })
    },

    initLogInfo() {
      getLoginfo(null).then((res) => {
        if (res.success) {
          Object.keys(res.result).forEach(key => {
            res.result[key] = res.result[key] + ""
          })
          this.loginfo = res.result;
        }
      })
      getVisitInfo().then(res => {
        if (res.success) {
          this.visitInfo = res.result;
        }
      })
    },
  }
}
</script>

<style lang="less" scoped>
.circle-cust {
  position: relative;
  top: 28px;
  left: -100%;
}

.extra-wrapper {
  line-height: 55px;
  padding-right: 24px;

  .extra-item {
    display: inline-block;
    margin-right: 24px;

    a {
      margin-left: 24px;
    }
  }
}

/* 首页访问量统计 */
.head-info {
  position: relative;
  text-align: left;
  padding: 0 32px 0 0;
  min-width: 125px;

  &.center {
    text-align: center;
    padding: 0 32px;
  }

  span {
    color: rgba(0, 0, 0, .45);
    display: inline-block;
    font-size: .95rem;
    line-height: 42px;
    margin-bottom: 4px;
  }

  p {
    line-height: 42px;
    margin: 0;

    a {
      font-weight: 600;
      font-size: 1rem;
    }
  }
}
</style>