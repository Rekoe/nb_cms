<template>
  <div class="page-indexlist">
    <mt-search v-model="value"></mt-search>
    <div class="page-indexlist-wrapper">
      <mt-index-list>
        <mt-index-section v-for="(item, index) in alphabet" :key="item.initial" :index="item.initial">
          <mt-cell v-for="cell in item.cells" :key="cell.name" :title="cell.cname"><template slot-scope="scope"><div v-if="cell.pay"><el-button type="primary" :disabled="true" size="mini">已付款</el-button></div><div v-if="!cell.pay"><el-button type="success" size="mini" @click="open2(cell.id)">未付款</el-button></div></template></mt-cell>
        </mt-index-section>
      </mt-index-list>
    </div>
  </div>
</template>

<style>
.mint-search{
  height: 100%;
}
@component-namespace page {
  @component indexlist {
    @descendent desc {
      text-align: center;
      color: #666;
      padding-bottom: 5px;
    }
    @descendent wrapper {
      width: 100%;
      border-top: solid 1px #ddd;
    }
  }
}
</style>

<script type="text/babel">
import { Indicator } from 'mint-ui';
import { Field } from 'mint-ui';
import { MessageBox } from 'mint-ui';
import 'mint-ui/lib/style.css';
export default {
  data() {
    return {
      alphabet: [],
      value: ''
    };
  },
  watch: {
    value: function() {
      this.$api.Mobile.load('/heath/search?key='+this.$route.params.id, result => {
	    let temp = result.data.filter(item => item.py.startsWith(this.value));
        this.gen(temp);
     });
    }
  },
  created() {
    this.$api.Mobile.load('/heath/search?key='+this.$route.params.id, result => {
 	  this.gen(result.data);
    });
  },
  methods: {
    gen(arr) {
      this.alphabet = [];
      'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('').forEach(initial => {
        let cells = arr.filter(name => name.py[0] === initial);
        if (cells.length) {
          this.alphabet.push({
            initial,
            cells
          });
        }
      });
    },
    open2(id) {
	    MessageBox.confirm('对方已付款', '确认?').then(action => {
	      this.$api.Mobile.load('/heath/pay?id='+id+'&key='+this.$route.params.id, result => {
		 	console.log(result);
		 	if(result.status == 200){
		   		this.$message({type: 'success',message: '成功!'});
		    	  	this.$api.Mobile.load('/heath/search?key='+this.$route.params.id, result => {
			 	  this.gen(result.data);
			    });
		   	}else{
		   	   this.$message({showClose: true, message: 'ERROE:' + resp.reason,type: 'error'});
		   	}
		  });
	    }).catch(action => {
			console.log('取消的回调');
		});
	 }
  }
};
</script>