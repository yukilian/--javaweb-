/* ══════════════════════════════════════════════
   manager.js  —  部门负责人个人中心 + 部门管理
   ══════════════════════════════════════════════ */

// ── 鉴权 ─────────────────────────────────────────
const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || 'null');
if (!userInfo) { window.location.href = 'login.html'; }
if (userInfo.role === '管理员') { window.location.href = 'admin.html'; }
if (!userInfo.isDeptManager)   { window.location.href = 'employee.html'; }

const EMP_ID  = userInfo.empId;
const USER_ID = userInfo.userId;
const DEPT_ID = userInfo.deptId;

document.getElementById('welcome-label').textContent =
    `你好，${userInfo.empName}\n（部门负责人）`;

// ── 页面切换 ──────────────────────────────────────
function switchPage(page) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    document.getElementById('page-' + page).classList.add('active');
    document.getElementById('nav-'  + page).classList.add('active');

    if (page === 'info')       loadInfo();
    if (page === 'salary')     loadSalary();
    if (page === 'attendance') loadAttendance();
    if (page === 'security')   loadSecurity();
    if (page === 'dept')       loadDeptPage();
}

// ── 退出登录 ──────────────────────────────────────
function handleLogout() {
    if (confirm('确定要退出登录吗？')) {
        sessionStorage.clear();
        window.location.href = 'login.html';
    }
}

/* ══════════════════════════════════════════════
   个人信息管理  （与 employee.js 完全相同的逻辑）
   ══════════════════════════════════════════════ */
async function loadInfo() {
    try {
        const res  = await fetch(`/api/profile/${EMP_ID}`);
        const data = await res.json();
        if (data.code !== 200) { showToast(data.message, 'danger'); return; }
        const e = data.data;
        document.getElementById('val-empid').textContent     = e.empId     || '-';
        document.getElementById('val-name').textContent      = e.empName   || '-';
        document.getElementById('val-gender').textContent    = e.gender    || '-';
        document.getElementById('val-birth').textContent     = e.birthDate || '-';
        document.getElementById('val-dept-name').textContent = e.deptName  || '（未分配）';
        document.getElementById('val-position').textContent  = e.position  || '-';
        document.getElementById('val-hire').textContent      = e.hireDate  || '-';
        document.getElementById('val-phone').textContent     = e.phone     || '-';
        document.getElementById('val-addr').textContent      = e.address   || '-';
    } catch (err) {
        showToast('加载个人信息失败', 'danger');
    }
}

function openEditModal(fieldKey, label, type) {
    document.getElementById('edit-modal-title').textContent = `修改${label}`;
    document.getElementById('edit-field-key').value = fieldKey;

    document.getElementById('edit-text-wrap').style.display   = 'none';
    document.getElementById('edit-date-wrap').style.display   = 'none';
    document.getElementById('edit-gender-wrap').style.display = 'none';

    if (type === 'gender') {
        document.getElementById('edit-gender-wrap').style.display = '';
        const cur = document.getElementById('val-gender').textContent;
        document.getElementById('edit-gender-val').value = (cur === '男' || cur === '女') ? cur : '男';
    } else if (type === 'date') {
        document.getElementById('edit-date-wrap').style.display = '';
        const cur = document.getElementById('val-birth').textContent;
        document.getElementById('edit-date-val').value = cur === '-' ? '' : cur;
    } else {
        document.getElementById('edit-text-wrap').style.display = '';
        const valMap = { empName: 'val-name', phone: 'val-phone', address: 'val-addr' };
        const cur = document.getElementById(valMap[fieldKey])?.textContent || '';
        document.getElementById('edit-text-val').value = cur === '-' ? '' : cur;
    }
    new bootstrap.Modal(document.getElementById('editModal')).show();
}

async function saveField() {
    const field = document.getElementById('edit-field-key').value;
    let value;
    if (field === 'gender') {
        value = document.getElementById('edit-gender-val').value;
    } else if (field === 'birthDate') {
        value = document.getElementById('edit-date-val').value;
        if (!value) { alert('出生日期不能为空'); return; }
    } else {
        value = document.getElementById('edit-text-val').value.trim();
        if (!value) { alert('内容不能为空'); return; }
    }
    try {
        const res  = await fetch(`/api/profile/${EMP_ID}/field`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ field, value })
        });
        const data = await res.json();
        if (data.code === 200) {
            bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
            loadInfo();
            showToast('修改成功', 'success');
        } else { alert(data.message); }
    } catch (err) { alert('请求失败，请稍后重试'); }
}

