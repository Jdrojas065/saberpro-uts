document.addEventListener('DOMContentLoaded', () => {

    // Marcar ítem activo del sidebar según URL actual
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-item').forEach(item => {
        const href = item.getAttribute('href') || '';
        if (href && currentPath.startsWith(href)) {
            item.classList.add('active');
        }
    });

    // Toggle sidebar en móvil
    const menuToggle = document.getElementById('menu-toggle');
    const sidebar    = document.querySelector('.sidebar');
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', () => sidebar.classList.toggle('open'));
        document.addEventListener('click', (e) => {
            if (!sidebar.contains(e.target) && !menuToggle.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        });
    }

    // Auto-ocultar alertas flash tras 4 segundos
    document.querySelectorAll('.alert-box.auto-hide').forEach(el => {
        setTimeout(() => {
            el.style.transition = 'opacity .4s';
            el.style.opacity = '0';
            setTimeout(() => el.remove(), 400);
        }, 4000);
    });

    // Confirmar acciones destructivas
    document.querySelectorAll('[data-confirm]').forEach(btn => {
        btn.addEventListener('click', e => {
            if (!confirm(btn.dataset.confirm || '¿Está seguro?')) e.preventDefault();
        });
    });

    // Mostrar/ocultar contraseña
    const togglePass = document.getElementById('toggle-password');
    const passInput  = document.getElementById('password');
    if (togglePass && passInput) {
        togglePass.addEventListener('click', () => {
            const isText = passInput.type === 'text';
            passInput.type = isText ? 'password' : 'text';
            togglePass.querySelector('i').className = isText ? 'ti ti-eye' : 'ti ti-eye-off';
        });
    }
});