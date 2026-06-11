// 检查登录状态
const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || 'null');
if (!userInfo || userInfo.role !== '管理员') {
    window.location.href = 'login.html';
}
document.getElementById('welcome-label').textContent = `你好，${userInfo.empName}`;

// 页面切换
function switchPage(page) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    document.getElementById('page-' + page).classList.add('active');
    document.getElementById('nav-' + page).classList.add('active');
    if (page === 'dept') loadDepts();
    if (page === 'emp') loadEmployees();
    if (page === 'salary') loadSalaries();
    if (page === 'att') loadAttendances();
}

// 退出登录
function handleLogout() {
    if (confirm('确定要退出登录吗？')) {
        sessionStorage.clear();
        window.location.href = 'login.html';
    }
}

// ══════════════════════════════════════════════
// 部门管理
// ══════════════════════════════════════════════
async function loadDepts() {
    const res  = await fetch('/api/departments');
    const data = await res.json();
    const tbody = document.getElementById('dept-tbody');
    tbody.innerHTML = '';
    data.data.forEach(d => {
        tbody.innerHTML += `
            <tr>
                <td>${d.deptName}</td>
                <td>${d.deptAddress || ''}</td>
                <td>${d.deptPhone || ''}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1"
                        onclick="openDeptModal(${d.deptId},'${d.deptName}','${d.deptAddress||''}','${d.deptPhone||''}')">编辑</button>
                    <button class="btn btn-sm btn-outline-danger"
                        onclick="deleteDept(${d.deptId},'${d.deptName}')">删除</button>
                </td>
            </tr>`;
    });
}

function openDeptModal(id, name, address, phone) {
    document.getElementById('dept-id').value      = id || '';
    document.getElementById('dept-name').value    = name || '';
    document.getElementById('dept-address').value = address || '';
    document.getElementById('dept-phone').value   = phone || '';
    document.getElementById('dept-modal-title').textContent = id ? '编辑部门' : '新增部门';
    new bootstrap.Modal(document.getElementById('deptModal')).show();
}

async function saveDept() {
    const id      = document.getElementById('dept-id').value;
    const name    = document.getElementById('dept-name').value.trim();
    const address = document.getElementById('dept-address').value.trim();
    const phone   = document.getElementById('dept-phone').value.trim();
    if (!name) { alert('部门名称不能为空'); return; }

    const url    = id ? `/api/departments/${id}` : '/api/departments';
    const method = id ? 'PUT' : 'POST';
    const res    = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ deptName: name, deptAddress: address, deptPhone: phone })
    });
    const data = await res.json();
    if (data.code === 200) {
        bootstrap.Modal.getInstance(document.getElementById('deptModal')).hide();
        loadDepts();
    } else {
        alert(data.message);
    }
}

async function deleteDept(id, name) {
    if (!confirm(`确定要删除部门「${name}」吗？`)) return;
    const res  = await fetch(`/api/departments/${id}`, { method: 'DELETE' });
    const data = await res.json();
    if (data.code === 200) loadDepts();
    else alert(data.message);
}

// ══════════════════════════════════════════════
// 职员管理
// ══════════════════════════════════════════════
async function loadEmployees() {
    const kw   = document.getElementById('emp-search').value.trim();
    const url  = kw ? `/api/employees?keyword=${kw}` : '/api/employees';
    const res  = await fetch(url);
    const data = await res.json();
    const tbody = document.getElementById('emp-tbody');
    tbody.innerHTML = '';
    data.data.forEach(e => {
        tbody.innerHTML += `
            <tr>
                <td>${e.empId}</td>
                <td>${e.empName}</td>
                <td>${e.gender || ''}</td>
                <td>${e.deptName || '未分配'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1"
                        onclick="openEmpModal(${JSON.stringify(e).replace(/"/g,'&quot;')})">编辑</button>
                    <button class="btn btn-sm btn-outline-danger"
                        onclick="deleteEmp(${e.empId},'${e.empName}')">删除</button>
                </td>
            </tr>`;
    });
}

function resetEmpSearch() {
    document.getElementById('emp-search').value = '';
    loadEmployees();
}

async function openEmpModal(emp) {
    // 加载部门下拉框
    const dres  = await fetch('/api/departments');
    const ddata = await dres.json();
    const sel   = document.getElementById('emp-dept');
    sel.innerHTML = '';
    ddata.data.forEach(d => {
        sel.innerHTML += `<option value="${d.deptId}">${d.deptName}</option>`;
    });

    if (emp && typeof emp === 'object') {
        document.getElementById('emp-modal-title').textContent = '编辑职员';
        document.getElementById('emp-edit-id').value  = emp.empId;
        document.getElementById('emp-id').value       = emp.empId;
        document.getElementById('emp-id').disabled    = true;
        document.getElementById('emp-name').value     = emp.empName;
        document.getElementById('emp-gender').value   = emp.gender || '男';
        document.getElementById('emp-position').value = emp.position || '';
        document.getElementById('emp-hire').value     = emp.hireDate || '';
        document.getElementById('emp-phone').value    = emp.phone || '';
        sel.value = emp.deptId;
    } else {
        document.getElementById('emp-modal-title').textContent = '新增职员';
        document.getElementById('emp-edit-id').value = '';
        document.getElementById('emp-id').value      = '';
        document.getElementById('emp-id').disabled   = false;
        document.getElementById('emp-name').value    = '';
        document.getElementById('emp-position').value = '';
        document.getElementById('emp-hire').value    = '';
        document.getElementById('emp-phone').value   = '';
    }
    new bootstrap.Modal(document.getElementById('empModal')).show();
}