/* ══════════════════════════════════════════════
   个人工资管理
   ══════════════════════════════════════════════ */
let _salaryRows = [];

async function loadSalary() {
    const month = document.getElementById('salary-month-filter').value.trim();
    let url = `/api/salaries/emp/${EMP_ID}`;
    if (month) url += `?month=${month}`;
    try {
        const res  = await fetch(url);
        const data = await res.json();
        if (data.code !== 200) { showToast(data.message, 'danger'); return; }
        _salaryRows = data.data;
        renderSalaryTable(_salaryRows);
    } catch (err) { showToast('加载工资数据失败', 'danger'); }
}

function renderSalaryTable(rows) {
    const tbody = document.getElementById('salary-tbody');
    tbody.innerHTML = '';
    if (!rows || rows.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center text-secondary py-4">暂无工资记录</td></tr>';
        return;
    }
    rows.forEach(s => {
        const other = ((s.waterFee||0)+(s.gasFee||0)+(s.rentFee||0)+(s.unionFee||0)).toFixed(2);
        tbody.innerHTML += `
            <tr>
                <td>${s.salaryMonth}</td>
                <td>${fmt(s.baseSalary)}</td><td>${fmt(s.allowance)}</td>
                <td>${fmt(s.tax)}</td><td>${fmt(s.insurance)}</td>
                <td>${fmt(s.housingFund)}</td><td>${other}</td>
                <td><strong>${fmt(s.netSalary)}</strong></td>
                <td>
                    <button class="btn btn-sm btn-outline-secondary py-0"
                        onclick='openSalaryDetail(${JSON.stringify(s)})'>详情</button>
                </td>
            </tr>`;
    });
}

function openSalaryDetail(s) {
    document.getElementById('salary-detail-body').innerHTML = `
        <table class="table table-sm mb-0">
            <tbody>
                <tr><td class="text-secondary">月份</td><td class="text-end fw-bold">${s.salaryMonth}</td></tr>
                <tr><td colspan="2"><hr class="my-1"></td></tr>
                <tr><td class="text-secondary">基本工资</td><td class="text-end">${fmt(s.baseSalary)}</td></tr>
                <tr><td class="text-secondary">津贴</td><td class="text-end">${fmt(s.allowance)}</td></tr>
                <tr><td colspan="2"><hr class="my-1"></td></tr>
                <tr><td class="text-secondary text-danger">- 税费</td><td class="text-end text-danger">${fmt(s.tax)}</td></tr>
                <tr><td class="text-secondary text-danger">- 保险</td><td class="text-end text-danger">${fmt(s.insurance)}</td></tr>
                <tr><td class="text-secondary text-danger">- 公积金</td><td class="text-end text-danger">${fmt(s.housingFund)}</td></tr>
                <tr><td class="text-secondary text-danger">- 水费</td><td class="text-end text-danger">${fmt(s.waterFee)}</td></tr>
                <tr><td class="text-secondary text-danger">- 煤气费</td><td class="text-end text-danger">${fmt(s.gasFee)}</td></tr>
                <tr><td class="text-secondary text-danger">- 房租</td><td class="text-end text-danger">${fmt(s.rentFee)}</td></tr>
                <tr><td class="text-secondary text-danger">- 会费</td><td class="text-end text-danger">${fmt(s.unionFee)}</td></tr>
                <tr><td colspan="2"><hr class="my-1"></td></tr>
                <tr>
                    <td class="fw-bold">实发工资</td>
                    <td class="text-end fw-bold text-primary fs-6">${fmt(s.netSalary)} 元</td>
                </tr>
            </tbody>
        </table>`;
    new bootstrap.Modal(document.getElementById('salaryDetailModal')).show();
}

function exportPersonalSalary() {
    if (!_salaryRows || _salaryRows.length === 0) { alert('没有可导出的工资数据'); return; }
    const headers = ['月份','基本工资','津贴','税费','保险','公积金','水费','煤气费','房租','会费','实发工资'];
    const rows = _salaryRows.map(s => [
        s.salaryMonth, fmt(s.baseSalary), fmt(s.allowance), fmt(s.tax),
        fmt(s.insurance), fmt(s.housingFund), fmt(s.waterFee), fmt(s.gasFee),
        fmt(s.rentFee), fmt(s.unionFee), fmt(s.netSalary)
    ]);
    downloadCsv(rows, headers, `${userInfo.empName}_个人工资表`);
}

/* ══════════════════════════════════════════════
   个人考勤历史
   ══════════════════════════════════════════════ */
