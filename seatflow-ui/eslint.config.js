import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import globals from 'globals'

const vueAutoImports = {
  computed: 'readonly',
  getCurrentInstance: 'readonly',
  nextTick: 'readonly',
  onBeforeUnmount: 'readonly',
  onMounted: 'readonly',
  reactive: 'readonly',
  ref: 'readonly'
}

export default [
  {
    ignores: ['dist/**', 'node_modules/**']
  },
  js.configs.recommended,
  ...pluginVue.configs['flat/essential'],
  {
    files: ['src/views/seatflow/**/*.vue', 'src/views/index.vue'],
    languageOptions: {
      globals: {
        ...globals.browser,
        ...vueAutoImports
      }
    },
    rules: {
      'vue/multi-word-component-names': 'off'
    }
  },
  {
    files: ['src/api/seatflow/**/*.js'],
    languageOptions: {
      globals: globals.browser
    }
  }
]
