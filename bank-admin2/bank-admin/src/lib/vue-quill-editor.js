// 富文本编辑器
import Vue from 'vue'
import VueQuillEditor from 'vue-quill-editor'

// 通过externals加载外部CDN资源
// import 'quill/dist/quill.core.css' // import styles
// import 'quill/dist/quill.snow.css' // for snow theme
// import 'quill/dist/quill.bubble.css' // for bubble theme

Vue.use(VueQuillEditor /* { default global options } */)
