import Login from './views/Login.vue'
import Mobile from './views/Mobile.vue'
import Health from './views/Health.vue'
import Home from './views/Home.vue'
import NotFound from './views/404.vue'

import Dashboard from './views/dashbord/DashBoard.vue'

import User from './views/acl/User.vue'
import Role from './views/acl/Role.vue'
import Permission from './views/acl/Permission.vue'

import Collection from './views/collection/Index.vue'
import Search from './views/collection/KeyWorld.vue'

let routes = [{
        path: '/',
        component: Login,
        name: '',
        hidden: true
    },
    {
        path: '/404',
        component: NotFound,
        name: '',
        hidden: true
    },
    {
        path: '/mobile/:id',
        component: Mobile,
        name: '手机验证',
        hidden: true
    },
    {
        path: '/health/:id',
        component: Health,
        name: '健身卡',
        hidden: true
    },
    {
        path: '/',
        component: Home,
        name: '主页',
        leaf: true,
        iconCls: 'el-icon-fa-dashboard', // 图标样式class
        children: [{
            path: '/dashboard',
            iconCls: 'el-icon-fa-dashboard',
            component: Dashboard,
            name: 'Dashboard'
        }]
    },
    {
        path: '/',
        component: Home,
        name: '访问控制',
        iconCls: 'el-icon-fa-users', // 图标样式class
        children: [{
                path: '/user',
                iconCls: 'el-icon-fa-user',
                component: User,
                meta: {
                    p: 'user.list'
                },
                name: '用户管理'
            },
            {
                path: '/role',
                iconCls: 'el-icon-fa-lock',
                component: Role,
                meta: {
                    p: 'role.list'
                },
                name: '角色管理'
            },
            {
                path: '/permission',
                iconCls: 'el-icon-fa-eye',
                component: Permission,
                meta: {
                    p: 'permission.list'
                },
                name: '权限管理'
            }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '',
        iconCls: 'el-icon-fa-cogs',
        name: '运营需求',
        children: [{
            path: '/collection',
            component: Collection,
            iconCls: 'el-icon-fa-cog',
            meta: {
                p: 'collection.list'
            },
            name: '图片采集'
        },
        {
            path: '/collection/search',
            component: Search,
            iconCls: 'el-icon-fa-cog',
            meta: {
                p: 'search.list'
            },
            name: '关键词搜索'
        }
        ]
    }
 ];

export default routes;