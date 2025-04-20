import { createRouter, createWebHistory} from "vue-router";

import Warshall from "@/pages/Warshall.vue";
import Dijkstra from "@/pages/Dijkstra.vue";
import MST from "@/pages/MST.vue";
import Huffman from "@/pages/Huffman.vue";
import function1 from "@/pages/function1.vue";
import function2 from "@/pages/function2.vue";
import function3 from "@/pages/function3.vue";
import BBG from "@/pages/BBG.vue";
import Login from "@/pages/Login.vue";
import Register from "@/pages/Register.vue";
import Profile from "@/pages/Profile.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name:'Home',
            component: function1
        },
        {
            path: '/warshall',
            component: Warshall
        },
        {
            path: '/dijkstra',
            component: Dijkstra
        },
        {
            path: '/MST',
            component: MST
        },
        {
            path: '/huffman',
            component: Huffman
        },
        {
            path: '/function1',
            component: function1
        },
        {
            path: '/function2',
            component: function2
        },
        {
            path: '/function3',
            component: function3
        },
        {
            path: '/BBG',
            component: BBG
        },
        {
            path: '/Login',
            component: Login
        },
        {
            path: '/Register',
            component: Register
        },
        {
            path: '/Profile',
            component: Profile
        },
    ]
})

export default router;