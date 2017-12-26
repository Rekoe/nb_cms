<template>
  <div class="social-signup-container">
    <a class="sign-btn" @click="githubHandleClick()">
      <span class="wx-svg-container"><svg-icon icon-class="github" class="icon"></svg-icon></span> GitHub
    </a>
    <div class="sign-btn" @click="googleHandleClick()">
      <span class="qq-svg-container"><svg-icon icon-class="google" class="icon"></svg-icon></span> Facebook
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    githubHandleClick() {
      
    },
    googleHandleClick(thirdpart) {
      
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
	.social-signup-container {
	  margin: 20px 0;
	  .sign-btn {
	    display: inline-block;
	    cursor: pointer;
	  }
	  .icon {
	    color: #fff;
	    font-size: 30px;
	    margin-top: 6px;
	  }
	  .wx-svg-container,
	  .qq-svg-container {
	    display: inline-block;
	    width: 40px;
	    height: 40px;
	    line-height: 40px;
	    text-align: center;
	    padding-top: 1px;
	    border-radius: 4px;
	    margin-bottom: 20px;
	    margin-right: 5px;
	  }
	  .wx-svg-container {
	    background-color: #8dc349;
	  }
	  .qq-svg-container {
	    background-color: #6BA2D6;
	    margin-left: 50px;
	  }
	}
</style>
