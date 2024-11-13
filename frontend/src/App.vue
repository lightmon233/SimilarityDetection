<template>
  <div class="container">
    <div>
      <div>原内容</div>
      <div ref="editor1" class="monaco-editor"></div> <!-- Monaco 编辑器的容器 -->
    </div>
    <div>
      <div>对比内容</div>
      <div ref="editor2" class="monaco-editor"></div> <!-- 另一个 Monaco 编辑器 -->
    </div>
    <button @click="submitText">算法比较相似度</button>
    <button @click="compareText">AI:qwen比较相似度</button>
    <div v-if="chatResponse" class="result-container">
      <div class="result-title">相似度结果</div>
      <div class="result-value">{{ chatResponse }}</div>
    </div>
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
      chatResponse: ''  // 添加一个用于存储相似度结果的变量
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


          })
          .catch(error => {
            console.error('提交失败', error);
          });
    },
    compareText() {

      axios.post('http://localhost:3000/dashchat', {
        text1: this.inputValue,
        text2: this.inputValue2
      })
          .then(response => {
            if (response.data.error === 0) {
              this.chatResponse = response.data.data.output.choices[0].message.content;



            } else {
              this.chatResponse = `Error: ${response.data.message}`;
              alert('相似度比较失败：' + this.chatResponse);
            }
          })
          .catch(error => {
            console.error('比较失败', error);
            this.chatResponse = `Error: ${error.message}`;
            alert('比较失败：' + this.chatResponse);
          });
    }
  }
};
</script>

<style scoped>
/* 容器整体样式 */
div {
  font-family: 'Arial', sans-serif; /* 设置字体 */
  color: #333; /* 设置文本颜色 */
  margin: 0;
  padding: 0;
}

/* Monaco 编辑器容器的样式 */
.monaco-editor {
  width: 100%;
  height: 300px; /* 高度可以根据需要调整 */
  border: 1px solid #ddd; /* 更加柔和的边框 */
  border-radius: 8px; /* 圆角边框 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  margin-bottom: 20px;
}

/* 原内容和对比内容的标题样式 */
div > div {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #444; /* 深灰色 */
}

/* 提交按钮样式 */
button {
  background-color: #4CAF50; /* 绿色背景 */
  color: white; /* 白色文字 */
  font-size: 16px;
  font-weight: bold;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px; /* 顶部间距 */
  display: block; /* 将按钮设为块级元素 */
  margin-left: auto; /* 左边距自动 */
  margin-right: auto; /* 右边距自动 */
}

/* 提交按钮鼠标悬停效果 */
button:hover {
  background-color: #45a049;
}

/* 表单容器样式 */
form {
  display: flex;
  flex-direction: column;
  gap: 20px; /* 为表单元素之间增加间距 */
}

/* 页面整体布局 */
.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9; /* 浅灰色背景 */
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 页面容器阴影 */
}

/* 结果显示区域的样式 */
.result-container {
  margin-top: 20px;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.result-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.result-value {
  font-size: 16px;
  color: #444;
}

/* 响应式布局：使编辑器容器和按钮在小屏幕设备上也能适应 */
@media (max-width: 768px) {
  .monaco-editor {
    height: 200px; /* 在小屏幕上调整编辑器的高度 */
  }

  button {
    width: 100%; /* 按钮宽度适应屏幕 */
    padding: 12px 0;
  }

  .result-container {
    padding: 15px;
  }

  .result-title {
    font-size: 16px;
  }

  .result-value {
    font-size: 14px;
  }
}
</style>