<template>
  <div>
    <div>原内容</div>
    <div ref="editor1" class="monaco-editor"></div> <!-- Monaco 编辑器的容器 -->
  </div>
  <div>对比内容</div>
  <div ref="editor2" class="monaco-editor"></div> <!-- 另一个 Monaco 编辑器 -->
  <button @click="submitText">提交</button>
</template>

<script>
import axios from 'axios';
import * as monaco from 'monaco-editor';

export default {
  data() {
    return {
      inputValue: '',
      inputValue2: ''
    };
  },
  mounted() {
    // 初始化 Monaco Editor 编辑器
    this.editor1 = monaco.editor.create(this.$refs.editor1, {
      value: this.inputValue,
      language: 'javascript',  // 可以根据需要更改语言模式，例如 'python', 'html' 等
      theme: 'vs-light',       // 选择主题，'vs' 是浅色主题，'vs-dark' 是深色主题
      lineNumbers: 'on',      // 显示行号
      automaticLayout: true   // 自动调整布局
    });
    
    this.editor2 = monaco.editor.create(this.$refs.editor2, {
      value: this.inputValue2,
      language: 'javascript', // 同上
      theme: 'vs-light',      // 同上
      lineNumbers: 'on',      // 同上
      automaticLayout: true   // 同上
    });

    // 监听 Monaco 编辑器的内容变化
    this.editor1.onDidChangeModelContent(() => {
      this.inputValue = this.editor1.getValue();
    });
    this.editor2.onDidChangeModelContent(() => {
      this.inputValue2 = this.editor2.getValue();
    });
  },
  methods: {
    submitText() {
      // 提交编辑器内容
      axios.post('http://localhost:3000/submitText', {
        text: this.inputValue,
        text2: this.inputValue2
      })
      .then(response => {
        console.log('提交成功', response);
        alert('代码相似度为：' + response.data);
        // 清空内容
        this.inputValue = '';
        this.inputValue2 = '';
        this.editor1.setValue('');  // 清空 Monaco 编辑器的内容
        this.editor2.setValue('');
      })
      .catch(error => {
        console.error('提交失败', error);
      });
    }
  }
};
</script>

<style>
.monaco-editor {
  width: 100%;
  height: 300px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 20px;
}
button {
  margin-top: 10px;
}
</style>

