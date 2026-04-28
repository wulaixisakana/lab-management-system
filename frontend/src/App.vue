<template>
    <router-view />
</template>

<script setup>
</script>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html, body, #app {
    height: 100%;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

@media (max-width: 768px) {
    .el-table {
        width: 100% !important;
        font-size: 12px;
    }

    .el-table__body-wrapper {
        overflow-x: auto !important;
        -webkit-overflow-scrolling: touch;
    }

    .el-table .el-table__cell {
        padding: 6px 0 !important;
    }

    .el-table .cell {
        padding: 0 6px !important;
        white-space: nowrap;
    }

    .el-form--inline .el-form-item {
        display: flex;
        margin-right: 0;
        margin-bottom: 10px;
        width: 100%;
    }

    .el-form--inline .el-form-item__content {
        flex: 1;
    }

    .el-form--inline {
        display: flex;
        flex-wrap: wrap;
        gap: 0;
    }

    .el-row {
        flex-direction: column;
    }

    .el-row .el-col {
        max-width: 100% !important;
        flex: 0 0 100% !important;
        margin-bottom: 12px;
    }

    .el-dialog {
        width: 95% !important;
        margin: 5vh auto !important;
    }

    .el-dialog .el-dialog__body {
        padding: 10px 15px !important;
    }

    .el-card {
        margin-bottom: 8px;
    }

    .el-card__header {
        padding: 10px 12px !important;
    }

    .card-header {
        flex-wrap: wrap;
        gap: 8px;
    }

    .card-header > div {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
    }

    .el-pagination {
        flex-wrap: wrap;
        justify-content: center !important;
    }

    .el-pagination .el-pagination__sizes {
        display: none;
    }

    .welcome-content {
        flex-direction: column;
        text-align: center;
        gap: 10px;
    }

    .clock {
        font-size: 24px !important;
    }

    .stat-content {
        flex-direction: row;
        text-align: left;
        gap: 10px;
    }

    .stat-icon {
        width: 44px !important;
        height: 44px !important;
        font-size: 22px !important;
    }

    .stat-info h3 {
        font-size: 20px !important;
    }

    .stat-info p {
        font-size: 12px !important;
    }

    .quick-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
    }

    .quick-actions .el-button {
        flex: 1;
        min-width: 100px;
        height: 40px !important;
        font-size: 13px !important;
    }

    .search-form .el-button {
        width: auto;
    }

    .el-button + .el-button {
        margin-left: 6px;
    }
}
</style>
