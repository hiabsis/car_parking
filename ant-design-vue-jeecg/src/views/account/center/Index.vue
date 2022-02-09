<template>
  <div class="page-header-index-wide page-header-wrapper-grid-content-main">

    <a-row :gutter="24">
      <a-col :md="24" :lg="24">
        <a-card :bordered="false">
          <div class="account-center-avatarHolder">
            <div class="avatar">
              <img :src="getAvatarView(userDetail.avatar)"/>
            </div>
            <div class="username">{{ userDetail.username }}</div>
          </div>
          <div class="account-center-tags">

            <br>
            <br>
            <a-divider type="vertical" />
<!--            <div class="tagsTitle">用户设置</div>-->
<!--            <a @click="handleAddCars()"  >添加车辆</a>-->
<!--            <br>-->
<!--            <br>-->
<!--            <a-table-->
<!--              ref="table"-->
<!--              size="middle"-->
<!--              :scroll="{x:true}"-->
<!--              bordered-->
<!--              rowKey="id"-->
<!--              :columns="carColumns"-->
<!--              :dataSource="cars"-->
<!--              class="j-table-force-nowrap"-->
<!--             >-->

<!--              <template slot="htmlSlot" slot-scope="text">-->
<!--                <div v-html="text"></div>-->
<!--              </template>-->
<!--              <template slot="imgSlot" slot-scope="text">-->
<!--                <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>-->
<!--                <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>-->
<!--              </template>-->
<!--              <template slot="fileSlot" slot-scope="text">-->
<!--                <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>-->
<!--              </template>-->

<!--              <span slot="action" slot-scope="text, record">-->
<!--          <a @click="handleEditCars(record)">编辑</a>-->

<!--          <a-divider type="vertical" />-->
<!--          <a-dropdown>-->
<!--            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>-->
<!--            <a-menu slot="overlay">-->
<!--              <a-menu-item>-->
<!--                <a @click="handleDetailCars(record)">详情</a>-->
<!--              </a-menu-item>-->
<!--&lt;!&ndash;               <a-menu-item>&ndash;&gt;-->
<!--&lt;!&ndash;                <a @click="handleDetailCards(record)">绑定卡号</a>&ndash;&gt;-->
<!--&lt;!&ndash;              </a-menu-item>&ndash;&gt;-->
<!--              <a-menu-item>-->
<!--                <a-popconfirm title="确定删除吗?" @confirm="() => handleDeleteCars(record.id)">-->
<!--                  <a>删除</a>-->
<!--                </a-popconfirm>-->
<!--              </a-menu-item>-->
<!--            </a-menu>-->
<!--          </a-dropdown>-->
<!--        </span>-->

<!--            </a-table>-->
            <br>
            <a-divider type="vertical" />
            <br>
<!--            {{userDetail}}-->
<!--            <a @click="handleScore(userDetail)"  >充值积分</a>-->
            <br>

            <div>
            </div>
            <div>
            </div>
          </div>
          <div class="account-center-detail">
            <div class="tagsTitle"></div>
            <br>
            <a @click="handleEdit(userDetail)"  >修改个人信息</a>
            <br><br>
            <div class=" ">用户信息</div>
            <p>
              <i class=" "></i>姓名 {{ '  '+userDetail.realname }}
            </p>

            <p>
              <i class=" "></i>积分: {{  '  '+ userDetail.score === null ? "":  userDetail.score }}
            </p>
            <p>
              <i class=" "></i>手机: {{  '  '+ userDetail.phone === null ? "":  userDetail.phone }}
            </p>
            <p>
              <i class=" "></i>邮箱: {{  '  '+ userDetail.email === null ? "":  userDetail.email }}
            </p>
            <p>
              <i class=" "></i>性别:{{ '  '+userDetail.sex_dictText === undefined ? "": userDetail.sex_dictText}}
            </p>
          </div>
        </a-card>

      </a-col>


    </a-row>
    <user-modal-index ref="modalForm" @ok="modalFormOk"></user-modal-index>
    <car-modal-index :user_id="userDetail.id" ref="modalCarForm" @ok="modalFormOk"></car-modal-index>
    <user-password  ref="userPassword" ></user-password>
    <recharge  ref="recharge" ></recharge>
  </div>
</template>


<script>
import PageLayout from '@/components/page/PageLayout'
import RouteView from "@/components/layouts/RouteView"

import {mapGetters} from 'vuex'
import {deleteAction, getAction, getFileAccessHttpUrl} from '@/api/manage';
import user from '@/store/modules/user'
import UserModalIndex from "@views/account/center/page/UserModalIndex";
import CarModalIndex from "@views/account/center/page/CarModelIndex";
import UserPassword from "@comp/tools/UserPassword";
import Recharge from "@views/account/center/page/Recharge";
import {getLocalUserInfo, getUserList} from "@api/api";

