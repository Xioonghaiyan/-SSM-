<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script setup>
// 使用空的 setup script，因为这个文件只需要作为路由容器
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  width: 100%;
  height: 100vh;
}
</style>