async function loadAttendance() {
    try {
        const res  = await fetch(`/api/attendances/emp/${EMP_ID}`);
        const data = await res.json();
        if (data.code !== 200) { showToast(data.message, 'danger'); return; }
        const tbody = document.getElementById('att-tbody');
        tbody.innerHTML = '';
        if (!data.data || data.data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-secondary py-4">暂无考勤记录</td></tr>';
            return;
        }
        data.data.forEach(a => {
            tbody.innerHTML += `
                <tr>
                    <td>${a.workDate}</td><td>${a.type}</td>
                    <td>${a.checkInTime  || '-'}</td>
                    <td>${a.checkOutTime || '-'}</td>
                    <td>${statusBadge(a.status)}</td>
                </tr>`;
        });
    } catch (err) { showToast('加载考勤数据失败', 'danger'); }
}

function statusBadge(status) {
    if (status === '已通过') return `<span class="badge badge-approved rounded-pill px-3">${status}</span>`;
    if (status === '已驳回') return `<span class="badge badge-rejected rounded-pill px-3">${status}</span>`;
    return `<span class="badge badge-pending rounded-pill px-3">${status || '待审核'}</span>`;
}

/* ══════════════════════════════════════════════
   账号安全管理
   ══════════════════════════════════════════════ */
async function loadSecurity() {
    try {
        const res  = await fetch(`/api/profile/${USER_ID}/security`);
        const data = await res.json();
        if (data.code === 200 && data.data && data.data !== '未设置') {
            document.getElementById('val-sec-question').textContent = data.data;
            document.getElementById('val-sec-answer').textContent   = '******';
        } else {
            document.getElementById('val-sec-question').textContent = '（未设置）';
            document.getElementById('val-sec-answer').textContent   = '（未设置）';
        }
    } catch (err) { /* 静默 */ }
}

async function changePassword() {
    const oldPwd  = document.getElementById('old-pwd').value.trim();
    const newPwd  = document.getElementById('new-pwd').value.trim();
    const confirm = document.getElementById('confirm-pwd').value.trim();
    const msgEl   = document.getElementById('pwd-msg');
    const show = (msg, type) => {
        msgEl.textContent = msg; msgEl.className = `small text-${type}`;
        msgEl.style.display = 'block';
    };
    if (!oldPwd || !newPwd || !confirm) { show('请填写所有密码字段', 'danger'); return; }
    if (newPwd !== confirm)              { show('两次输入的新密码不一致', 'danger'); return; }
    if (newPwd.length < 4)              { show('新密码长度不能少于4位', 'danger'); return; }
    try {
        const res  = await fetch(`/api/profile/${USER_ID}/password`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ oldPassword: oldPwd, newPassword: newPwd, confirmPassword: confirm })
        });
        const data = await res.json();
        if (data.code === 200) {
            show('密码修改成功！', 'success');
            ['old-pwd','new-pwd','confirm-pwd'].forEach(id => document.getElementById(id).value = '');
        } else { show(data.message, 'danger'); }
    } catch (err) { show('请求失败，请稍后重试', 'danger'); }
}

function openSecurityModal() {
    ['sec-question','sec-old-answer','sec-new-answer'].forEach(id =>
        document.getElementById(id).value = '');
    document.getElementById('sec-msg').style.display = 'none';
    const cur = document.getElementById('val-sec-question').textContent;
    if (cur !== '（未设置）') document.getElementById('sec-question').value = cur;
    new bootstrap.Modal(document.getElementById('securityModal')).show();
}

async function saveSecurity() {
    const question  = document.getElementById('sec-question').value.trim();
    const oldAnswer = document.getElementById('sec-old-answer').value.trim();
    const newAnswer = document.getElementById('sec-new-answer').value.trim();
    const msgEl     = document.getElementById('sec-msg');
    const showMsg = (msg, type) => {
        msgEl.textContent = msg; msgEl.className = `small text-${type}`;
        msgEl.style.display = 'block';
    };
    if (!question || !newAnswer) { showMsg('密保问题和新答案不能为空', 'danger'); return; }
    try {
        const res  = await fetch(`/api/profile/${USER_ID}/security`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ securityQuestion: question, oldAnswer, newAnswer })
        });
        const data = await res.json();
        if (data.code === 200) {
            bootstrap.Modal.getInstance(document.getElementById('securityModal')).hide();
            loadSecurity();
            showToast('密保已更新', 'success');
        } else { showMsg(data.message, 'danger'); }
    } catch (err) { showMsg('请求失败，请稍后重试', 'danger'); }
}

/* ══════════════════════════════════════════════
   部门管理（负责人专属）
   ══════════════════════════════════════════════ */

