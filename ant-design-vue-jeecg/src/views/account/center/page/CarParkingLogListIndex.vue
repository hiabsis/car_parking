<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">

<!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--            <a-form-item label="车位号">-->
<!--              <j-search-select-tag placeholder="请选择车位号" v-model="queryParam.parkingNumner" dict="car_parking,parking_number,id"/>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="车牌号">
              <j-search-select-tag placeholder="请选择车牌号" v-model="queryParam.carNumner" :dict="dictQueryUserCarInfo"/>
            </a-form-item>
          </a-col>
<!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--            <a-form-item label="车牌号">-->
<!--              <j-search-select-tag placeholder="车牌号" v-model="queryParam.parkingNumner" dict="car,parking_number,id"/>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <template v-if="toggleSearchStatus">-->
<!--            <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
<!--              <a-form-item label="车牌号">-->
<!--                <a-input placeholder="请输入车牌号" v-model="queryParam.carNumner"></a-input>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--          </template>-->
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
<!--              <a @click="handleToggleSearch" style="margin-left: 8px">-->
<!--                {{ toggleSearchStatus ? '收起' : '展开' }}-->
<!--                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>-->
<!--              </a>-->
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
<!--    <div class="table-operator">-->
<!--      <a-button @click="handleAdd" type="primary" icon="plus">入库</a-button>-->
<!--      <a-button type="primary" icon="download" @click="handleExportXls('车辆入库记录')">导出</a-button>-->
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
      <!-- 高级查询区域 -->
<!--      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>-->
<!--      <a-dropdown v-if="selectedRowKeys.length > 0">-->
<!--        <a-menu slot="overlay">-->
<!--          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>-->
<!--        </a-menu>-->
<!--        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>-->
<!--      </a-dropdown>-->
<!--    </div>-->

    <!-- table区域-begin -->
    <div>
<!--      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">-->
<!--        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项-->
<!--        <a style="margin-left: 24px" @click="onClearSelected">清空</a>-->
<!--      </div>-->
      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="logs"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">

          <a @click="handleOuter(record)"  :disabled="record.endTime !== undefined" >出库</a>
<!--          <a @click="handleOuter(record)"  :disabled="record.endTime !== undefined" >出库</a>-->
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>

              <a-menu-item>
<!--                <a-popconfirm title="确定删除吗?"  @confirm="() => handleDelete(record.id)">-->
<!--                  <a>删除</a>-->
<!--                </a-popconfirm>-->
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <car-parking-log-modal ref="modalForm" @ok="modalFormOk"></car-parking-log-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import CarParkingLogModal from './CarParkingLogModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import {getAction, putAction} from "@api/manage";
  import {getLocalUserInfo} from "@api/api";

  export default {
    name: 'CarParkingLogListIndex',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      CarParkingLogModal
    },
    data () {
      return {
        description: '车辆入库记录管理页面',
        // 表头
        columns: [
          {
            title:'车位号',
            align:"center",
            dataIndex: 'parkingNumner_dictText'
          },
          {
            title:'车牌号',
            align:"center",
            dataIndex: 'carNumner_dictText'
          },
          {
            title:'入库时间',
            align:"center",
            dataIndex: 'startTime'
          },
          {
            title:'出库时间',
            align:"center",
            dataIndex: 'endTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/car/carParkingLog/list",
          delete: "/car/carParkingLog/delete",
          deleteBatch: "/car/carParkingLog/deleteBatch",
          exportXlsUrl: "/car/carParkingLog/exportXls",
          importExcelUrl: "car/carParkingLog/importExcel",
          outer: "car/carParkingLog/outer",

        },
        carUrl: {
          list: "/car/carParkingLog/list",

        },
        userId : "",
        dictOptions:{},
        superFieldList:[],
        dictQueryUserCarInfo : "car,number,id",
        logs:[]
      }
    },
    created() {
      this.userId = getLocalUserInfo().id;
      this.dictQueryUserCarInfo = "car,number,id,user_id ='"+this.userId+"'";
      this.getSuperFieldList();
      this.getUserCarsLog(this.userId)
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      },
    },
    methods: {
      //获取用户车辆信息
      getUserCarsLog(id){
        const that = this;
        // alert(id)
        getAction("/car/carParkingLog/list",{userId:id}).then((res)=>{
          console.log(res)
          if(res.success){
            that.logs = res.result.records
          }else{
            that.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;
        })
      },
      handleOuter(row){
        putAction(this.url.outer,row)
        this.loadData();
      },
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'userId',text:'用户名',dictCode:"sys_user,username,id,del_flag='0'"})
        fieldList.push({type:'sel_search',value:'parkingNumner',text:'车位号',dictTable:'car_parking', dictText:'parking_number', dictCode:'id'})
        fieldList.push({type:'sel_search',value:'carNumner',text:'车牌号',dictTable:'car', dictText:'number', dictCode:'id'})

        fieldList.push({type:'datetime',value:'startTime',text:'入库时间'})
        fieldList.push({type:'datetime',value:'endTime',text:'出库时间'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>