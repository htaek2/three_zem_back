document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);
    const email = formData.get('email');
    const password = formData.get('password');

    const credentials = {
        email: email,
        password: password
    };

    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
    .then(response => {
        if (response.ok) {
            const token = response.headers.get('Authorization');
            if (token) {
                localStorage.setItem('authToken', token);
                alert('로그인 성공!');
                window.location.href = '/';
            } else {
                alert('로그인에 성공했지만 토큰을 받지 못했습니다.');
            }
        } else {
            alert('로그인 실패: 이메일 또는 비밀번호가 올바르지 않습니다.');
        }
    })
    .catch(error => {
        console.error('Error during login:', error);
        alert('로그인 중 오류가 발생했습니다.');
    });
});