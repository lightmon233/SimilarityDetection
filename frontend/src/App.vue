<template>
  <div>
    通过后端取得：{{ msg }}
  </div>
  <router-view></router-view>
</template>

<script>

import $ from 'jquery';
import { ref } from 'vue';

export default {
  name: "App",
  setup: () => {
    let msg = ref("");
    $.ajax({
      url: "http://localhost:3000/",
      type: "get", 
      success: resp => {
        msg.value = resp;
      }
    })
    return {
      msg
    }
  }
}

</script>

<style>
</style>
