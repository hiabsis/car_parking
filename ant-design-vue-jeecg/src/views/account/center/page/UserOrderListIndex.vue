<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="订单类型">
              <j-dict-select-tag type="list" v-model="logPage.type" dictCode="order_type"  placeholder="请选择订单类型" />
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-button type="primary" @click="getCurrentUserOrder" icon="search">查询</a-button>
               <a-button type="primary" @click="searchResetLog" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-timeline>
      <a-timeline-item v-for="item in orders" :key="item.id" color="green" >
        <p>订单编号 {{item.orderNumner}}</p>
        <p>支付时间 {{item.createTime}}</p>
        <p>类型 {{item.type_dictText}}</p>

<!--        <p v-if=" item.type === '4'">类型 充值积分</p>-->
<!--        <p v-else-if="item.type === '1' ||item.type === '2' ">类型 会卡购买</p>-->
<!--        <p v-else="item.type === '1' ||item.type === '2' ">类型  车辆出库</p>-->
        <p  >支付状态  已支付</p>
        <p v-if=" item.type === '4'" >消费 {{item.cost +'（元）'}}</p>
        <p v-else>消费 {{item.cost + ' 积分'}}</p>
      </a-timeline-item>
    </a-timeline>
    <a-pagination  :show-total="total => `共 ${total} 条`" :current="current" :total="total" @change="onChange"  />
    <!-- 查询区域 -->

    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
<!--    <div class="table-operator">-->
<!--      <a-button type="primary" icon="download" @click="handleExportXls('订单表')">导出</a-button>-->
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
<!--      &lt;!&ndash; 高级查询区域 &ndash;&gt;-->
<!--&lt;!&ndash;      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>&ndash;&gt;-->
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

<!--      <a-table-->
<!--        ref="table"-->
<!--        size="middle"-->
<!--        :scroll="{x:true}"-->
<!--        bordered-->
<!--        rowKey="id"-->
<!--        :columns="columns"-->
<!--        :dataSource="orders"-->
<!--        :pagination="ipagination"-->
<!--        :loading="loading"-->
<!--        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"-->
<!--        class="j-table-force-nowrap"-->
<!--        @change="handleTableChange">-->

<!--        <template slot="htmlSlot" slot-scope="text">-->
<!--          <div v-html="text"></div>-->
<!--        </template>-->
<!--        <template slot="imgSlot" slot-scope="text">-->
<!--          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>-->
<!--          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>-->
<!--        </template>-->
<!--        <template slot="fileSlot" slot-scope="text">-->
<!--          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>-->
<!--          <a-button-->
<!--            v-else-->
<!--            :ghost="true"-->
<!--            type="primary"-->
<!--            icon="download"-->
<!--            size="small"-->
<!--            @click="downloadFile(text)">-->
<!--            下载-->
<!--          </a-button>-->
<!--        </template>-->

<!--        <span slot="action" slot-scope="text, record">-->
<!--&lt;!&ndash;          <a @click="handleEdit(record)">编辑</a>&ndash;&gt;-->
<!--               <a @click="handleDetail(record)">详情</a>-->

<!--&lt;!&ndash;          <a-divider type="vertical" />&ndash;&gt;-->
<!--&lt;!&ndash;          <a-dropdown>&ndash;&gt;-->
<!--&lt;!&ndash;            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>&ndash;&gt;-->
<!--&lt;!&ndash;            <a-menu slot="overlay">&ndash;&gt;-->
<!--&lt;!&ndash;              <a-menu-item>&ndash;&gt;-->
<!--&lt;!&ndash;                <a @click="handleDetail(record)">详情</a>&ndash;&gt;-->
<!--&lt;!&ndash;              </a-menu-item>&ndash;&gt;-->
<!--&lt;!&ndash;              <a-menu-item>&ndash;&gt;-->
<!--&lt;!&ndash;&lt;!&ndash;                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">&ndash;&gt;&ndash;&gt;-->
<!--&lt;!&ndash;&lt;!&ndash;                  <a>删除</a>&ndash;&gt;&ndash;&gt;-->
<!--&lt;!&ndash;&lt;!&ndash;                </a-popconfirm>&ndash;&gt;&ndash;&gt;-->
<!--&lt;!&ndash;              </a-menu-item>&ndash;&gt;-->
<!--&lt;!&ndash;            </a-menu>&ndash;&gt;-->
<!--&lt;!&ndash;          </a-dropdown>&ndash;&gt;-->
<!--        </span>-->

<!--      </a-table>-->
    </div>

    <user-order-modal ref="modalForm" @ok="modalFormOk"></user-order-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import UserOrderModal from './UserOrderModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import user from '@/store/modules/user'
  import {getAction} from "@api/manage";
  import {getLocalUserInfo} from "@api/api";

  export default {
    name: 'UserOrderListIndex',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      UserOrderModal
    },
    data () {
      return {
        current: 1,
        orders : [],
        description: '订单表管理页面',
        // 表头
        columns: [

          {
            title:'费用(积分/元)',
            align:"center",
            dataIndex: 'cost'
          },
          {
            title:'订单编号',
            align:"center",
            dataIndex: 'orderNumner'
          },
          {
            title:'订单类型',
            align:"center",
            dataIndex: 'type_dictText'
          },
          {
            title:'状态',
            align:"center",
            dataIndex: 'status_dictText'
          },
          {
            title:'交易时间',
            align:"center",
            dataIndex: 'orderTime'
          },
          // {
          //   title:'备注',
          //   align:"center",
          //   dataIndex: 'notes'
          // },
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
          list: "/car/userOrder/list",
          delete: "/car/userOrder/delete",
          deleteBatch: "/car/userOrder/deleteBatch",
          exportXlsUrl: "/car/userOrder/exportXls",
          importExcelUrl: "car/userOrder/importExcel",
          
        },
        logPage:{
          pageNo:1,
          pageSize:5,
        },
        total:100,
        dictOptions:{},
        superFieldList:[],
        userId : ""
      }
    },
    created() {
      this.userId = getLocalUserInfo().id;
    this.getSuperFieldList();
    this.getCurrentUserOrder()
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      searchResetLog(){
        this.logPage.type = undefined
      },
      onChange(current) {
        this.logPage.pageNo = current;
        this.getCurrentUserOrder()
        this.current = current;
      },

      /**
       * 获取当前用户的消费记录
       */
      getCurrentUserOrder(){
        const that = this;
        this.logPage.userId =this.userId
        getAction("/car/userOrder/list",this.logPage).then((res)=>{
          console.log(res)
          if(res.success){
            that.orders = res.result.records
            that.total = res.result.total
          }else{
            that.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;
        })
      },
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'sel_search',value:'userId',text:'用户名',dictTable:'sys_user', dictText:'username', dictCode:'id'})
        fieldList.push({type:'string',value:'cost',text:'费用',dictCode:''})
        fieldList.push({type:'string',value:'orderNumner',text:'订单编号',dictCode:''})
        fieldList.push({type:'string',value:'type',text:'订单类型',dictCode:'order_type'})
        fieldList.push({type:'string',value:'status',text:'状态',dictCode:'oder_status'})
        fieldList.push({type:'datetime',value:'orderTime',text:'交易时间'})
        // fieldList.push({type:'string',value:'notes',text:'备注',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>