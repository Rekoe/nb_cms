<template>
<div id="page-field">
    <div class="page-part">
      <mt-field label="姓名:" placeholder="请填写真实姓名" v-model="ruleForm.name" class="fi"></mt-field>
	    <mt-cell :title="ruleForm.msl ? '穆斯林' : '不是穆斯林' ">
	      <mt-switch v-model="ruleForm.msl" @change="handleChange"></mt-switch>
	    </mt-cell>
  	</div>
    <div class="page-part"><mt-radio v-model="ruleForm.addr" :options="addrOption" /></div>
	<div class="login-btn">
		<mt-button type="danger" class="primary-button" @click.native="save">确定绑定</mt-button>
	</div>
</div>
</template>
<script>
import { Indicator } from 'mint-ui';
import { Field } from 'mint-ui';
import { MessageBox } from 'mint-ui';
import 'mint-ui/lib/style.css';
export default {
 data(){
      return {
      	ruleForm:{
      		office: '1',
      		name :'',
      		addr :'1',
      		msl:false
      }
    }
  },
  created() {
    this.addrOption = [{label: '101办公室',value: '1'},{label: '504办公室',value: '2'},{label: '903办公室',value: '3'}];
    this.mslOption = [{label: '是',value: '1'},{label: '否',value: '0'}];
  },
  methods: {
	save:function() {
		let vm = this;
		MessageBox.confirm(' ', '确认提交?').then(action => {
	        let url = '/dingcan/cn/'+this.$route.params.id;
	    		this.$api.Mobile.commit(url, this.ruleForm,data => {
 		  		MessageBox('', '恭喜你,提交成功');
       		});
		});
	  },
	  loadData() {
        this.$api.Mobile.load('/dingcan/user/' + this.$route.params.id, result => {
 		  console.log(result);
 		  this.ruleForm = result;
        })
     },
     handleChange(event) {
       console.log(event);
    	}
	},
    components:{
       'mt-field': Field
    },
    mounted: function () {
      this.loadData();
    }
};
</script>
<style lang='css'>
.login-btn {
    text-align: center;
}
.fi{
      border-bottom:1px solid #BABABA;
 }
.primary-button{
    width: 100%;
    background:#26A2FF;
 }
.page-radio .page-part {
  margin-top: 20px;
}
@component-namespace page {
    @component switch {
      @descendent padding {
        padding: 0 10px;
      }
    }
  }
</style>