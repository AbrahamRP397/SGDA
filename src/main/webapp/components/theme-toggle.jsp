<%--
    Vista técnica: theme-toggle.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Botón de cambio de tema -->
<button id="themeToggle"
        class="btn theme-toggle-btn"
        aria-label="Cambiar tema"
        title="Cambiar tema">
    <i id="themeIcon" class="bi bi-moon-stars"></i>
</button>

<style>
    .theme-toggle-btn {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 1100;
        width: 45px;
        height: 45px;
        border-radius: 50%;
        background: var(--bg-color);
        color: var(--text-color);
        border: none;
        box-shadow: var(--neumo-shadow);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.3rem;
        transition: box-shadow 0.3s ease, transform 0.2s ease,
        background-color 0.5s ease, color 0.3s ease;
        cursor: pointer;
    }

    .theme-toggle-btn:hover {
        box-shadow: var(--neumo-shadow-hover);
        transform: scale(1.05);
    }

    .theme-toggle-btn:active {
        box-shadow: var(--neumo-shadow-inset);
        transform: scale(0.95);
    }

    .theme-toggle-btn i {
        transition: transform 0.5s ease;
    }

    .theme-toggle-btn:hover i {
        transform: rotate(20deg);
    }

    @media (max-width: 768px) {
        .theme-toggle-btn {
            top: 12px;
            right: 12px;
            width: 40px;
            height: 40px;
            font-size: 1.1rem;
        }
    }
</style>

<script>
    (function() {
        'use strict';

        const themeToggle = document.getElementById('themeToggle');
        const themeIcon = document.getElementById('themeIcon');

        // Función para obtener el tema actual
        function getCurrentTheme() {
            return document.documentElement.getAttribute('data-theme') || 'light';
        }

        // Función para establecer el tema
        function setTheme(theme) {
            document.documentElement.setAttribute('data-theme', theme);
            localStorage.setItem('theme', theme);
            updateIcon(theme);

            // Disparar evento para que otros componentes puedan reaccionar
            document.dispatchEvent(new CustomEvent('themeChanged', { detail: { theme: theme } }));
        }

        // Función para actualizar el ícono según el tema
        function updateIcon(theme) {
            if (theme === 'dark') {
                themeIcon.className = 'bi bi-sun-fill';
            } else {
                themeIcon.className = 'bi bi-moon-stars';
            }
        }

        // Función para alternar el tema
        function toggleTheme() {
            const currentTheme = getCurrentTheme();
            const newTheme = currentTheme === 'light' ? 'dark' : 'light';
            setTheme(newTheme);
        }

        // Cargar tema guardado
        function loadTheme() {
            const savedTheme = localStorage.getItem('theme');
            if (savedTheme) {
                setTheme(savedTheme);
            } else {
                // Detectar preferencia del sistema
                const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
                if (prefersDark) {
                    setTheme('dark');
                }
            }
        }

        // Inicializar
        loadTheme();

        // Event listener
        if (themeToggle) {
            themeToggle.addEventListener('click', toggleTheme);
        }

        // Escuchar cambios en la preferencia del sistema
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function(e) {
            const currentTheme = getCurrentTheme();
            // Solo cambiar si no hay un tema guardado manualmente
            if (!localStorage.getItem('theme')) {
                setTheme(e.matches ? 'dark' : 'light');
            }
        });

        // Función para precargar imágenes
        function preloadImages() {
            const contextPath = '${pageContext.request.contextPath}';
            const images = [
                contextPath + '/assets/img/loginBG.jpg',
                contextPath + '/assets/img/loginBG-dark.jpg',
                contextPath + '/assets/img/logoSGDA.svg',
                contextPath + '/assets/img/logoSGDA-dark.svg',
                contextPath + '/assets/img/logoSGDAClosed.svg',
                contextPath + '/assets/img/logoSGDAClosed-dark.svg'
            ];
            images.forEach(src => {
                const img = new Image();
                img.src = src;
            });
        }
        preloadImages();

    })();
</script>
<%-- Componente reutilizable para alternar el tema visual de la interfaz. --%>
