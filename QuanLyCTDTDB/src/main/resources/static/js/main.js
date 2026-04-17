/* ============================================================
   He Thong Quan Ly CTDT - Main JS
   Cac utility dung chung cho toan bo he thong
   ============================================================ */

'use strict';

/* ---------- CSRF Helper ----------
   Lay CSRF token tu meta tag (da inject trong base.html).
   Dung khi can gui AJAX fetch() voi method POST/PUT/DELETE.
   Vi du:
     fetch('/api/...', {
         method: 'POST',
         headers: csrfHeaders(),
         body: JSON.stringify(data)
     })
----------------------------------*/
function csrfToken() {
    return document.querySelector('meta[name="_csrf"]')?.content || '';
}
function csrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
}
function csrfHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    headers[csrfHeader()] = csrfToken();
    return headers;
}

/* ---------- Loading Overlay ----------
   Hien/an overlay loading khi submit form lon (import Excel, upload file).
   Them id="loadingOverlay" vao body trong page cu the de dung.
---------------------------------------*/
function showLoading() {
    const el = document.getElementById('loadingOverlay');
    if (el) el.classList.add('active');
}
function hideLoading() {
    const el = document.getElementById('loadingOverlay');
    if (el) el.classList.remove('active');
}

/* ---------- Confirm Delete ----------
   Gan vao nut xoa: onclick="return confirmDelete()"
   Tra ve false = huy submit.
---------------------------------------*/
function confirmDelete(msg) {
    return confirm(msg || 'Ban co chac muon xoa ban ghi nay?');
}

/* ---------- Toast Notification ----------
   Su dung Bootstrap 5 Toast API.
   Goi showToast('Luu thanh cong!', 'success')
   type: 'success' | 'danger' | 'warning' | 'info'
-------------------------------------------*/
function showToast(message, type) {
    const bgMap = {
        success: 'text-bg-success',
        danger:  'text-bg-danger',
        warning: 'text-bg-warning',
        info:    'text-bg-info'
    };
    const bgClass = bgMap[type] || 'text-bg-secondary';

    const toastHtml = `
        <div class="toast align-items-center ${bgClass} border-0 mb-2" role="alert" aria-live="assertive">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto"
                        data-bs-dismiss="toast"></button>
            </div>
        </div>`;

    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        container.style.zIndex = '9000';
        document.body.appendChild(container);
    }

    container.insertAdjacentHTML('beforeend', toastHtml);
    const toastEl  = container.lastElementChild;
    const bsToast  = new bootstrap.Toast(toastEl, { delay: 4000 });
    bsToast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
}

/* ---------- Auto-dismiss alerts ----------
   Alert co class .alert-dismissible se tu dong dong sau 5 giay.
   Da xu ly trong base.html, de day lam fallback.
-------------------------------------------*/
document.addEventListener('DOMContentLoaded', function () {
    // Tooltip Bootstrap 5
    const tooltipEls = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipEls.forEach(el => new bootstrap.Tooltip(el));

    // Popover Bootstrap 5
    const popoverEls = document.querySelectorAll('[data-bs-toggle="popover"]');
    popoverEls.forEach(el => new bootstrap.Popover(el));

    // Form submit loading: bat ki form co class .form-loading se hien overlay
    document.querySelectorAll('form.form-loading').forEach(function (form) {
        form.addEventListener('submit', function () {
            showLoading();
        });
    });

    // Active nav link: compare pathname
    const currentPath = window.location.pathname;
    document.querySelectorAll('.navbar-nav .nav-link').forEach(function (link) {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            link.classList.add('active');
        }
    });
});
