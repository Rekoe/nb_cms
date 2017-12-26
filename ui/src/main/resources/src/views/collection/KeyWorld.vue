<template>
    <section>
        <el-row>
            <el-col :span="6">
                <el-input placeholder="请输入内容" v-model="searchKey" prefix-icon="el-icon-fa-search">
                    <div slot="append">
                        <el-button type="primary" icon="el-icon-fa-search" @click="doSearch()"></el-button>
                    </div>
                </el-input>
            </el-col>
        </el-row>
		<el-table :data="search" border style="width: 100%" v-loading="loading">
            <el-table-column prop="value" label="关键字"></el-table-column>
            <el-table-column prop="number" label="数量"></el-table-column>
            <el-table-column prop="tag" label="分类"></el-table-column>
        </el-table>
        </el-table>
    </section>
</template>
<script>
export default {
  data() {
    return {
      searchKey: "",
      loading: false,
      search:[],
      formLabelWidth: "100px"
    };
  },
  methods: {
    doSearch() {
      this.$api.Search.search(this.searchKey,result => {
         this.search = result.data;
       	 this.loading = false;
      });
    }
  }
};
</script>