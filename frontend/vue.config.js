const { defineConfig } = require('@vue/cli-service')
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin'); 

module.exports = {
  configureWebpack: {
    plugins: [
      new MonacoWebpackPlugin({
        languages: ['javascript', 'typescript', 'css', 'html'],  // 指定要加载的语言
        features: ['!gotoSymbol']  // 可选：禁用一些功能，减少打包体积
      })
    ]
  },

  // chainWebpack: config => {
  //   config.resolve.alias
  //     .set('monaco-editor', 'monaco-editor/esm/vs/editor/editor.api.js');  // Monaco 编辑器路径
  // }
}
