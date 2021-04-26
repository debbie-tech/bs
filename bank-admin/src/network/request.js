import axios from 'axios'
// 页面顶部进度条
import NProgress from 'nprogress'
import { Message } from 'element-ui';
import router from '../router/index'
export const axios1 = axios.create({
  baseURL: 'http://tsncp.picp.io',
  timeout: 5000
})



// 发送请求前添加请求头Authorization字段
axios1.interceptors.request.use(config => {
  if(sessionStorage.getItem('token')){
    config.headers.Authorization = 'Bearer '+sessionStorage.getItem('token')

  }
    

  // 展示进度条
  NProgress.start()
  return config // 必须return config
})

axios1.interceptors.response.use(config => {
  // 隐藏进度条
  NProgress.done()
  console.log(config)
  if(config.status == 200){
    return config.data
  }else{
    Message.$error(config.msg)
  }
})