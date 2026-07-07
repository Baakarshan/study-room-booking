<template>
  <header class="top-nav">
    <div class="nav-brand">
      <router-link to="/" class="brand-link">
        <span class="brand-name">{{ title }}</span>
      </router-link>
    </div>

    <nav v-if="appStore.device !== 'mobile'" class="nav-menu">
      <router-link
        v-for="item in flatMenuItems"
        :key="item.path"
        :to="item.path"
        :class="['nav-link', { active: isActive(item.path) }]"
      >
        <svg-icon v-if="item.icon" :icon-class="item.icon" class="nav-icon" />
        <span>{{ item.title }}</span>
      </router-link>
    </nav>

    <div class="nav-actions">
      <el-dropdown @command="handleCommand" trigger="click">
        <div class="user-trigger">
          <img :src="userStore.avatar" class="user-avatar" />
          <span class="user-name">{{ userStore.nickName }}</span>
          <el-icon><arrow-down /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import useAppStore from '@/store/modules/app'
import useUserStore from '@/store/modules/user'
import usePermissionStore from '@/store/modules/permission'

const title = import.meta.env.VITE_APP_TITLE
const appStore = useAppStore()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const route = useRoute()
const router = useRouter()

const flatMenuItems = computed(() => {
  const items = []
  const routes = permissionStore.sidebarRouters.filter(r => !r.hidden && r.path !== '/redirect')

  routes.forEach(r => {
    if (!r.children || r.children.length === 0) {
      items.push({
        path: resolvePath(r.path),
        title: r.meta?.title || r.name,
        icon: r.meta?.icon
      })
    } else {
      r.children.filter(c => !c.hidden).forEach(child => {
        items.push({
          path: resolveChildPath(r.path, child.path),
          title: child.meta?.title || child.name,
          icon: child.meta?.icon
        })
      })
    }
  })

  return items
})

function resolvePath(path) {
  if (!path) return '/'
  return path.startsWith('/') ? path : '/' + path
}

function resolveChildPath(parentPath, childPath) {
  if (childPath.startsWith('/')) return childPath
  const parent = resolvePath(parentPath)
  const child = '/' + childPath
  return parent + child
}

function isActive(path) {
  return route.path === path || route.path.startsWith(path + '/')
}

function handleCommand(command) {
  if (command === 'logout') {
    ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logOut().then(() => {
        location.href = '/index'
      })
    }).catch(() => {})
  }
}
</script>

<style lang="scss" scoped>
.top-nav {
  height: 64px;
  padding: 0 32px;
  background: #F9F8F6;
  border-bottom: 1px solid #EFE9E3;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  position: relative;
  z-index: 10;
}

.nav-brand {
  flex-shrink: 0;
}

.brand-link {
  display: flex;
  align-items: center;
  text-decoration: none;

  .brand-name {
    font-size: 18px;
    font-weight: 700;
    color: #2C2621;
    letter-spacing: 0;
  }
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  justify-content: center;
  overflow-x: auto;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #6B6560;
  text-decoration: none;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.2s ease;

  .nav-icon {
    font-size: 16px;
  }

  &:hover {
    color: #2C2621;
    background: #EFE9E3;
  }

  &.active {
    background: #2C2621;
    color: #ffffff;
    font-weight: 600;
  }
}

.dropdown-icon {
  font-size: 12px;
  margin-left: 2px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  cursor: pointer;
  color: #6B6560;
  transition: all 0.2s ease;

  &:hover {
    background: #EFE9E3;
    color: #2C2621;
  }
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: #EFE9E3;
  }

  .user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
  }

  .user-name {
    font-size: 14px;
    font-weight: 500;
    color: #2C2621;
  }
}

.menu-icon {
  margin-right: 6px;
}

@media (max-width: 768px) {
  .top-nav {
    padding: 0 16px;
  }

  .nav-menu {
    display: none;
  }
}
</style>
