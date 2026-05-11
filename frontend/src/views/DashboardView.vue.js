import { computed, onMounted, ref } from 'vue';
import VChart from 'vue-echarts';
import { BarChart, PieChart } from 'echarts/charts';
import { GridComponent, LegendComponent, TooltipComponent, } from 'echarts/components';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { listProjectsApi } from '@/api/project';
use([CanvasRenderer, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent]);
const loading = ref(false);
const projects = ref([]);
const totalTasks = computed(() => projects.value.reduce((sum, item) => sum + item.totalTasks, 0));
const completedTasks = computed(() => projects.value.reduce((sum, item) => sum + item.completedTasks, 0));
const completionRate = computed(() => {
    if (!totalTasks.value) {
        return 0;
    }
    return Math.round((completedTasks.value / totalTasks.value) * 100);
});
const barOption = computed(() => ({
    tooltip: {},
    grid: { left: 32, right: 16, top: 24, bottom: 24 },
    xAxis: { type: 'category', data: projects.value.map((item) => item.code) },
    yAxis: { type: 'value' },
    series: [
        {
            name: '任务数',
            type: 'bar',
            data: projects.value.map((item) => item.totalTasks),
            itemStyle: { color: '#2563eb' },
            barWidth: 28,
        },
    ],
}));
const pieOption = computed(() => ({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
        {
            name: '完成情况',
            type: 'pie',
            radius: ['46%', '68%'],
            data: [
                { value: completedTasks.value, name: '已完成', itemStyle: { color: '#16a34a' } },
                { value: totalTasks.value - completedTasks.value, name: '未完成', itemStyle: { color: '#f59e0b' } },
            ],
        },
    ],
}));
async function loadProjects() {
    loading.value = true;
    try {
        projects.value = await listProjectsApi();
    }
    finally {
        loading.value = false;
    }
}
onMounted(loadProjects);
const __VLS_ctx = {
    ...{},
    ...{},
};
let __VLS_components;
let __VLS_intrinsics;
let __VLS_directives;
__VLS_asFunctionalElement1(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "page-stack" },
});
__VLS_asFunctionalDirective(__VLS_directives.vLoading, {})(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.loading) }, null, null);
/** @type {__VLS_StyleScopedClasses['page-stack']} */ ;
__VLS_asFunctionalElement1(__VLS_intrinsics.section, __VLS_intrinsics.section)({
    ...{ class: "metric-grid" },
});
/** @type {__VLS_StyleScopedClasses['metric-grid']} */ ;
let __VLS_0;
/** @ts-ignore @type { | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card'] | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card']} */
elCard;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent1(__VLS_0, new __VLS_0({
    shadow: "never",
    ...{ class: "metric-card" },
}));
const __VLS_2 = __VLS_1({
    shadow: "never",
    ...{ class: "metric-card" },
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
/** @type {__VLS_StyleScopedClasses['metric-card']} */ ;
const { default: __VLS_5 } = __VLS_3.slots;
__VLS_asFunctionalElement1(__VLS_intrinsics.span, __VLS_intrinsics.span)({});
__VLS_asFunctionalElement1(__VLS_intrinsics.strong, __VLS_intrinsics.strong)({});
(__VLS_ctx.projects.length);
// @ts-ignore
[vLoading, loading, projects,];
var __VLS_3;
let __VLS_6;
/** @ts-ignore @type { | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card'] | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card']} */
elCard;
// @ts-ignore
const __VLS_7 = __VLS_asFunctionalComponent1(__VLS_6, new __VLS_6({
    shadow: "never",
    ...{ class: "metric-card" },
}));
const __VLS_8 = __VLS_7({
    shadow: "never",
    ...{ class: "metric-card" },
}, ...__VLS_functionalComponentArgsRest(__VLS_7));
/** @type {__VLS_StyleScopedClasses['metric-card']} */ ;
const { default: __VLS_11 } = __VLS_9.slots;
__VLS_asFunctionalElement1(__VLS_intrinsics.span, __VLS_intrinsics.span)({});
__VLS_asFunctionalElement1(__VLS_intrinsics.strong, __VLS_intrinsics.strong)({});
(__VLS_ctx.totalTasks);
// @ts-ignore
[totalTasks,];
var __VLS_9;
let __VLS_12;
/** @ts-ignore @type { | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card'] | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card']} */
elCard;
// @ts-ignore
const __VLS_13 = __VLS_asFunctionalComponent1(__VLS_12, new __VLS_12({
    shadow: "never",
    ...{ class: "metric-card" },
}));
const __VLS_14 = __VLS_13({
    shadow: "never",
    ...{ class: "metric-card" },
}, ...__VLS_functionalComponentArgsRest(__VLS_13));
/** @type {__VLS_StyleScopedClasses['metric-card']} */ ;
const { default: __VLS_17 } = __VLS_15.slots;
__VLS_asFunctionalElement1(__VLS_intrinsics.span, __VLS_intrinsics.span)({});
__VLS_asFunctionalElement1(__VLS_intrinsics.strong, __VLS_intrinsics.strong)({});
(__VLS_ctx.completedTasks);
// @ts-ignore
[completedTasks,];
var __VLS_15;
let __VLS_18;
/** @ts-ignore @type { | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card'] | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card']} */
elCard;
// @ts-ignore
const __VLS_19 = __VLS_asFunctionalComponent1(__VLS_18, new __VLS_18({
    shadow: "never",
    ...{ class: "metric-card" },
}));
const __VLS_20 = __VLS_19({
    shadow: "never",
    ...{ class: "metric-card" },
}, ...__VLS_functionalComponentArgsRest(__VLS_19));
/** @type {__VLS_StyleScopedClasses['metric-card']} */ ;
const { default: __VLS_23 } = __VLS_21.slots;
__VLS_asFunctionalElement1(__VLS_intrinsics.span, __VLS_intrinsics.span)({});
__VLS_asFunctionalElement1(__VLS_intrinsics.strong, __VLS_intrinsics.strong)({});
(__VLS_ctx.completionRate);
// @ts-ignore
[completionRate,];
var __VLS_21;
__VLS_asFunctionalElement1(__VLS_intrinsics.section, __VLS_intrinsics.section)({
    ...{ class: "analytics-grid" },
});
/** @type {__VLS_StyleScopedClasses['analytics-grid']} */ ;
let __VLS_24;
/** @ts-ignore @type { | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card'] | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card']} */
elCard;
// @ts-ignore
const __VLS_25 = __VLS_asFunctionalComponent1(__VLS_24, new __VLS_24({
    shadow: "never",
}));
const __VLS_26 = __VLS_25({
    shadow: "never",
}, ...__VLS_functionalComponentArgsRest(__VLS_25));
const { default: __VLS_29 } = __VLS_27.slots;
{
    const { header: __VLS_30 } = __VLS_27.slots;
    // @ts-ignore
    [];
}
let __VLS_31;
/** @ts-ignore @type { | typeof __VLS_components.VChart} */
VChart;
// @ts-ignore
const __VLS_32 = __VLS_asFunctionalComponent1(__VLS_31, new __VLS_31({
    ...{ class: "chart" },
    option: (__VLS_ctx.barOption),
    autoresize: true,
}));
const __VLS_33 = __VLS_32({
    ...{ class: "chart" },
    option: (__VLS_ctx.barOption),
    autoresize: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_32));
/** @type {__VLS_StyleScopedClasses['chart']} */ ;
// @ts-ignore
[barOption,];
var __VLS_27;
let __VLS_36;
/** @ts-ignore @type { | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card'] | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card']} */
elCard;
// @ts-ignore
const __VLS_37 = __VLS_asFunctionalComponent1(__VLS_36, new __VLS_36({
    shadow: "never",
}));
const __VLS_38 = __VLS_37({
    shadow: "never",
}, ...__VLS_functionalComponentArgsRest(__VLS_37));
const { default: __VLS_41 } = __VLS_39.slots;
{
    const { header: __VLS_42 } = __VLS_39.slots;
    // @ts-ignore
    [];
}
let __VLS_43;
/** @ts-ignore @type { | typeof __VLS_components.VChart} */
VChart;
// @ts-ignore
const __VLS_44 = __VLS_asFunctionalComponent1(__VLS_43, new __VLS_43({
    ...{ class: "chart" },
    option: (__VLS_ctx.pieOption),
    autoresize: true,
}));
const __VLS_45 = __VLS_44({
    ...{ class: "chart" },
    option: (__VLS_ctx.pieOption),
    autoresize: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_44));
/** @type {__VLS_StyleScopedClasses['chart']} */ ;
// @ts-ignore
[pieOption,];
var __VLS_39;
let __VLS_48;
/** @ts-ignore @type { | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card'] | typeof __VLS_components.elCard | typeof __VLS_components.ElCard | typeof __VLS_components['el-card']} */
elCard;
// @ts-ignore
const __VLS_49 = __VLS_asFunctionalComponent1(__VLS_48, new __VLS_48({
    shadow: "never",
}));
const __VLS_50 = __VLS_49({
    shadow: "never",
}, ...__VLS_functionalComponentArgsRest(__VLS_49));
const { default: __VLS_53 } = __VLS_51.slots;
{
    const { header: __VLS_54 } = __VLS_51.slots;
    // @ts-ignore
    [];
}
let __VLS_55;
/** @ts-ignore @type { | typeof __VLS_components.elTable | typeof __VLS_components.ElTable | typeof __VLS_components['el-table'] | typeof __VLS_components.elTable | typeof __VLS_components.ElTable | typeof __VLS_components['el-table']} */
elTable;
// @ts-ignore
const __VLS_56 = __VLS_asFunctionalComponent1(__VLS_55, new __VLS_55({
    data: (__VLS_ctx.projects),
}));
const __VLS_57 = __VLS_56({
    data: (__VLS_ctx.projects),
}, ...__VLS_functionalComponentArgsRest(__VLS_56));
const { default: __VLS_60 } = __VLS_58.slots;
let __VLS_61;
/** @ts-ignore @type { | typeof __VLS_components.elTableColumn | typeof __VLS_components.ElTableColumn | typeof __VLS_components['el-table-column']} */
elTableColumn;
// @ts-ignore
const __VLS_62 = __VLS_asFunctionalComponent1(__VLS_61, new __VLS_61({
    prop: "name",
    label: "项目名称",
    minWidth: "180",
}));
const __VLS_63 = __VLS_62({
    prop: "name",
    label: "项目名称",
    minWidth: "180",
}, ...__VLS_functionalComponentArgsRest(__VLS_62));
let __VLS_66;
/** @ts-ignore @type { | typeof __VLS_components.elTableColumn | typeof __VLS_components.ElTableColumn | typeof __VLS_components['el-table-column']} */
elTableColumn;
// @ts-ignore
const __VLS_67 = __VLS_asFunctionalComponent1(__VLS_66, new __VLS_66({
    prop: "code",
    label: "编码",
    width: "120",
}));
const __VLS_68 = __VLS_67({
    prop: "code",
    label: "编码",
    width: "120",
}, ...__VLS_functionalComponentArgsRest(__VLS_67));
let __VLS_71;
/** @ts-ignore @type { | typeof __VLS_components.elTableColumn | typeof __VLS_components.ElTableColumn | typeof __VLS_components['el-table-column']} */
elTableColumn;
// @ts-ignore
const __VLS_72 = __VLS_asFunctionalComponent1(__VLS_71, new __VLS_71({
    prop: "ownerName",
    label: "负责人",
    width: "140",
}));
const __VLS_73 = __VLS_72({
    prop: "ownerName",
    label: "负责人",
    width: "140",
}, ...__VLS_functionalComponentArgsRest(__VLS_72));
let __VLS_76;
/** @ts-ignore @type { | typeof __VLS_components.elTableColumn | typeof __VLS_components.ElTableColumn | typeof __VLS_components['el-table-column'] | typeof __VLS_components.elTableColumn | typeof __VLS_components.ElTableColumn | typeof __VLS_components['el-table-column']} */
elTableColumn;
// @ts-ignore
const __VLS_77 = __VLS_asFunctionalComponent1(__VLS_76, new __VLS_76({
    label: "进度",
    minWidth: "180",
}));
const __VLS_78 = __VLS_77({
    label: "进度",
    minWidth: "180",
}, ...__VLS_functionalComponentArgsRest(__VLS_77));
const { default: __VLS_81 } = __VLS_79.slots;
{
    const { default: __VLS_82 } = __VLS_79.slots;
    const [{ row }] = __VLS_vSlot(__VLS_82);
    let __VLS_83;
    /** @ts-ignore @type { | typeof __VLS_components.elProgress | typeof __VLS_components.ElProgress | typeof __VLS_components['el-progress']} */
    elProgress;
    // @ts-ignore
    const __VLS_84 = __VLS_asFunctionalComponent1(__VLS_83, new __VLS_83({
        percentage: (Math.round((row.completedTasks / row.totalTasks) * 100)),
    }));
    const __VLS_85 = __VLS_84({
        percentage: (Math.round((row.completedTasks / row.totalTasks) * 100)),
    }, ...__VLS_functionalComponentArgsRest(__VLS_84));
    // @ts-ignore
    [projects,];
}
// @ts-ignore
[];
var __VLS_79;
// @ts-ignore
[];
var __VLS_58;
// @ts-ignore
[];
var __VLS_51;
// @ts-ignore
[];
const __VLS_export = (await import('vue')).defineComponent({});
export default {};
