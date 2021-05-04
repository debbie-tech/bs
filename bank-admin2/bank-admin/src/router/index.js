import Vue from 'vue'
import VueRouter from 'vue-router'
import { Message } from 'element-ui'

Vue.use(VueRouter)
Vue.component(Message.name, Message)


const home = () =>
  import(/* webpackChunkName: "login_home_welcome" */ '../views/Home.vue')
const openAcount = () =>
  import(/* webpackChunkName: "login_home_welcome" */ '../views/openAcount/openAcount.vue')
const billing = () =>
  import(/* webpackChunkName: "login_home_welcome" */ '../views/billing/billing.vue')
const transferAccounts = () =>
  import(/* webpackChunkName: "login_home_welcome" */ '../views/transferAccounts/transferAccounts.vue')
const personal = () =>
  import(/* webpackChunkName: "login_home_welcome" */ '../views/personal/personal.vue')
const login = () =>
  import(/* webpackChunkName: "login_home_welcome" */ '../views/login/login.vue')



// 防止同一路由多次点击报错
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

// 后台管理系统，除了登录页面不需要token，其他页面都需要token
const routes = [
  {
    path: '/',
    redirect: '/home/openAcount',
  },
  {
    path: '/home',
    component: home,
    children:[
      {
        path:'/home/openAcount',
        component:openAcount
      },
      {
        path:'/home/billing',
        component:billing
      },
      {
        path:'/home/transferAccounts',
        component:transferAccounts
      },
      {
        path:'/home/personal',
        component:personal
      },
      {
        path:'/home/login',
        component:login
      }
    ]
  },
]

const router = new VueRouter({
  routes,
})

export default router

// 后台管理系统，除了登录页面不需要token，其他页面都需要token
/**
 * 1 如果要去的是登录页面 直接跳转
 * 2 如果不是，判断缓存有没有token
 *  1 有 跳转
 *  2 没有 重新登录获取
 */
router.beforeEach((to, from, next) => {
  // if (to.path === '/login') return next()
  // if (sessionStorage.getItem('token')) {
  //   next()
  // } else {
  //   Message.info('请登录')
  //   setTimeout(() => {
  //     next('/login')
  //   }, 1000)
  // }
  next()
})
