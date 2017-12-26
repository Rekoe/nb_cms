<template>
 <section>
         <el-row>
            <el-col :span="16">
                <el-autocomplete :fetch-suggestions="querySearch" placeholder="Please Input" @select="handleSelect" v-model="state"></el-autocomplete>
            </el-col>
        </el-row>
		<el-table :data="search" border style="width: 100%" v-loading="loading">
            <el-table-column prop="value" label="关键字"></el-table-column>
            <el-table-column prop="number" label="数量"></el-table-column>
            <el-table-column prop="tag" label="分类"></el-table-column>
        </el-table>
  </section>
</template>

<style>
	.el-select .el-input {
    		width: 130px;
  	}
	.el-row {
	    margin-bottom: 20px;
	    &:last-child {
	        margin-bottom: 0;
	    }
	}
	
	.el-col {
	    border-radius: 4px;
	}
	
	.bg-purple-dark {
	    background: #99a9bf;
	}
	
	.bg-purple {
	    background: #d3dce6;
	}
	
	.bg-purple-light {
	    background: #e5e9f2;
	}
	
	.grid-content {
	    border-radius: 4px;
	    min-height: 36px;
	}
	
	.row-bg {
	    padding: 10px 0;
	    background-color: #f9fafc;
	}

    .dialog_box{
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .el-dialog--small{
        width: auto!important;
    }
</style>
<script>
export default {
  data() {
    return {
    	  state: '',
      formLabelWidth: '120px',
      loading: false,
      search:[]
    }
  },
  methods: {
    querySearch (queryString, cb) {
      if(queryString){
     	this.$api.Search.search(queryString,result => {
         	var results = result.data.filter(this.createFilter(queryString))
         	this.search = results;
       		cb(results);
       		this.loading = false;
      });
     }
    },
    createFilter (queryString) {
      return (link) => {
        return (link.value.indexOf(queryString.toLowerCase()) === 0)
      }
    },
    handleSelect (item) {
      console.log(item)
    }
  },
  created: function() {
    
  }
};
</script>