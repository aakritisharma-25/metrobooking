function showTab(tab, el) {
    document.getElementById('login-form').classList.add('hidden');
    document.getElementById('register-form').classList.add('hidden');
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.getElementById(`${tab}-form`).classList.remove('hidden');
    el.classList.add('active');

    // Update subtitle
    document.querySelector('.auth-box h2').textContent =
        tab === 'login' ? 'Welcome back' : 'Create account';
    document.querySelector('.subtitle').textContent =
        tab === 'login' ? 'Sign in to your account to continue' : 'Join MetroBook today';
}

async function login() {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    try {
        const data = await apiCall('/auth/login', 'POST', { email, password });
        if (data.token) {
            setToken(data.token);
            setUser({ email: data.email, name: data.name, role: data.role });
            window.location.href = 'home.html';
        } else {
            document.getElementById('login-error').textContent = 'Invalid email or password!';
        }
    } catch (err) {
        document.getElementById('login-error').textContent = 'Something went wrong!';
    }
}

async function register() {
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    try {
        const data = await apiCall('/auth/register', 'POST', { email, password });
        if (data.token) {
            setToken(data.token);
            setUser({ email: data.email, name: data.name, role: data.role });
            window.location.href = 'home.html';
        } else {
            document.getElementById('reg-error').textContent = data.error || 'Registration failed!';
        }
    } catch (err) {
        document.getElementById('reg-error').textContent = 'Something went wrong!';
    }
}
