<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link href="https://cdn.jsdelivr.net/npm/daisyui@5" rel="stylesheet" type="text/css" />
<script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
<script src="https://code.jquery.com/jquery-4.0.0.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css">

<style type="text/tailwindcss">
    @theme {
        --color-brand-50: #eff6ff;
        --color-brand-100: #dbeafe;
        --color-brand-500: #3b82f6;
        --color-brand-600: #2563eb;
        --color-brand-700: #1d4ed8;
        --color-point-500: #f59e0b;
    }

    @layer base {
        body { @apply bg-slate-50 text-slate-800; }
    }

    @layer components {
        .page-wrap { @apply max-w-6xl w-full mx-auto px-4 py-8; }
        .section-title { @apply text-2xl font-bold mb-2; }
        .section-desc { @apply text-sm text-slate-500 mb-6; }
        .content-card { @apply bg-white border border-slate-200 rounded-2xl shadow-sm; }
        .card-pad { @apply p-5 md:p-6; }
        .form-label { @apply block text-sm font-semibold mb-2 text-slate-700; }
        .btn-brand { @apply text-white bg-brand-600 hover:bg-brand-700 border-0; }
        .btn-soft-brand { @apply bg-brand-50 text-brand-700 border border-brand-100 hover:bg-brand-100; }
        .chip { @apply inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold; }
        .chip-blue { @apply bg-blue-50 text-blue-700; }
        .chip-amber { @apply bg-amber-50 text-amber-700; }
        .chip-emerald { @apply bg-emerald-50 text-emerald-700; }

       /* 페이징 바 */
        .pagination-wrap {
            @apply flex items-center border border-slate-200 rounded-lg overflow-hidden shadow-sm bg-white w-fit mx-auto;
        }

        .pagination-wrap a, 
        .pagination-wrap span {
            @apply h-9 px-4 flex items-center justify-center text-sm font-semibold text-slate-600 border-r last:border-r-0 border-slate-200 hover:bg-slate-50 transition-all;
            text-decoration: none;
        }

        /* 현재 페이지 (활성화) - !important 에러 해결 */
        .pagination-wrap span,
        .pagination-wrap .active {
            @apply bg-brand-600 text-white;
            /* !important가 필요하다면 @apply 밖에서 표준 CSS로 작성해야 함 */
            background-color: #2563eb !important;
            color: white !important;
        }
    }
</style>
<style>
    body {
      font-family: "Pretendard", sans-serif;
    }
</style>

<link rel="stylesheet" type="text/tailwindcss" href="/teamtwo/asset/css/main.css" />
<link rel="icon" type="image/png" href="/teamtwo/asset/pic/favilogo.png">