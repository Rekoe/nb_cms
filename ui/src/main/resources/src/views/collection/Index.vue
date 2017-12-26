<template>
 <section>
         <el-row>
            <el-col :span="16">
                <el-input placeholder="input filter url ..." v-model="searchKey" icon="search">
                		<el-select v-model="select" slot="prepend" placeholder="请选择">
				      <el-option label="天猫" value="tmall"></el-option>
				      <el-option label="1688" value="1688"></el-option>
				      <el-option label="淘宝" value="taobao"></el-option>
				      <el-option label="亚马逊" value="amazon"></el-option>
				    </el-select>
                    <div slot="append">
                        <el-button type="primary" icon="search" @click="doSearch()">GO</el-button>
                    </div>
                </el-input>
            </el-col>
        </el-row>
		<el-table :data="images" border style="width: 100%" v-loading="loading">
            <el-table-column prop="image" label="地址"></el-table-column>
            <el-table-column label="缩略图">
                <template slot-scope="scope">
					<img :src='scope.row.thumbnail' width="64" height="64">
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
					<el-button type="primary" icon="message" @click="handleToPush(scope.$index,scope.row)">查看</el-button>
                </template>
            </el-table-column>
            <el-table-column label="下载">
                <template slot-scope="scope">
					<el-button type="primary" icon="el-icon-caret-bottom" @click="download(scope.$index)">下载</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-dialog :title="'浏览'" :visible.sync="view" width="100%" center>
			<el-input placeholder="https://" v-model="image.image"></el-input>
			<div class="dialog_box"><img :src='image.image'></div>
        </el-dialog>
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
      selected: [],
      searchKey: "",
      select:"tmail",
      view: false,
      images:[],
      image:{},
      formLabelWidth: '120px',
      loading: false
    }
  },
  methods: {
    changePage() {
      if (this.searchKey) {
        this.doSearch();
      } else {
        this.loadData();
      }
    },
    doSearch() {
    	  let params = {select: this.select, url: this.searchKey}
      this.$api.Collection.search(params, result => {
        this.images = result.list;
        this.loading = false;
      });
    },
    handleToPush(index, row) {
      this.image = this.images[index];
      this.view = true;
    },
    download(index){
      let img = this.images[index].image;
      console.log(img);
      location.href = baseUrl+'/collection/down?id='+img;
    },
    loadData() {
      this.$api.Collection.list(1,result => {
        this.images = result.list;
        console.log(result.list);
        this.searchKey='';
      });
    }
  },
  created: function() {
    this.loadData();
  }
};
</script>