export default {
  components: {
    Recharge,
    UserPassword,
    CarModalIndex,
    UserModalIndex,

    RouteView,
    PageLayout,

  },
  data() {
    return {
      visible: false,
      childrenDrawer: false,
      userInfo: {},
      cars:[],
      userDetail:{},

      // 表头
      carColumns: [
        {
          title:'车牌号',
          align:"center",
          dataIndex: 'number'
        },
        {
          title:'车辆类型',
          align:"center",
          dataIndex: 'type_dictText'
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
      carUrl: {
        list: "/car/car/list",
        delete: "/car/car/delete",
        deleteBatch: "/car/car/deleteBatch",
        exportXlsUrl: "/car/car/exportXls",
        importExcelUrl: "car/car/importExcel",
      },
      userId : "",
    }
  },
  created() {
    this.userId = getLocalUserInfo().id;
    // this.userDetail = getLocalUserInfo();
  },
  mounted() {
    this.getUserDetail(this.userId)
    this.getUserCars(this.userId)
  },
  methods: {
    ...mapGetters(["nickname"]),

    /**
     * 获取视图
     * @param avatar
     * @returns {*}
     */
    getAvatarView: function (avatar) {
      return getFileAccessHttpUrl(avatar)
    },
    /**
     * 获取当前车辆绑定的卡号
     * @param record
     */
    handleDetailCards:function (record){

    },
    handleScore:function (){
      let username = this.userDetail.username
      let id = this.userDetail.id
      this.$refs.recharge.show(username,id)
    },
    handleEditCars: function (record) {
      this.$refs.modalCarForm.edit(record);
      this.$refs.modalCarForm.title = "编辑";
      this.$refs.modalCarForm.disableSubmit = false;
    },
    handleDetailCars:function(record){
      this.$refs.modalCarForm.edit(record);
      this.$refs.modalCarForm.title="详情";
      this.$refs.modalCarForm.disableSubmit = true;
    },
    handleDeleteCars: function (id) {
      if(!this.carUrl.delete){
        this.$message.error("请设置url.delete属性!")
      }
      var that = this;
      deleteAction(that.carUrl.delete, {id: id}).then((res) => {
        if (res.success) {
          //重新计算分页问题
          that.reCalculatePage(1)
          that.$message.success(res.message);
          that.modalFormOk();
          that.getUserCars(that.userDetail.id)
        } else {
          that.$message.warning(res.message);
        }
      });

    },

    /**
     * 添加用户车辆
     */
    handleAddCars(){
      this.$refs.modalCarForm.add();
      this.$refs.modalCarForm.title = "新增";
      this.$refs.modalCarForm.disableSubmit = false;
    },
    //获取用户车辆信息
    getUserCars(id){
      const that = this;
      getAction("/car/car/list",{userId:id}).then((res)=>{
        console.log(res)
        if(res.success){
          that.cars = res.result.records
        }else{
          that.$message.warning(res.message);
        }
      }).finally(() => {
        that.confirmLoading = false;
      })
    },
    // 获取用户
    getUserDetail(id){
      this.userDetail = getLocalUserInfo();
      const that = this;
      getAction("/sys/user/list",{id:id}).then((res)=>{

        if(res.success){
          that.userDetail = res.result.records[0]
        }else{
          that.$message.warning(res.message);
        }
      }).finally(() => {
        that.confirmLoading = false;
      })
    },
    modalFormOk() {
      this.getUserDetail(this.userDetail.id)
      this.getUserCars(this.userDetail.id)
    },
    /**
     * 修改个人信息
     * @param record
     */
    handleEdit: function (record) {
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "编辑";
      this.$refs.modalForm.disableSubmit = false;
    },


    ...mapGetters(["nickname", "avatar"]),
    getAvatar() {
      return getFileAccessHttpUrl(this.avatar());
    },

  },
}
</script>

<style lang="less" scoped>
.page-header-wrapper-grid-content-main {
  width: 100%;
  height: 100%;
  min-height: 100%;
  transition: .3s;

  .account-center-avatarHolder {
    text-align: center;
    margin-bottom: 24px;

    & > .avatar {
      margin: 0 auto;
      width: 104px;
      height: 104px;
      margin-bottom: 20px;
      border-radius: 50%;
      overflow: hidden;

      img {
        height: 100%;
        width: 100%;
      }
    }

    .username {
      color: rgba(0, 0, 0, 0.85);
      font-size: 20px;
      line-height: 28px;
      font-weight: 500;
      margin-bottom: 4px;
    }
  }

  .account-center-detail {

    p {
      margin-bottom: 8px;
      padding-left: 26px;
      position: relative;
    }

    i {
      position: absolute;
      height: 14px;
      width: 14px;
      left: 0;
      top: 4px;
      background: url(https://gw.alipayobjects.com/zos/rmsportal/pBjWzVAHnOOtAUvZmZfy.svg)
    }

    .title {
      background-position: 0 0;
    }

    .group {
      background-position: 0 -22px;
    }

    .address {
      background-position: 0 -44px;
    }
  }

  .account-center-tags {
    .ant-tag {
      margin-bottom: 8px;
    }
  }

  .account-center-team {

    .members {
      a {
        display: block;
        margin: 12px 0;
        line-height: 24px;
        height: 24px;

        .member {
          font-size: 14px;
          color: rgba(0, 0, 0, .65);
          line-height: 24px;
          max-width: 100px;
          vertical-align: top;
          margin-left: 12px;
          transition: all 0.3s;
          display: inline-block;
        }

        &:hover {
          span {
            color: #1890ff;
          }
        }
      }
    }
  }

  .tagsTitle, .teamTitle {
    font-weight: 500;
    color: rgba(0, 0, 0, .85);
    margin-bottom: 12px;
  }

}

</style>