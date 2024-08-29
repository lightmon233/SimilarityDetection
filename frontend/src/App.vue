<template>
  <div>原内容
    <textarea v-model="inputValue" placeholder="请输入内容"></textarea>
  </div>
  <div>对比内容
    <textarea v-model="inputValue2" placeholder="请输入内容"></textarea>
  </div>
  <button @click="submitText">提交</button>
</template>

<script>

import axios from 'axios';

export default {
  data() {
    return {
      inputValue: '',
      inputValue2: ''
    }
  },
  methods: {
    submitText() {
      // 将inputValue发送给后端
      axios.post('http://localhost:3000/submitText', {
        text: this.inputValue,
        text2: this.inputValue2
      })
      .then(response => {
        console.log('提交成功', response)
        this.inputValue = ''
        this.inputValue2 = ''
      })
      .catch(error => {
        console.error('提交失败', error)
      })
    }
  }
}

</script>

<style>
</style>
