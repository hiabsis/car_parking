<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <!--    <div class="table-page-search-wrapper">-->
    <!--      <a-form layout="inline" @keyup.enter.native="searchQuery">-->
    <!--        <a-row :gutter="24">-->
    <!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
    <!--            <a-form-item label="用户">-->
    <!--              <j-search-select-tag placeholder="请选择用户" v-model="queryParam.userId" dict="sys_user,username,id"/>-->
    <!--            </a-form-item>-->
    <!--          </a-col>-->
    <!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
    <!--            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">-->
    <!--              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>-->
    <!--              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>-->
    <!--              <a @click="handleToggleSearch" style="margin-left: 8px">-->
    <!--                {{ toggleSearchStatus ? '收起' : '展开' }}-->
    <!--                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>-->
    <!--              </a>-->
    <!--            </span>-->
    <!--          </a-col>-->
    <!--        </a-row>-->
    <!--      </a-form>-->
    <!--    </div>-->
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="plus" slot="actions" @click="bugCard" style="">购买</a-button>
      <!-- 高级查询区域 -->
      <!--      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
<!--        <a-button style="margin-left: 8px"> 批量操作-->
<!--          <a-icon type="down"/>-->
<!--        </a-button>-->
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
<!--      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">-->
<!--        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a-->
<!--        style="font-weight: 600">{{ selectedRowKeys.length }}</a>项-->
<!--        <a style="margin-left: 24px" @click="onClearSelected">清空</a>-->
<!--      </div>-->

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="cards"
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
          <img v-else :src="getImgView(text)" height="25px" alt=""
               style="max-width:80px;font-size: 12px;font-style: italic;"/>
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
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>

              <a-menu-item>
                <a-popconfirm title="确定注销会卡吗?" @confirm="() => handleDeleteCard(record.id)">
                  <a>注销会卡</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <card-modal-index ref="modalCardForm" @ok="ok"></card-modal-index>
    <card-modal ref="modalCardFormPassword" @ok="ok"></card-modal>
    <card-modal ref="modalForm" @ok="ok"></card-modal>

  </a-card>
</template>

<script>

import '@/assets/less/TableExpand.less'
import {mixinDevice} from '@/utils/mixin'
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import CardModal from './CardModal'
import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
import user from '@/store/modules/user'
import {deleteAction, getAction} from "@api/manage";
import CardModalIndex from "@views/dashboard/page/CardModal.Style#Drawer";
import CardFormPassword from "@views/account/center/page/CardFormPassword";
import {getLocalUserInfo} from "@api/api";


export default {
  name: 'CardListIndex',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    CardFormPassword,
    CardModalIndex,
    CardModal
  },
  data() {
    return {
      description: '会员卡管理页面',
      // 表头
      columns: [
        // {
        //   title: '#',
        //   dataIndex: '',
        //   key: 'rowIndex',
        //   width: 60,
        //   align: "center",
        //   customRender: function (t, r, index) {
        //     return parseInt(index) + 1;
        //   }
        // },

        {
          title: '卡号',
          align: "center",
          dataIndex: 'number'
        },

        {
          title: '绑定车辆',
          align: "center",
          dataIndex: 'carId_dictText'
        },
        {
          title:'绑定车位',
          align:"center",
          dataIndex: 'parkingId_dictText'
        },
        {
          title: '购买时间',
          align: "center",
          dataIndex: 'startTime'
        },
        {
          title: '失效时间',
          align: "center",
          dataIndex: 'endTime'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: "center",
          fixed: "right",
          width: 147,
          scopedSlots: {customRender: 'action'}
        }
      ],
      url: {
        list: "/car/card/list",
        delete: "/car/card/delete",
        deleteBatch: "/car/card/deleteBatch",
        exportXlsUrl: "/car/card/exportXls",
        importExcelUrl: "car/card/importExcel",

      },
      userId: "",
      dictOptions: {},
      superFieldList: [],
      cards: [],
      localUserInfo: {}
    }
  },
  created() {
    this.localUserInfo = getLocalUserInfo()
    this.getSuperFieldList();
    this.getCurrentUserCards(this.localUserInfo.id)
    this.userId = this.localUserInfo.id;
  },
  computed: {
    importExcelUrl: function () {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    },
  },


  methods: {
    /**
     * 会卡续费
     * @param record
     */
    renewal(record){
      console.log("重置密码")
      this.$refs.modalCardFormPassword.edit(record, 1);
      this.$refs.modalCardFormPassword.title = "重置密码";
      this.$refs.modalCardFormPassword.disableSubmit = false;
    },
    /**
     * 获取用户信息
     */

    /**
     * 更改记录
     */
    ok() {
      this.getCurrentUserCards(this.localUserInfo.id)
    },

    /**
     * 重置密码
     */
    handleChangePassword(record) {
      console.log("重置密码")
      this.$refs.modalCardFormPassword.edit(record, 1);
      this.$refs.modalCardFormPassword.title = "重置密码";
      this.$refs.modalCardFormPassword.disableSubmit = false;
    },
    /**
     * 购买会员卡
     */
    bugCard() {
      console.log("购买年卡/月卡", this.userId)
      this.$refs.modalCardForm.add(this.userId);
      this.$refs.modalCardForm.title = "购买年卡/月卡";
      this.$refs.modalCardForm.disableSubmit = false;
    },
    handleDeleteCard(id) {
      if (!this.url.delete) {
        this.$message.error("请设置url.delete属性!")
        return
      }
      var that = this;
      deleteAction(that.url.delete, {id: id}).then((res) => {
        if (res.success) {
          //重新计算分页问题
          that.getCurrentUserCards(that.localUserInfo.id);
        } else {
          that.$message.warning(res.message);
        }
      });
    },
    /**
     * 获取当前用户的会员卡
     */
    getCurrentUserCards(userId) {
      const that = this;
      getAction("/car/card/list", {userId: userId}).then((res) => {
        console.log(res)
        if (res.success) {
          that.cards = res.result.records
        } else {
          that.$message.warning(res.message);
        }
      }).finally(() => {
        that.confirmLoading = false;
      })
    },
    initDictConfig() {
    },
    getSuperFieldList() {
      let fieldList = [];
      fieldList.push({
        type: 'sel_search',
        value: 'userId',
        text: '用户',
        dictTable: 'sys_user',
        dictText: 'username',
        dictCode: 'id'
      })
      fieldList.push({type: 'string', value: 'number', text: '卡号', dictCode: ''})
      fieldList.push({type: 'string', value: 'password', text: '支付密码', dictCode: ''})
      fieldList.push({
        type: 'sel_search',
        value: 'carId',
        text: '绑定车辆',
        dictTable: 'car',
        dictText: 'number',
        dictCode: 'id'
      })
      fieldList.push({type:'sel_search',value:'parkingId',text:'绑定车位',dictTable:'car_parking', dictText:'parking_number', dictCode:'id'})

      fieldList.push({type: 'datetime', value: 'startTime', text: '购买时间'})
      fieldList.push({type: 'datetime', value: 'endTime', text: '失效时间'})
      this.superFieldList = fieldList
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>