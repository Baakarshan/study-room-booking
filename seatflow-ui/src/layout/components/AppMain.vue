<template>
  <section class="app-main">
    <router-view v-slot="{ Component, route }">
      <transition name="fade-transform" mode="out-in">
        <component v-if="!route.meta.link" :is="Component" :key="route.path" />
      </transition>
    </router-view>
    <copyright />
  </section>
</template>

<script setup>
import copyright from './Copyright/index'
</script>

<style lang="scss" scoped>
.app-main {
  min-height: calc(100vh - 64px);
  width: 100%;
  position: relative;
  overflow: hidden;
}

.app-main:has(.copyright) {
  padding-bottom: 36px;
}

@media screen and (max-width: 991px) {
  .app-main {
    padding-bottom: max(60px, calc(constant(safe-area-inset-bottom) + 40px));
    padding-bottom: max(60px, calc(env(safe-area-inset-bottom) + 40px));
    overscroll-behavior-y: none;
  }
}

@supports (-webkit-touch-callout: none) {
  @media screen and (max-width: 991px) {
    .app-main {
      padding-bottom: max(17px, calc(constant(safe-area-inset-bottom) + 10px));
      padding-bottom: max(17px, calc(env(safe-area-inset-bottom) + 10px));
      height: calc(100svh - 64px);
      height: calc(100dvh - 64px);
    }
  }
}
</style>

<style lang="scss">
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #F9F8F6;
}

::-webkit-scrollbar-thumb {
  background-color: #D9CFC7;
  border-radius: 3px;
}
</style>
