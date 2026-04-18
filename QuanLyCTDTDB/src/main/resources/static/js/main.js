/* =============================================================================
   main.js — He Thong Quan Ly Dao Tao Xuat Sac
   ============================================================================= */

document.addEventListener('DOMContentLoaded', function () {

    // =========================================================================
    // 1. Bootstrap Tooltips — khoi tao tat ca [data-bs-toggle="tooltip"]
    // =========================================================================
    var tooltipEls = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipEls.forEach(function (el) {
        new bootstrap.Tooltip(el, { trigger: 'hover' });
    });

    // =========================================================================
    // 2. Auto-dismiss alerts sau 4 giay
    // =========================================================================
    var autoDismissAlerts = document.querySelectorAll('.alert.auto-dismiss');
    autoDismissAlerts.forEach(function (alert) {
        setTimeout(function () {
            var bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 4000);
    });

    // =========================================================================
    // 3. Mobile Sidebar Toggle
    // =========================================================================
    var sidebarToggle = document.getElementById('sidebarToggleMobile');
    var sidebar = document.getElementById('sidebar');
    var overlay = document.getElementById('sidebarOverlay');

    if (sidebarToggle && sidebar && overlay) {
        sidebarToggle.addEventListener('click', function () {
            sidebar.classList.toggle('sidebar-open');
            overlay.classList.toggle('show');
        });
        overlay.addEventListener('click', function () {
            sidebar.classList.remove('sidebar-open');
            overlay.classList.remove('show');
        });
    }

    // =========================================================================
    // 4. Highlight active sidebar link (fallback neu Thymeleaf khong set)
    // =========================================================================
    var currentPath = window.location.pathname;
    var sidebarLinks = document.querySelectorAll('#sidebar .nav-link');
    sidebarLinks.forEach(function (link) {
        var href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/dashboard') {
            link.classList.add('active');
        }
    });

});

// =============================================================================
// 5. showLoading — disable button va hien spinner khi submit form
// =============================================================================
function showLoading(button) {
    var spinner = button.querySelector('.spinner-border');
    var icon = button.querySelector('i:not(.spinner-border)');
    if (spinner) spinner.classList.remove('d-none');
    if (icon) icon.classList.add('d-none');
    button.disabled = true;
    // Re-enable sau 15 giay de phong truong hop loi
    setTimeout(function () {
        button.disabled = false;
        if (spinner) spinner.classList.add('d-none');
        if (icon) icon.classList.remove('d-none');
    }, 15000);
    return true;
}

// =============================================================================
// 6. confirmDelete — xac nhan truoc khi submit form xoa
// =============================================================================
function confirmDelete(form, event, itemName) {
    event.preventDefault();
    var msg = itemName
        ? ('Ban co chac muon xoa "' + itemName + '"? Hanh dong nay khong the hoan tac.')
        : 'Ban co chac muon xoa muc nay? Hanh dong nay khong the hoan tac.';

    // Dung Bootstrap modal neu co, ngac khong dung confirm() trong JS thuan
    var modalEl = document.getElementById('confirmDeleteModal');
    if (modalEl) {
        document.getElementById('confirmDeleteModalLabel').textContent = 'Xac Nhan Xoa';
        document.getElementById('confirmDeleteModalBody').textContent = msg;
        var confirmBtn = document.getElementById('confirmDeleteBtn');
        confirmBtn.onclick = function () {
            form.submit();
        };
        var modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        modal.show();
    } else {
        // Fallback: browser confirm
        if (window.confirm(msg)) {
            form.submit();
        }
    }
    return false;
}

// =============================================================================
// 7. Format so trong input file de hien thi ten file da chon
// =============================================================================
document.addEventListener('DOMContentLoaded', function () {
    var fileInputs = document.querySelectorAll('input[type="file"]');
    fileInputs.forEach(function (input) {
        input.addEventListener('change', function () {
            var label = document.querySelector('label[for="' + input.id + '"]');
            if (label && input.files.length > 0) {
                var originalText = label.dataset.originalText;
                if (!originalText) {
                    label.dataset.originalText = label.textContent;
                }
                label.textContent = input.files[0].name;
            }
        });
    });
});

// =============================================================================
// 8. Preview anh truoc khi upload (neu co the element preview)
// =============================================================================
function previewImage(input, previewId) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            var preview = document.getElementById(previewId);
            if (preview) {
                preview.src = e.target.result;
                preview.classList.remove('d-none');
            }
        };
        reader.readAsDataURL(input.files[0]);
    }
}