async function saveEmp() {
    const editId = document.getElementById('emp-edit-id').value;
    const body   = {
        empId:    parseInt(document.getElementById('emp-id').value),
        empName:  document.getElementById('emp-name').value.trim(),
        gender:   document.getElementById('emp-gender').value,
        deptId:   parseInt(document.getElementById('emp-dept').value),
        position: document.getElementById('emp-position').value.trim(),
        hireDate: document.getElementById('emp-hire').value || null,
        phone:    document.getElementById('emp-phone').value.trim(),
    };
    if (!body.empName) { alert('姓名不能为空'); return; }

    const url    = editId ? `/api/employees/${editId}` : '/api/employees';
    const method = editId ? 'PUT' : 'POST';
    const res    = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    const data = await res.json();
    if (data.code === 200) {
        bootstrap.Modal.getInstance(document.getElementById('empModal')).hide();
        loadEmployees();
    } else {
        alert(data.message);
    }
}

async function deleteEmp(id, name) {
    if (!confirm(`确定要删除职员「${name}」吗？`)) return;
    const res  = await fetch(`/api/employees/${id}`, { method: 'DELETE' });
    const data = await res.json();
    if (data.code === 200) loadEmployees();
    else alert(data.message);
}

// ══════════════════════════════════════════════
// 工资管理
// ══════════════════════════════════════════════
async function loadSalaries() {
    const kw    = document.getElementById('salary-search').value.trim();
    const month = document.getElementById('salary-month').value.trim();
    let url     = '/api/salaries?';
    if (kw)    url += `keyword=${kw}&`;
    if (month) url += `month=${month}`;
    const res  = await fetch(url);
    const data = await res.json();
    const tbody = document.getElementById('salary-tbody');
    tbody.innerHTML = '';
    data.data.forEach(s => {
        tbody.innerHTML += `
            <tr>
                <td>${s.empId}</td>
                <td>${s.empName}</td>
                <td>${s.deptName || ''}</td>
                <td>${s.salaryMonth}</td>
                <td>${s.netSalary?.toFixed(2) || '0.00'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1"
                        onclick="openSalaryModal(${JSON.stringify(s).replace(/"/g,'&quot;')})">编辑</button>
                    <button class="btn btn-sm btn-outline-danger"
                        onclick="deleteSalary(${s.salaryId})">删除</button>
                </td>
            </tr>`;
    });
}

async function openSalaryModal(sal) {
    // 加载职员下拉框
    const eres  = await fetch('/api/employees');
    const edata = await eres.json();
    const sel   = document.getElementById('salary-emp');
    sel.innerHTML = '';
    edata.data.forEach(e => {
        sel.innerHTML += `<option value="${e.empId}">${e.empId} - ${e.empName}</option>`;
    });

    if (sal && typeof sal === 'object') {
        document.getElementById('salary-modal-title').textContent = '编辑工资';
        document.getElementById('salary-id').value          = sal.salaryId;
        sel.value = sal.empId;
        sel.disabled = true;
        document.getElementById('salary-month-input').value = sal.salaryMonth;
        document.getElementById('sal-base').value  = sal.baseSalary || 0;
        document.getElementById('sal-allow').value = sal.allowance || 0;
        document.getElementById('sal-tax').value   = sal.tax || 0;
        document.getElementById('sal-ins').value   = sal.insurance || 0;
        document.getElementById('sal-fund').value  = sal.housingFund || 0;
        document.getElementById('sal-water').value = sal.waterFee || 0;
        document.getElementById('sal-gas').value   = sal.gasFee || 0;
        document.getElementById('sal-rent').value  = sal.rentFee || 0;
        document.getElementById('sal-union').value = sal.unionFee || 0;
    } else {
        document.getElementById('salary-modal-title').textContent = '工资录入';
        document.getElementById('salary-id').value = '';
        sel.disabled = false;
        ['salary-month-input','sal-base','sal-allow','sal-tax','sal-ins',
         'sal-fund','sal-water','sal-gas','sal-rent','sal-union']
            .forEach(id => document.getElementById(id).value = id === 'salary-month-input' ? '' : 0);
    }
    calcNet();
    new bootstrap.Modal(document.getElementById('salaryModal')).show();
}

function calcNet() {
    const f = id => parseFloat(document.getElementById(id).value) || 0;
    const net = f('sal-base') + f('sal-allow') - f('sal-tax') - f('sal-ins')
              - f('sal-fund') - f('sal-water') - f('sal-gas') - f('sal-rent') - f('sal-union');
    document.getElementById('net-salary').textContent = net.toFixed(2);
}

