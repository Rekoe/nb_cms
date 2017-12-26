<template>
    <section>
        我是dashbord
    </section>
</template>
<style>
</style>
<script>
import { mapState, mapGetters, mapMutations } from "vuex";
export default {
  computed: {
    ...mapState({
      loginUser: state => state.loginUser
    })
  },
  methods: {
  ...mapMutations(["save", "remove"]),
    loadData() {
      this.$api.User.load(result => {
        let loginUser = result.loginUser;
        loginUser.roles = result.roles;
        loginUser.permissions = result.permissions;
      	console.log(result.permissions);
        this.save(loginUser);
      });
    }
  },
  created: function() {
    this.loadData();
  }
};
</script>