// 本地缓存部门职员列表（供搜索 + 导出复用）
let _deptEmpRows = [];
let _deptName    = '';

async function loadDeptPage() {
    await loadDeptName();
    await loadDeptEmp();
}

// 查询部门名称
async function loadDeptName() {
    try {
        const res  = await fetch(`/api/departments/${DEPT_ID}`);
        const data = await res.json();
        if (data.code === 200) {
            _deptName = data.data.deptName || '';
            document.getElementById('dept-title-name').textContent = _deptName;
        }
    } catch (err) { /* 静默 */ }
}

// 查询本部门全量职员工资数据（含最新月份）
async function loadDeptEmp(keyword) {
    const kw  = keyword !== undefined ? keyword
              : document.getElementById('dept-emp-search').value.trim();
    let url   = `/api/employees/dept/${DEPT_ID}`;
    if (kw)   url += `?keyword=${encodeURIComponent(kw)}`;

    try {
        const [empRes, salRes] = await Promise.all([
            fetch(url),
            fetch(`/api/salaries?deptId=${DEPT_ID}`)   // 若后端无此参数，下方做前端聚合
        ]);
        const empData = await empRes.json();
        if (empData.code !== 200) { showToast(empData.message, 'danger'); return; }

        // 逐员工查最新工资（并行请求）
        const emps = empData.data;
        const salPromises = emps.map(e =>
            fetch(`/api/salaries/emp/${e.empId}`)
                .then(r => r.json())
                .then(d => ({ empId: e.empId, salaries: d.code === 200 ? d.data : [] }))
                .catch(() => ({ empId: e.empId, salaries: [] }))
        );
        const salResults = await Promise.all(salPromises);

        // 构建 empId -> 最新工资 的 Map
        const latestSalMap = {};
        salResults.forEach(({ empId, salaries }) => {
            if (salaries && salaries.length > 0) {
                // 已按月份降序，第一条即最新
                latestSalMap[empId] = salaries[0];
            }
        });

        // 合并
        _deptEmpRows = emps.map(e => ({
            ...e,
            latestSalary: latestSalMap[e.empId] || null
        }));

        renderDeptEmpTable(_deptEmpRows);
        updateSummaryCards(_deptEmpRows);
    } catch (err) {
        showToast('加载部门数据失败', 'danger');
    }
}

function renderDeptEmpTable(rows) {
    const tbody = document.getElementById('dept-emp-tbody');
    tbody.innerHTML = '';
    if (!rows || rows.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-secondary py-4">暂无成员</td></tr>';
        return;
    }
    rows.forEach(e => {
        const sal = e.latestSalary;
        const net = sal ? fmt(sal.netSalary) : '-';
        tbody.innerHTML += `
            <tr>
                <td>${e.empId}</td>
                <td>${e.empName}</td>
                <td>${e.gender || '-'}</td>
                <td>${e.position || '-'}</td>
                <td>${net !== '-' ? `<strong>${net}</strong>` : '<span class="text-secondary">-</span>'}</td>
                <td>
                    ${sal
                        ? `<button class="btn btn-sm btn-outline-secondary py-0"
                               onclick='openSalaryDetail(${JSON.stringify(sal)})'>工资详情</button>`
                        : '<span class="text-secondary small">暂无记录</span>'}
                </td>
            </tr>`;
    });
}

// 更新汇总卡片
function updateSummaryCards(rows) {
    const count = rows.length;
    document.getElementById('summary-count').textContent = count;

    const withSal = rows.filter(e => e.latestSalary);
    if (withSal.length === 0) {
        document.getElementById('summary-avg').textContent   = '-';
        document.getElementById('summary-total').textContent = '-';
        document.getElementById('summary-month').textContent = '-';
        return;
    }
    const total  = withSal.reduce((acc, e) => acc + Number(e.latestSalary.netSalary || 0), 0);
    const avg    = total / withSal.length;
    const month  = withSal[0].latestSalary.salaryMonth;   // 均取第一条

    document.getElementById('summary-avg').textContent   = avg.toFixed(2);
    document.getElementById('summary-total').textContent = total.toFixed(2);
    document.getElementById('summary-month').textContent = month;
}

function searchDeptEmp() {
    const kw = document.getElementById('dept-emp-search').value.trim();
    loadDeptEmp(kw);
}

function resetDeptSearch() {
    document.getElementById('dept-emp-search').value = '';
    loadDeptEmp('');
}

