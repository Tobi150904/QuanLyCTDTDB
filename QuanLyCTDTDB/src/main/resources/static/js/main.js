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
        // A11y: them aria-current cho link active (ke ca neu Thymeleaf da set
        // class 'active' qua activeMenu — tranh phai sua tat ca template)
        if (link.classList.contains('active')) {
            link.setAttribute('aria-current', 'page');
        }
    });

});

// =============================================================================
// 5. showLoading — disable button va hien spinner khi submit form
// =============================================================================
//
// FIX BUG: truoc day set `button.disabled = true` DONG BO trong onclick
// lam browser bo qua default action "submit form" (HTML spec: submit button
// bi disabled KHONG the kich hoat submit). Hau qua: form khong bao gio duoc
// submit, khong co POST den server, log server sach tuyet doi, browser
// console cung sach (vi day khong phai JS error) — user tuong app hong.
//
// Fix: dung setTimeout(..., 0) de disable o MICROTASK KE TIEP, sau khi trinh
// duyet da bat dau submit form. Spinner van hien thi, va double-click van
// duoc ngan (vi tick ke tiep button da disabled). Form van submit binh thuong.
function showLoading(button) {
    var spinner = button.querySelector('.spinner-border');
    var icon = button.querySelector('i:not(.spinner-border)');
    if (spinner) spinner.classList.remove('d-none');
    if (icon) icon.classList.add('d-none');

    // Defer disable sang tick sau — cho phep form submit chay truoc.
    setTimeout(function () {
        button.disabled = true;
    }, 0);

    // Re-enable sau 15 giay de phong truong hop loi network.
    setTimeout(function () {
        button.disabled = false;
        if (spinner) spinner.classList.add('d-none');
        if (icon) icon.classList.remove('d-none');
    }, 15000);
    return true;
}

// =============================================================================
// 6. confirmDelete / confirmAction — xac nhan truoc khi submit form
// Cac action ho tro:
//   - (mac dinh)         -> Xac Nhan Xoa | btn-danger | bi-trash | \"Xoa\"
//   - \"phe duyet\"        -> Xac Nhan Phe Duyet | btn-success | bi-check2-circle
//   - \"gui duyet\"        -> Xac Nhan Gui Duyet | btn-primary | bi-send
//   - \"huy\"              -> Xac Nhan Huy | btn-warning | bi-x-circle
function confirmDeleteFromForm(form, event) {
    var name   = form.getAttribute('data-item-name');
    var action = form.getAttribute('data-confirm-action'); // optional
    return confirmDelete(form, event, name, action);
}

// Profile dinh nghia text + class theo action key (loai bo dau de match
// du user nhap \"Phe Duyet\" hay \"phe duyet\").
var CONFIRM_ACTION_PROFILES = {
    'phe duyet': {
        title:    'Xác Nhận Phê Duyệt',
        verb:     'phê duyệt',
        btnText:  'Phê Duyệt',
        btnClass: 'btn btn-sm btn-success',
        btnIcon:  'bi-check2-circle'
    },
    'gui duyet': {
        title:    'Xác Nhận Gửi Duyệt',
        verb:     'gửi duyệt',
        btnText:  'Gửi Duyệt',
        btnClass: 'btn btn-sm btn-primary',
        btnIcon:  'bi-send'
    },
    'huy': {
        title:    'Xác Nhận Huỷ',
        verb:     'huỷ',
        btnText:  'Huỷ Bỏ',
        btnClass: 'btn btn-sm btn-warning',
        btnIcon:  'bi-x-circle'
    }
};

var CONFIRM_DELETE_PROFILE = {
    title:    'Xác Nhận Xoá',
    verb:     'xoá',
    btnText:  'Xoá',
    btnClass: 'btn btn-sm btn-danger',
    btnIcon:  'bi-trash'
};

function _normalizeAction(s) {
    return (s || '').toString().toLowerCase()
        .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
        .replace(/đ/g, 'd').trim();
}

