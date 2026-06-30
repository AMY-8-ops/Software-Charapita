document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');

    // Ejemplo: Puedes agregar lógica antes de enviar el formulario si lo deseas
    if (loginForm) {
        loginForm.addEventListener('submit', async function (event) {
            event.preventDefault(); // Evitar el envío tradicional del formulario
            
            const btnSubmit = document.querySelector('.btn-login');
            const originalBtnHtml = btnSubmit.innerHTML;
            
            // Mostrar estado de carga
            btnSubmit.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Iniciando...';
            btnSubmit.disabled = true;
            
            const correo = document.getElementById('correo').value;
            const contrasena = document.getElementById('contrasena').value;
            
            try {
                const response = await fetch('/api/usuarios/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ correo: correo, contrasena: contrasena })
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Error de autenticación');
                }
                
                const data = await response.json();
                
                // Guardar la información del usuario en localStorage para usarla en el dashboard
                localStorage.setItem('user', JSON.stringify(data));
                
                // Redirigir al dashboard
                window.location.href = '/dashboard.html';
                
            } catch (error) {
                // Mostrar error en la interfaz
                let errorContainer = document.querySelector('.alert-error');
                if (!errorContainer) {
                    errorContainer = document.createElement('div');
                    errorContainer.className = 'alert-error';
                    loginForm.parentElement.insertBefore(errorContainer, loginForm);
                }
                errorContainer.innerHTML = `<i class="fa-solid fa-circle-exclamation"></i> <span>${error.message}</span>`;
                
                // Restaurar botón
                btnSubmit.innerHTML = originalBtnHtml;
                btnSubmit.disabled = false;
            }
        });
    }
});