/* ── 导出工资发放表（每人一行，含所有工资字段） ─── */
async function exportPayroll() {
    try {
        // 拉取本部门全量工资明细
        const promises = _deptEmpRows.map(e =>
            fetch(`/api/salaries/emp/${e.empId}`)
                .then(r => r.json())
                .then(d => ({ emp: e, salaries: d.code === 200 ? d.data : [] }))
                .catch(() => ({ emp: e, salaries: [] }))
        );
        const results = await Promise.all(promises);

        const headers = ['职员编号','姓名','性别','职位','月份',
                         '基本工资','津贴','税费','保险','公积金',
                         '水费','煤气费','房租','会费','实发工资'];
        const rows = [];
        results.forEach(({ emp, salaries }) => {
            salaries.forEach(s => {
                rows.push([
                    emp.empId, emp.empName, emp.gender || '', emp.position || '',
                    s.salaryMonth, fmt(s.baseSalary), fmt(s.allowance), fmt(s.tax),
                    fmt(s.insurance), fmt(s.housingFund), fmt(s.waterFee),
                    fmt(s.gasFee), fmt(s.rentFee), fmt(s.unionFee), fmt(s.netSalary)
                ]);
            });
        });

        if (rows.length === 0) { alert('本部门暂无工资数据'); return; }
        downloadCsv(rows, headers, `${_deptName}_工资发放表`);
    } catch (err) {
        showToast('导出失败，请稍后重试', 'danger');
    }
}

/* ── 导出工资汇总表（按月份聚合） ─── */
async function exportSummary() {
    try {
        const promises = _deptEmpRows.map(e =>
            fetch(`/api/salaries/emp/${e.empId}`)
                .then(r => r.json())
                .then(d => d.code === 200 ? d.data : [])
                .catch(() => [])
        );
        const allSalArrays = await Promise.all(promises);
        const allSal = allSalArrays.flat();

        if (allSal.length === 0) { alert('本部门暂无工资数据'); return; }

        // 按月份聚合
        const monthMap = {};
        allSal.forEach(s => {
            const m = s.salaryMonth;
            if (!monthMap[m]) monthMap[m] = { count: 0, totalBase: 0, totalAllow: 0, totalDeduct: 0, totalNet: 0 };
            const g = monthMap[m];
            g.count++;
            g.totalBase  += Number(s.baseSalary  || 0);
            g.totalAllow += Number(s.allowance   || 0);
            g.totalDeduct += Number(s.tax||0) + Number(s.insurance||0) + Number(s.housingFund||0)
                           + Number(s.waterFee||0) + Number(s.gasFee||0)
                           + Number(s.rentFee||0) + Number(s.unionFee||0);
            g.totalNet   += Number(s.netSalary   || 0);
        });

        const headers = ['月份','发放人数','基本工资合计','津贴合计','扣款合计','实发工资合计'];
        const rows = Object.entries(monthMap)
            .sort((a, b) => b[0] - a[0])   // 降序
            .map(([month, g]) => [
                month, g.count,
                g.totalBase.toFixed(2),
                g.totalAllow.toFixed(2),
                g.totalDeduct.toFixed(2),
                g.totalNet.toFixed(2)
            ]);
        downloadCsv(rows, headers, `${_deptName}_工资汇总表`);
    } catch (err) {
        showToast('导出失败，请稍后重试', 'danger');
    }
}

/* ══════════════════════════════════════════════
   工具函数
   ══════════════════════════════════════════════ */
function fmt(val) {
    return val != null ? Number(val).toFixed(2) : '0.00';
}

function downloadCsv(rows, headers, filename) {
    let csv = '\uFEFF' + headers.join(',') + '\n';
    rows.forEach(r => { csv += r.map(v => `"${v}"`).join(',') + '\n'; });
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url  = URL.createObjectURL(blob);
    const a    = document.createElement('a');
    a.href = url; a.download = `${filename}.csv`; a.click();
    URL.revokeObjectURL(url);
}

function showToast(msg, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'position-fixed bottom-0 end-0 p-3';
        container.style.zIndex = 9999;
        document.body.appendChild(container);
    }
    const id   = 'toast-' + Date.now();
    const icon = type === 'success' ? '✓' : type === 'danger' ? '✕' : 'ℹ';
    container.insertAdjacentHTML('beforeend', `
        <div id="${id}" class="toast align-items-center text-bg-${type === 'danger' ? 'danger' : type === 'success' ? 'success' : 'secondary'} border-0"
             role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">${icon} ${msg}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>`);
    const toastEl = document.getElementById(id);
    new bootstrap.Toast(toastEl, { delay: 2500 }).show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
}

// ── 初始加载 ──────────────────────────────────────
loadInfo();