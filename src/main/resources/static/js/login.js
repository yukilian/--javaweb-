// 回车键触发登录
document.addEventListener('keydown', function(e) {
    if (e.key === 'Enter') handleLogin();
});

async function handleLogin() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMsg = document.getElementById('error-msg');

    if (!username) {
        showError('请输入账号！'); return;
    }
    if (!password) {
        showError('请输入密码！'); return;
    }

    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();

        if (data.code === 200) {
            // 把用户信息存到 sessionStorage，后续页面使用
            sessionStorage.setItem('userInfo', JSON.stringify(data.data));

            // 根据角色跳转不同页面
            const user = data.data;
            if (user.role === '管理员') {
                window.location.href = 'admin.html';
            } else if (user.isDeptManager) {
                window.location.href = 'manager.html';
            } else {
                window.location.href = 'employee.html';
            }
        } else {
            showError(data.message);
        }
    } catch (e) {
        showError('网络错误，请稍后重试');
    }
}

function showError(msg) {
    const el = document.getElementById('error-msg');
    el.textContent = msg;
    el.style.display = 'block';
}