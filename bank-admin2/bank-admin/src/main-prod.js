import Vue from 'vue'
import App from './App.vue'
import router from './router'
// 全局基础样式
import './assets/css/base.css'

// // Element UI
// import './lib/element'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
// 富文本编辑器
import './lib/vue-quill-editor'
Vue.use(ElementUI);
// 树状表格组件
import './lib/vue-table-with-tree-grid'

// 挂载axios到Vue原型对象,全局vue实例内可使用
import { axios1 } from './network/request'
Vue.prototype.$axios1 = axios1

// JS工具库
import _ from 'lodash'

// 全局日期过滤器（将时间戳变成定义的日期格式）
Vue.filter('dateFormat', function(originVal) {
  const dt = new Date(originVal)

  const y = dt.getFullYear()
  const m = (dt.getMonth() + 1 + '').padStart(2, '0')
  const d = (dt.getDate() + '').padStart(2, '0')
  const hh = (dt.getHours() + '').padStart(2, '0')
  const mm = (dt.getMinutes() + '').padStart(2, '0')
  const ss = (dt.getSeconds() + '').padStart(2, '0')

  return `${y}-${m}-${d} ${hh}:${mm}:${ss}` // yyyy-mm-dd hh:mm:ss 格式
})

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  router,
}).$mount('#app')