async function saveSalary() {
    const id    = document.getElementById('salary-id').value;
    const empId = parseInt(document.getElementById('salary-emp').value);
    const body  = {
        empId,
        salaryMonth: parseInt(document.getElementById('salary-month-input').value),
        baseSalary:  parseFloat(document.getElementById('sal-base').value) || 0,
        allowance:   parseFloat(document.getElementById('sal-allow').value) || 0,
        tax:         parseFloat(document.getElementById('sal-tax').value) || 0,
        insurance:   parseFloat(document.getElementById('sal-ins').value) || 0,
        housingFund: parseFloat(document.getElementById('sal-fund').value) || 0,
        waterFee:    parseFloat(document.getElementById('sal-water').value) || 0,
        gasFee:      parseFloat(document.getElementById('sal-gas').value) || 0,
        rentFee:     parseFloat(document.getElementById('sal-rent').value) || 0,
        unionFee:    parseFloat(document.getElementById('sal-union').value) || 0,
    };
    if (!body.salaryMonth) { alert('月份不能为空'); return; }

    const url    = id ? `/api/salaries/${id}` : '/api/salaries';
    const method = id ? 'PUT' : 'POST';
    const res    = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    const data = await res.json();
    if (data.code === 200) {
        bootstrap.Modal.getInstance(document.getElementById('salaryModal')).hide();
        loadSalaries();
    } else {
        alert(data.message);
    }
}

async function deleteSalary(id) {
    if (!confirm('确定要删除这条工资记录吗？')) return;
    const res  = await fetch(`/api/salaries/${id}`, { method: 'DELETE' });
    const data = await res.json();
    if (data.code === 200) loadSalaries();
    else alert(data.message);
}

// ══════════════════════════════════════════════
// 考勤管理
// ══════════════════════════════════════════════
async function loadAttendances() {
    const kw     = document.getElementById('att-search').value.trim();
    const status = document.getElementById('att-status').value;
    let url      = '/api/attendances?';
    if (kw)     url += `keyword=${kw}&`;
    if (status) url += `status=${status}`;
    const res  = await fetch(url);
    const data = await res.json();
    const tbody = document.getElementById('att-tbody');
    tbody.innerHTML = '';
    data.data.forEach(a => {
        tbody.innerHTML += `
            <tr>
                <td>${a.empName}</td>
                <td>${a.deptName || ''}</td>
                <td>${a.workDate}</td>
                <td>${a.type}</td>
                <td>${a.reason || ''}</td>
                <td><span class="badge ${statusBadge(a.status)}">${a.status}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-warning me-1"
                        onclick="openReviewModal(${a.attendanceId},'${a.status}')">审核</button>
                    <button class="btn btn-sm btn-outline-danger"
                        onclick="deleteAtt(${a.attendanceId})">删除</button>
                </td>
            </tr>`;
    });
}

function statusBadge(status) {
    if (status === '已通过') return 'bg-success';
    if (status === '已驳回') return 'bg-danger';
    return 'bg-warning text-dark';
}

async function openAttModal() {
    const eres  = await fetch('/api/employees');
    const edata = await eres.json();
    const sel   = document.getElementById('att-emp');
    sel.innerHTML = '';
    edata.data.forEach(e => {
        sel.innerHTML += `<option value="${e.empId}">${e.empId} - ${e.empName}</option>`;
    });
    document.getElementById('att-date').value   = '';
    document.getElementById('att-reason').value = '';
    new bootstrap.Modal(document.getElementById('attModal')).show();
}

async function saveAtt() {
    const body = {
        empId:        parseInt(document.getElementById('att-emp').value),
        workDate:     document.getElementById('att-date').value,
        type:         document.getElementById('att-type').value,
        checkInTime:  document.getElementById('att-in').value || null,
        checkOutTime: document.getElementById('att-out').value || null,
        reason:       document.getElementById('att-reason').value.trim() || null,
    };
    if (!body.workDate) { alert('日期不能为空'); return; }

    const res  = await fetch('/api/attendances', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    const data = await res.json();
    if (data.code === 200) {
        bootstrap.Modal.getInstance(document.getElementById('attModal')).hide();
        loadAttendances();
    } else {
        alert(data.message);
    }
}

function openReviewModal(id, currentStatus) {
    document.getElementById('review-att-id').value    = id;
    document.getElementById('review-status').value    = currentStatus;
    new bootstrap.Modal(document.getElementById('reviewModal')).show();
}

async function submitReview() {
    const id     = document.getElementById('review-att-id').value;
    const status = document.getElementById('review-status').value;
    const res    = await fetch(`/api/attendances/${id}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
    });
    const data = await res.json();
    if (data.code === 200) {
        bootstrap.Modal.getInstance(document.getElementById('reviewModal')).hide();
        loadAttendances();
    } else {
        alert(data.message);
    }
}

async function deleteAtt(id) {
    if (!confirm('确定要删除这条考勤记录吗？')) return;
    const res  = await fetch(`/api/attendances/${id}`, { method: 'DELETE' });
    const data = await res.json();
    if (data.code === 200) loadAttendances();
    else alert(data.message);
}

// 初始加载
loadDepts();