<template>
  <div :class="classObj" class="app-wrapper">
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside" />
    <div :class="{ 'fixed-header': fixedHeader }">
      <navbar />
    </div>
    <div class="main-container">
      <app-main />
    </div>
  </div>
</template>

<script setup>
import { useWindowSize } from '@vueuse/core'
import { AppMain, Navbar } from './components'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'

const settingsStore = useSettingsStore()
const sidebar = computed(() => useAppStore().sidebar)
const device = computed(() => useAppStore().device)
const fixedHeader = computed(() => settingsStore.fixedHeader)

const classObj = computed(() => ({
  mobile: device.value === 'mobile'
}))

const { width } = useWindowSize()
const WIDTH = 992

watch(() => device.value, () => {
  if (device.value === 'mobile' && sidebar.value.opened) {
    useAppStore().closeSideBar({ withoutAnimation: false })
  }
})

watchEffect(() => {
  if (width.value - 1 < WIDTH) {
    useAppStore().toggleDevice('mobile')
    useAppStore().closeSideBar({ withoutAnimation: true })
  } else {
    useAppStore().toggleDevice('desktop')
  }
})

function handleClickOutside() {
  useAppStore().closeSideBar({ withoutAnimation: false })
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile {
    position: fixed;
    top: 0;
  }
}

.main-container {
  min-height: 100%;
  position: relative;
  padding-top: 64px;
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: fixed;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: 100%;
}
</style>
