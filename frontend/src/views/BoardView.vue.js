import { Filter, Plus, Refresh } from '@element-plus/icons-vue';
import { onMounted, ref } from 'vue';
import { getProjectBoardApi } from '@/api/project';
const loading = ref(false);
const columns = ref([]);
const priorityMap = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info',
};
async function loadBoard() {
    loading.value = true;
    try {
        columns.value = await getProjectBoardApi(1);
    }
    finally {
        loading.value = false;
    }
}
onMounted(loadBoard);
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
    ...{ class: "toolbar-row" },
});
/** @type {__VLS_StyleScopedClasses['toolbar-row']} */ ;
__VLS_asFunctionalElement1(__VLS_intrinsics.div, __VLS_intrinsics.div)({});
__VLS_asFunctionalElement1(__VLS_intrinsics.h2, __VLS_intrinsics.h2)({});
__VLS_asFunctionalElement1(__VLS_intrinsics.p, __VLS_intrinsics.p)({});
__VLS_asFunctionalElement1(__VLS_intrinsics.div, __VLS_intrinsics.div)({
    ...{ class: "toolbar-actions" },
});
/** @type {__VLS_StyleScopedClasses['toolbar-actions']} */ ;
let __VLS_0;
/** @ts-ignore @type { | typeof __VLS_components.elButton | typeof __VLS_components.ElButton | typeof __VLS_components['el-button'] | typeof __VLS_components.elButton | typeof __VLS_components.ElButton | typeof __VLS_components['el-button']} */
elButton;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent1(__VLS_0, new __VLS_0({
    icon: (__VLS_ctx.Filter),
}));
const __VLS_2 = __VLS_1({
    icon: (__VLS_ctx.Filter),
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
const { default: __VLS_5 } = __VLS_3.slots;
// @ts-ignore
[vLoading, loading, Filter,];
var __VLS_3;
let __VLS_6;
/** @ts-ignore @type { | typeof __VLS_components.elButton | typeof __VLS_components.ElButton | typeof __VLS_components['el-button'] | typeof __VLS_components.elButton | typeof __VLS_components.ElButton | typeof __VLS_components['el-button']} */
elButton;
// @ts-ignore
const __VLS_7 = __VLS_asFunctionalComponent1(__VLS_6, new __VLS_6({
    ...{ 'onClick': {} },
    icon: (__VLS_ctx.Refresh),
}));
const __VLS_8 = __VLS_7({
    ...{ 'onClick': {} },
    icon: (__VLS_ctx.Refresh),
}, ...__VLS_functionalComponentArgsRest(__VLS_7));
let __VLS_11;
const __VLS_12 = ({ click: {} },
    { onClick: (__VLS_ctx.loadBoard) });
const { default: __VLS_13 } = __VLS_9.slots;
// @ts-ignore
[Refresh, loadBoard,];
var __VLS_9;
var __VLS_10;
let __VLS_14;
/** @ts-ignore @type { | typeof __VLS_components.elButton | typeof __VLS_components.ElButton | typeof __VLS_components['el-button'] | typeof __VLS_components.elButton | typeof __VLS_components.ElButton | typeof __VLS_components['el-button']} */
elButton;
// @ts-ignore
const __VLS_15 = __VLS_asFunctionalComponent1(__VLS_14, new __VLS_14({
    type: "primary",
    icon: (__VLS_ctx.Plus),
}));
const __VLS_16 = __VLS_15({
    type: "primary",
    icon: (__VLS_ctx.Plus),
}, ...__VLS_functionalComponentArgsRest(__VLS_15));
const { default: __VLS_19 } = __VLS_17.slots;
// @ts-ignore
[Plus,];
var __VLS_17;
__VLS_asFunctionalElement1(__VLS_intrinsics.section, __VLS_intrinsics.section)({
    ...{ class: "board-grid" },
});
/** @type {__VLS_StyleScopedClasses['board-grid']} */ ;
for (const [column] of __VLS_vFor((__VLS_ctx.columns))) {
    __VLS_asFunctionalElement1(__VLS_intrinsics.div, __VLS_intrinsics.div)({
        key: (column.key),
        ...{ class: "board-column" },
    });
    /** @type {__VLS_StyleScopedClasses['board-column']} */ ;
    __VLS_asFunctionalElement1(__VLS_intrinsics.header, __VLS_intrinsics.header)({});
    __VLS_asFunctionalElement1(__VLS_intrinsics.strong, __VLS_intrinsics.strong)({});
    (column.title);
    let __VLS_20;
    /** @ts-ignore @type { | typeof __VLS_components.elTag | typeof __VLS_components.ElTag | typeof __VLS_components['el-tag'] | typeof __VLS_components.elTag | typeof __VLS_components.ElTag | typeof __VLS_components['el-tag']} */
    elTag;
    // @ts-ignore
    const __VLS_21 = __VLS_asFunctionalComponent1(__VLS_20, new __VLS_20({
        round: true,
    }));
    const __VLS_22 = __VLS_21({
        round: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_21));
    const { default: __VLS_25 } = __VLS_23.slots;
    (column.tasks.length);
    // @ts-ignore
    [columns,];
    var __VLS_23;
    for (const [task] of __VLS_vFor((column.tasks))) {
        __VLS_asFunctionalElement1(__VLS_intrinsics.article, __VLS_intrinsics.article)({
            key: (task.id),
            ...{ class: "task-card" },
        });
        /** @type {__VLS_StyleScopedClasses['task-card']} */ ;
        __VLS_asFunctionalElement1(__VLS_intrinsics.div, __VLS_intrinsics.div)({
            ...{ class: "task-title" },
        });
        /** @type {__VLS_StyleScopedClasses['task-title']} */ ;
        (task.title);
        __VLS_asFunctionalElement1(__VLS_intrinsics.div, __VLS_intrinsics.div)({
            ...{ class: "task-meta" },
        });
        /** @type {__VLS_StyleScopedClasses['task-meta']} */ ;
        let __VLS_26;
        /** @ts-ignore @type { | typeof __VLS_components.elTag | typeof __VLS_components.ElTag | typeof __VLS_components['el-tag'] | typeof __VLS_components.elTag | typeof __VLS_components.ElTag | typeof __VLS_components['el-tag']} */
        elTag;
        // @ts-ignore
        const __VLS_27 = __VLS_asFunctionalComponent1(__VLS_26, new __VLS_26({
            type: (__VLS_ctx.priorityMap[task.priority]),
            size: "small",
        }));
        const __VLS_28 = __VLS_27({
            type: (__VLS_ctx.priorityMap[task.priority]),
            size: "small",
        }, ...__VLS_functionalComponentArgsRest(__VLS_27));
        const { default: __VLS_31 } = __VLS_29.slots;
        (task.priority);
        // @ts-ignore
        [priorityMap,];
        var __VLS_29;
        __VLS_asFunctionalElement1(__VLS_intrinsics.span, __VLS_intrinsics.span)({});
        (task.assigneeName);
        __VLS_asFunctionalElement1(__VLS_intrinsics.div, __VLS_intrinsics.div)({
            ...{ class: "task-footer" },
        });
        /** @type {__VLS_StyleScopedClasses['task-footer']} */ ;
        __VLS_asFunctionalElement1(__VLS_intrinsics.span, __VLS_intrinsics.span)({});
        (task.dueDate);
        __VLS_asFunctionalElement1(__VLS_intrinsics.div, __VLS_intrinsics.div)({});
        for (const [label] of __VLS_vFor((task.labels))) {
            let __VLS_32;
            /** @ts-ignore @type { | typeof __VLS_components.elTag | typeof __VLS_components.ElTag | typeof __VLS_components['el-tag'] | typeof __VLS_components.elTag | typeof __VLS_components.ElTag | typeof __VLS_components['el-tag']} */
            elTag;
            // @ts-ignore
            const __VLS_33 = __VLS_asFunctionalComponent1(__VLS_32, new __VLS_32({
                key: (label),
                size: "small",
                effect: "plain",
            }));
            const __VLS_34 = __VLS_33({
                key: (label),
                size: "small",
                effect: "plain",
            }, ...__VLS_functionalComponentArgsRest(__VLS_33));
            const { default: __VLS_37 } = __VLS_35.slots;
            (label);
            // @ts-ignore
            [];
            var __VLS_35;
            // @ts-ignore
            [];
        }
        // @ts-ignore
        [];
    }
    // @ts-ignore
    [];
}
// @ts-ignore
[];
const __VLS_export = (await import('vue')).defineComponent({});
export default {};
