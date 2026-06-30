document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');

    // Ejemplo: Puedes agregar lógica antes de enviar el formulario si lo deseas
    if (loginForm) {
        loginForm.addEventListener('submit', function (event) {
            const btnSubmit = document.querySelector('.btn-login');

            // Opcional: Cambiar el texto del botón y deshabilitarlo para evitar dobles envíos
            // btnSubmit.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Iniciando...';
            // btnSubmit.disabled = true;
        });
    }
});