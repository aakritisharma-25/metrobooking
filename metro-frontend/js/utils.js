const API_BASE = 'https://metrobooking.onrender.com/api';

function getToken() {
    return localStorage.getItem('token');
}

function setToken(token) {
    localStorage.setItem('token', token);
}

function getUser() {
    return JSON.parse(localStorage.getItem('user') || '{}');
}

function setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

function checkAuth() {
    if (!getToken()) {
        window.location.href = 'index.html';
    }
}

async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json };
    const publicRoutes = ['/auth/login', '/auth/register'];
    
    if (getToken() && !publicRoutes.includes(endpoint)) {
        headers['Authorization'] = `Bearer ${getToken()}`;
    }
    
    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);
    
    const response = await fetch(`${API_BASE}${endpoint}`, options);
    
    // If token expired, clear and redirect to login
    if (response.status === 401 || response.status === 403) {
        if (!publicRoutes.includes(endpoint)) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = 'index.html';
        }
    }
    
    return response.json();
}
