<template>
  <div class="container">
    <!-- Title -->
    <h1 class="title">SimilarityDetection</h1>

    <div>
      <div>原内容：</div>
      <div>
        <select v-model="selectedLanguage1" @change="updateLanguage('editor1')">
          <option value="javascript">JavaScript</option>
          <option value="python">Python</option>
          <option value="java">Java</option>
          <option value="cpp">Cpp</option>
          <option value="html">HTML</option>
          <!-- 可根据需要添加更多语言 -->
        </select>
      </div>
      <div ref="editor1" class="monaco-editor"></div> <!-- Monaco 编辑器的容器 -->
    </div>
    
    <div>
      <div>对比内容：</div>
      <div>
        <select v-model="selectedLanguage2" @change="updateLanguage('editor2')">
          <option value="javascript">JavaScript</option>
          <option value="python">Python</option>
          <option value="java">Java</option>
          <option value="cpp">Cpp</option>
          <option value="html">HTML</option>
          <!-- 可根据需要添加更多语言 -->
        </select>
      </div>
      <div ref="editor2" class="monaco-editor"></div> <!-- 另一个 Monaco 编辑器 -->
    </div>
    
    <div>
      <label for="method">选择比较算法：</label>
      <select v-model="method" id="method">
        <option value="levenshtein">Levenshtein</option>
        <option value="tongyi">Tongyi</option>
      </select>
    </div>
    
    <button @click="submitText">比较相似度</button>
  </div>
</template>

<script>
import axios from 'axios';
import * as monaco from 'monaco-editor';

export default {
  data() {
    return {
      inputValue: '',
      inputValue2: '',
      compareResponse: '',  // 添加一个用于存储相似度结果的变量
      method: 'levenshtein',
      selectedLanguage1: 'javascript',
      selectedLanguage2: 'javascript'
    };
  },
  mounted() {
    // 初始化 Monaco Editor 编辑器
    this.editor1 = monaco.editor.create(this.$refs.editor1, {
      value: this.inputValue,
      language: this.selectedLanguage1,  // 可以根据需要更改语言模式，例如 'python', 'html' 等
      theme: 'vs-light',       // 选择主题，'vs' 是浅色主题，'vs-dark' 是深色主题
      lineNumbers: 'on',      // 显示行号
      automaticLayout: true   // 自动调整布局
    });

    this.editor2 = monaco.editor.create(this.$refs.editor2, {
      value: this.inputValue2,
      language: this.selectedLanguage2, // 同上
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
    updateLanguage(editor) {
      if (editor === 'editor1') {
        this.editor1.getModel().setLanguage(this.selectedLanguage1);  // 更新第一个编辑器的语言
      } else if (editor === 'editor2') {
        this.editor2.getModel().setLanguage(this.selectedLanguage2);  // 更新第二个编辑器的语言
      }
    },
    submitText() {
      // 提交编辑器内容
      axios.post('http://localhost:3000/submitText', {
        text: this.inputValue,
        text2: this.inputValue2,
        method: this.method
      })
      .then(response => {
        console.log('提交成功', response);
        alert('代码相似度为：' + response.data);
      })
      .catch(error => {
        console.error('提交失败', error);
      });
    },
  }
};
</script>

<style scoped>
html, body {
  height: 100%;
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: Arial, sans-serif;
  background-color: #f4f7fa;
}

body {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* Ensure full viewport height */
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 80%;
  max-width: 1000px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  text-align: center;
  /* This will ensure the container stays centered within its parent */
  margin: 0 auto;
}

div {
  margin-bottom: 20px;
  width: 100%;
}

select {
  padding: 8px;
  font-size: 14px;
  border-radius: 5px;
  border: 1px solid #ccc;
  margin-right: 10px;
}

button {
  background-color: #007bff;
  color: white;
  font-size: 16px;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}

label {
  font-size: 16px;
  color: #333;
}

.monaco-editor {
  width: 100%;
  height: 400px;
  border: 1px solid #ccc;
  border-radius: 5px;
  text-align: left; /* Prevent code from being centered inside the editor */
}

.monaco-editor:focus {
  border-color: #007bff;
}

select, button {
  margin-top: 10px;
}
</style>