function confirmDelete(form, event, itemName, action) {
    event.preventDefault();

    var key = _normalizeAction(action);
    var profile = CONFIRM_ACTION_PROFILES[key] || CONFIRM_DELETE_PROFILE;

    var msg = itemName
        ? ('Bạn có chắc muốn ' + profile.verb + ' \"' + itemName + '\"?'
           + (profile === CONFIRM_DELETE_PROFILE ? ' Hành động này không thể hoàn tác.' : ''))
        : ('Bạn có chắc muốn ' + profile.verb + ' mục này?'
           + (profile === CONFIRM_DELETE_PROFILE ? ' Hành động này không thể hoàn tác.' : ''));

    // Dung Bootstrap modal neu co, neu khong dung confirm() trong JS thuan
    var modalEl = document.getElementById('confirmDeleteModal');
    if (modalEl) {
        document.getElementById('confirmDeleteModalLabel').textContent = profile.title;
        document.getElementById('confirmDeleteModalBody').textContent = msg;
        var confirmBtn = document.getElementById('confirmDeleteBtn');
        confirmBtn.className = profile.btnClass;
        confirmBtn.innerHTML = '<i class=\"bi ' + profile.btnIcon
                             + ' me-1\" aria-hidden=\"true\"></i>' + profile.btnText;
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

// =============================================================================
// PHASE 1 — FOUNDATION JS EXTENSIONS
// =============================================================================
//
// Cac module them vao mot lan tai foundation. Khong pha vo bat ky helper cu nao.
// Cac module:
//   - window.Toast        : Toast.success/error/info/warning(msg, opts)
//   - window.BulkSelect   : init checkbox-row + checkbox-all + bulk bar
//   - window.SortableTable: init sortable column header
//   - window.UnsavedGuard : warn truoc khi user roi trang co form unsaved
//
// =============================================================================

(function () {
    'use strict';

    // -------------------------------------------------------------------------
    // Toast helper — wrap Bootstrap Toast voi API quen thuoc Toast.success(msg).
    // -------------------------------------------------------------------------
    var ICONS = {
        success: 'bi-check-circle-fill',
        error:   'bi-exclamation-triangle-fill',
        danger:  'bi-exclamation-triangle-fill',
        info:    'bi-info-circle-fill',
        warning: 'bi-exclamation-circle-fill'
    };

    function showToast(level, msg, opts) {
        if (!msg) return;
        opts = opts || {};
        var stack = document.getElementById('toastStack');
        if (!stack) {
            console.warn('[QLCTDT] Toast: #toastStack khong tim thay trong DOM');
            return;
        }
        var iconCls = ICONS[level] || ICONS.info;
        var lvlCls  = 'toast-' + level;
        var delay   = opts.delay != null ? opts.delay
                    : (level === 'error' || level === 'danger' ? 8000 : 4500);

        // Build toast element
        var toastEl = document.createElement('div');
        toastEl.className = 'toast ' + lvlCls;
        toastEl.setAttribute('role', level === 'error' || level === 'danger'
                                      ? 'alert' : 'status');
        toastEl.setAttribute('aria-live',
            level === 'error' || level === 'danger' ? 'assertive' : 'polite');
        toastEl.setAttribute('aria-atomic', 'true');

        var body = document.createElement('div');
        body.className = 'd-flex align-items-start';
        body.innerHTML =
            '<div class="toast-body flex-grow-1">' +
            '  <i class="bi ' + iconCls + '" aria-hidden="true"></i>' +
            '  <span></span>' +
            '</div>' +
            '<button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Đóng"></button>';
        // Inject text content safely (avoid HTML injection)
        body.querySelector('span').textContent = msg;
        toastEl.appendChild(body);

        stack.prepend(toastEl);

        // Activate via Bootstrap
        if (window.bootstrap && bootstrap.Toast) {
            var bsToast = bootstrap.Toast.getOrCreateInstance(toastEl, {
                autohide: opts.autohide !== false,
                delay: delay
            });
            toastEl.addEventListener('hidden.bs.toast', function () {
                toastEl.remove();
            }, { once: true });
            bsToast.show();
        } else {
            toastEl.classList.add('show');
            setTimeout(function () { toastEl.remove(); }, delay);
        }
    }

    window.Toast = {
        success: function (msg, opts) { showToast('success', msg, opts); },
        error:   function (msg, opts) { showToast('error',   msg, opts); },
        danger:  function (msg, opts) { showToast('danger',  msg, opts); },
        info:    function (msg, opts) { showToast('info',    msg, opts); },
        warning: function (msg, opts) { showToast('warning', msg, opts); }
    };

    // -------------------------------------------------------------------------
    // Bridge — convert flash banner co [data-toast="..."] thanh toast,
    // an banner di. Chi ap dung cho success/info (neu user prefer pattern do).
    // Error/warning van giu o banner vi co the dai va can context.
    // -------------------------------------------------------------------------
    document.addEventListener('DOMContentLoaded', function () {
        var bridges = document.querySelectorAll('.alert[data-toast]');
        bridges.forEach(function (alert) {
            var level = alert.getAttribute('data-toast') || 'info';
            var msgEl = alert.querySelector('span');
            var msg = msgEl ? msgEl.textContent.trim() : '';
            if (msg) {
                window.Toast[level === 'success' ? 'success'
                          : level === 'info'    ? 'info'
                          : level === 'warning' ? 'warning'
                          : 'info'](msg);
            }
            // Hide banner ngay (toast da thay the)
            alert.remove();
        });
    });

    // -------------------------------------------------------------------------
    // BulkSelect — quan ly checkbox row + checkbox header + bulk bar.
    //
    // Init: BulkSelect.init({
    //   tableSelector: '#tbl',
    //   checkboxSelector: 'input[name="selectedIds"]',
    //   selectAllSelector: '#selectAll',
    //   bulkBarId: 'bulkBar',
    //   countId: 'bulkBarCount',
    //   clearBtnId: 'bulkClearBtn',
    //   bulkFormId: 'bulkDeleteForm',     // optional — neu co form bulk delete
    //   inputName: 'ids'                  // hidden inputs name khi submit
    // });
    //
    // Khong tu tim DOM neu khong duoc init — tranh chi phi tren trang khong dung.
    // -------------------------------------------------------------------------
    var BulkSelect = (function () {
        var cfg = null;
        var rowChecks = [];
        var selectAll = null;
        var bulkBar = null;
        var countEl = null;
        var bulkForm = null;

        function selectedIds() {
            return rowChecks.filter(function (c) { return c.checked; })
                            .map(function (c) { return c.value; });
        }

        function refresh() {
            var selected = selectedIds();
            var n = selected.length;

            if (countEl) countEl.textContent = n;

            // Header checkbox indeterminate state
            if (selectAll) {
                if (n === 0) {
                    selectAll.checked = false;
                    selectAll.indeterminate = false;
                } else if (n === rowChecks.length) {
                    selectAll.checked = true;
                    selectAll.indeterminate = false;
                } else {
                    selectAll.checked = false;
                    selectAll.indeterminate = true;
                }
            }

            // Bulk bar visibility
            if (bulkBar) {
                if (n > 0) {
                    bulkBar.removeAttribute('hidden');
                    bulkBar.setAttribute('data-state', 'visible');
                    bulkBar.classList.add('show');
                } else {
                    bulkBar.setAttribute('data-state', 'hidden');
                    bulkBar.classList.remove('show');
                    // Cho transition out xong roi an hoan toan
                    setTimeout(function () {
                        if (bulkBar.getAttribute('data-state') === 'hidden') {
                            bulkBar.setAttribute('hidden', '');
                        }
                    }, 220);
                }
            }
        }

        function clearAll() {
            rowChecks.forEach(function (c) { c.checked = false; });
            refresh();
        }

        function fillBulkFormHiddenInputs() {
            if (!bulkForm) return;
            // Xoa cac hidden input cu (cua lan submit truoc neu co)
            bulkForm.querySelectorAll('input.bulk-id-injected').forEach(function (n) {
                n.remove();
            });
            var ids = selectedIds();
            ids.forEach(function (id) {
                var inp = document.createElement('input');
                inp.type = 'hidden';
                inp.name = cfg.inputName || 'ids';
                inp.value = id;
                inp.className = 'bulk-id-injected';
                bulkForm.appendChild(inp);
            });
            // Update item-name de confirm modal hien dung so luong
            bulkForm.setAttribute('data-item-name', ids.length + ' mục đã chọn');
        }

        function init(options) {
            cfg = options || {};
            var table = document.querySelector(cfg.tableSelector || 'table');
            if (!table) return;
            rowChecks = Array.prototype.slice.call(
                table.querySelectorAll(cfg.checkboxSelector || 'input[type="checkbox"][name="selectedIds"]')
            );
            if (rowChecks.length === 0) return;

            selectAll = document.querySelector(cfg.selectAllSelector || '#selectAll');
            bulkBar   = document.getElementById(cfg.bulkBarId   || 'bulkBar');
            countEl   = document.getElementById(cfg.countId     || 'bulkBarCount');
            bulkForm  = cfg.bulkFormId ? document.getElementById(cfg.bulkFormId) : null;

            // Wire checkboxes
            rowChecks.forEach(function (c) {
                c.addEventListener('change', refresh);
            });
            if (selectAll) {
                selectAll.addEventListener('change', function () {
                    rowChecks.forEach(function (c) { c.checked = selectAll.checked; });
                    refresh();
                });
            }

            // Clear button
            var clearBtn = document.getElementById(cfg.clearBtnId || 'bulkClearBtn');
            if (clearBtn) {
                clearBtn.addEventListener('click', function () { clearAll(); });
            }

            // Bulk form submit — fill hidden inputs, intercept BEFORE confirmDelete.
            if (bulkForm) {
                bulkForm.addEventListener('submit', function (e) {
                    // Fill hidden inputs ngay
                    fillBulkFormHiddenInputs();
                    if (selectedIds().length === 0) {
                        e.preventDefault();
                        window.Toast.warning('Chưa có mục nào được chọn');
                        return false;
                    }
                    return true;
                }, true); // capture so chay TRUOC confirmDeleteFromForm
            }

            refresh();
        }

        return { init: init, clear: clearAll, refresh: refresh,
                 selected: selectedIds };
    })();

    window.BulkSelect = BulkSelect;

    // -------------------------------------------------------------------------
    // SortableTable — them class .sortable-th vao th va data-sort-key="cot".
    // Click se submit form GET voi param "sort=key,asc/desc". Server phai
    // ho tro Spring Data Pageable Sort.
    //
    // Markup mau:
    //   <table data-sortable
    //          data-sort-current-key="hoTen"
    //          data-sort-current-dir="asc">
    //     <thead><tr>
    //       <th class="sortable-th" data-sort-key="hoTen">Họ tên</th>
    //       ...
    //     </tr></thead>
    //   </table>
    //
    // Khi click, JS xay dung URL moi (giu lai cac query param khac) va navigate.
    // -------------------------------------------------------------------------
    var SortableTable = {
        init: function (tableEl) {
            var table = (typeof tableEl === 'string')
                ? document.querySelector(tableEl)
                : tableEl;
            if (!table) return;

            var currentKey = table.getAttribute('data-sort-current-key') || '';
            var currentDir = table.getAttribute('data-sort-current-dir') || 'asc';

            var ths = table.querySelectorAll('th.sortable-th[data-sort-key]');
            ths.forEach(function (th) {
                var key = th.getAttribute('data-sort-key');
                if (key === currentKey) {
                    th.classList.add(currentDir === 'desc' ? 'sort-desc' : 'sort-asc');
                    th.setAttribute('aria-sort',
                        currentDir === 'desc' ? 'descending' : 'ascending');
                } else {
                    th.setAttribute('aria-sort', 'none');
                }

                // A11y: lam th clickable bang keyboard
                if (!th.hasAttribute('tabindex')) th.setAttribute('tabindex', '0');
                if (!th.hasAttribute('role'))     th.setAttribute('role', 'columnheader');

                function trigger() {
                    var nextDir = (key === currentKey && currentDir === 'asc') ? 'desc' : 'asc';
                    var url = new URL(window.location.href);
                    url.searchParams.set('sort', key + ',' + nextDir);
                    url.searchParams.set('page', '0'); // reset ve trang 1
                    window.location.href = url.toString();
                }
                th.addEventListener('click', trigger);
                th.addEventListener('keydown', function (e) {
                    if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        trigger();
                    }
                });
            });
        },
        autoInit: function () {
            document.querySelectorAll('table[data-sortable]').forEach(function (t) {
                SortableTable.init(t);
            });
        }
    };
    window.SortableTable = SortableTable;
    document.addEventListener('DOMContentLoaded', SortableTable.autoInit);

    // -------------------------------------------------------------------------
    // UnsavedGuard — warn user truoc khi rời trang khi form co thay doi.
    //
    // Auto-init: tat ca form co attribute data-unsaved-guard se duoc bao ve.
    // Skip auto-warn khi submit form do (vi do chinh la luc save).
    // -------------------------------------------------------------------------
    var UnsavedGuard = {
        init: function (formEl) {
            var form = (typeof formEl === 'string')
                ? document.querySelector(formEl)
                : formEl;
            if (!form) return;

            var dirty = false;
            var initialState = serialize(form);

            form.addEventListener('input', function () {
                dirty = serialize(form) !== initialState;
            });
            form.addEventListener('change', function () {
                dirty = serialize(form) !== initialState;
            });
            form.addEventListener('submit', function () {
                dirty = false; // dang luu, khong warn
            });

            window.addEventListener('beforeunload', function (e) {
                if (dirty) {
                    e.preventDefault();
                    e.returnValue = '';
                    return '';
                }
            });

            function serialize(f) {
                var data = new FormData(f);
                var out = [];
                data.forEach(function (v, k) {
                    if (typeof v === 'string') out.push(k + '=' + v);
                });
                return out.join('&');
            }
        },
        autoInit: function () {
            document.querySelectorAll('form[data-unsaved-guard]').forEach(function (f) {
                UnsavedGuard.init(f);
            });
        }
    };
    window.UnsavedGuard = UnsavedGuard;
    document.addEventListener('DOMContentLoaded', UnsavedGuard.autoInit);

    // -------------------------------------------------------------------------
    // Dropzone — Phase 5
    //
    // Bien <input type="file"> binh thuong thanh khu vuc drag-drop dep, voi
    // preview ten/kich thuoc file, nut clear, va thong bao kich thuoc khi vuot.
    //
    // Markup:
    //   <label class="dropzone" data-dropzone>
    //     <input type="file" name="file" class="dropzone-input"
    //            accept=".xlsx,.xls" data-max-size="20971520">
    //     <i class="bi bi-cloud-arrow-up dropzone-icon"></i>
    //     <p class="dropzone-title">Keo tha file vao day</p>
    //     <p class="dropzone-hint">hoac click de chon · .xlsx, .xls (toi da 20MB)</p>
    //   </label>
    //
    // Auto-init: tat ca element co attribute [data-dropzone].
    // -------------------------------------------------------------------------
    function formatBytes(bytes) {
        if (bytes < 1024) return bytes + ' B';
        if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
        return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    }

    var Dropzone = {
        init: function (zoneEl) {
            if (!zoneEl || zoneEl.__dzInited) return;
            zoneEl.__dzInited = true;

            var input = zoneEl.querySelector('.dropzone-input');
            if (!input) return;

            // Backup goc HTML cua zone (de restore sau khi clear)
            var initialHtml = zoneEl.innerHTML;

            function renderFile(file) {
                zoneEl.classList.add('has-file');
                zoneEl.innerHTML = '';

                var fileWrap = document.createElement('div');
                fileWrap.className = 'dropzone-file';

                var icon = document.createElement('i');
                icon.className = 'bi bi-file-earmark-spreadsheet dropzone-file-icon';
                fileWrap.appendChild(icon);

                var meta = document.createElement('div');
                meta.className = 'dropzone-file-meta';
                var name = document.createElement('span');
                name.className = 'dropzone-file-name';
                name.textContent = file.name;
                var size = document.createElement('span');
                size.className = 'dropzone-file-size';
                size.textContent = formatBytes(file.size);
                meta.appendChild(name);
                meta.appendChild(size);
                fileWrap.appendChild(meta);

                var clearBtn = document.createElement('button');
                clearBtn.type = 'button';
                clearBtn.className = 'dropzone-file-clear';
                clearBtn.innerHTML = '<i class="bi bi-x-lg me-1"></i>Bo chon';
                clearBtn.addEventListener('click', function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    reset();
                });
                fileWrap.appendChild(clearBtn);

                zoneEl.appendChild(fileWrap);
                // Re-attach hidden input (truoc bi xoa khi innerHTML clear)
                zoneEl.appendChild(input);
            }

            function reset() {
                input.value = '';
                zoneEl.classList.remove('has-file', 'is-dragover');
                zoneEl.innerHTML = initialHtml;
                // Re-bind on the new DOM
                input = zoneEl.querySelector('.dropzone-input');
                if (input) bindInput();
                zoneEl.dispatchEvent(new CustomEvent('dropzone:cleared'));
            }

            function validate(file) {
                var maxSize = parseInt(input.getAttribute('data-max-size') || '0', 10);
                if (maxSize > 0 && file.size > maxSize) {
                    if (window.Toast) {
                        window.Toast.error('File qua lon. Toi da ' + formatBytes(maxSize) + '.');
                    } else {
                        alert('File qua lon. Toi da ' + formatBytes(maxSize) + '.');
                    }
                    return false;
                }
                return true;
            }

            function setFile(file) {
                if (!file) return;
                if (!validate(file)) return;
                // Gan vao input qua DataTransfer
                try {
                    var dt = new DataTransfer();
                    dt.items.add(file);
                    input.files = dt.files;
                } catch (err) {
                    // Trinh duyet cu khong support DataTransfer — bo qua, du
                    // sao file da nam trong input qua change event.
                }
                renderFile(file);
                zoneEl.dispatchEvent(new CustomEvent('dropzone:filed', { detail: { file: file } }));
            }

            function bindInput() {
                input.addEventListener('change', function () {
                    if (input.files && input.files[0]) {
                        setFile(input.files[0]);
                    }
                });
            }

            bindInput();

            // Drag-drop handlers
            ['dragenter', 'dragover'].forEach(function (evt) {
                zoneEl.addEventListener(evt, function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    zoneEl.classList.add('is-dragover');
                });
            });
            ['dragleave', 'drop'].forEach(function (evt) {
                zoneEl.addEventListener(evt, function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    if (evt === 'dragleave' && zoneEl.contains(e.relatedTarget)) return;
                    zoneEl.classList.remove('is-dragover');
                });
            });
            zoneEl.addEventListener('drop', function (e) {
                var dt = e.dataTransfer;
                if (dt && dt.files && dt.files[0]) {
                    setFile(dt.files[0]);
                }
            });

            // Click anywhere on zone -> open file dialog (nhung khong khi click vao chinh input)
            zoneEl.addEventListener('click', function (e) {
                if (e.target === input || e.target.closest('.dropzone-file-clear')) return;
                input.click();
            });

            // Keyboard accessibility
            zoneEl.setAttribute('tabindex', '0');
            zoneEl.setAttribute('role', 'button');
            zoneEl.addEventListener('keydown', function (e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    input.click();
                }
            });
        },
        autoInit: function () {
            document.querySelectorAll('[data-dropzone]').forEach(function (el) {
                Dropzone.init(el);
            });
        }
    };
    window.Dropzone = Dropzone;
    document.addEventListener('DOMContentLoaded', Dropzone.autoInit);

})();
