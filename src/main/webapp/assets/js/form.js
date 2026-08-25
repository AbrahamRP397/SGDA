/*
 * ==========================================================
 * Librería reutilizable de formularios
 * ==========================================================
 *
 * FUNCIONES PRINCIPALES
 *
 * - Validaciones visuales.
 * - Restricción de caracteres.
 * - Mensajes personalizados.
 * - Campos obligatorios y opcionales.
 * - Confirmación de contraseña.
 * - Estado de carga en botones.
 * - Bloqueo contra doble envío.
 * - Reinicio al cerrar modales.
 * - API pública mediante window.Form.
 *
 *
 * ==========================================================
 * USO BÁSICO
 * ==========================================================
 *
 * FORMULARIO CON ENVÍO AUTOMÁTICO
 *
 * <form
 *     class="js-form"
 *     method="post"
 *     novalidate
 * >
 *
 *     <div class="form-field">
 *
 *         <label for="name">
 *             Nombre
 *         </label>
 *
 *         <input
 *             id="name"
 *             name="name"
 *             class="form-control js-form-field"
 *             data-type="letters"
 *             data-label="Nombre"
 *             minlength="2"
 *             maxlength="50"
 *             required
 *         >
 *
 *         <div class="valid-feedback">
 *             Nombre válido.
 *         </div>
 *
 *         <div class="invalid-feedback"></div>
 *
 *     </div>
 *
 *     <button
 *         type="submit"
 *         class="btn js-form-submit"
 *         data-loading-text="Guardando..."
 *     >
 *         Guardar
 *     </button>
 *
 * </form>
 *
 *
 * ==========================================================
 * FORMULARIO CON MODAL DE CONFIRMACIÓN
 * ==========================================================
 *
 * Usa:
 *
 * data-submit-mode="manual"
 *
 * Esto evita que la librería envíe el formulario
 * automáticamente.
 *
 * Después puedes hacer:
 *
 * if (!Form.validate(form)) {
 *     return;
 * }
 *
 * modal.show();
 *
 *
 * ==========================================================
 * TIPOS DE CAMPO DISPONIBLES
 * ==========================================================
 *
 * data-type="letters"
 * data-type="name"
 * data-type="numbers"
 * data-type="integer"
 * data-type="phone"
 * data-type="email"
 * data-type="alphanumeric"
 * data-type="code"
 * data-type="decimal"
 * data-type="price"
 * data-type="rfc"
 * data-type="password"
 * data-type="url"
 * data-type="date"
 *
 *
 * ==========================================================
 * CONFIRMAR OTRO CAMPO
 * ==========================================================
 *
 * data-match="#password"
 *
 * Ejemplo:
 *
 * <input
 *     type="password"
 *     class="js-form-field"
 *     data-type="password"
 *     id="password"
 * >
 *
 * <input
 *     type="password"
 *     class="js-form-field"
 *     data-match="#password"
 *     data-match-message="Las contraseñas no coinciden."
 * >
 *
 *
 * ==========================================================
 * MENSAJES PERSONALIZADOS
 * ==========================================================
 *
 * data-required-message="Este campo es obligatorio."
 * data-pattern-message="Formato incorrecto."
 * data-minlength-message="El valor es demasiado corto."
 * data-maxlength-message="El valor es demasiado largo."
 * data-min-message="El valor es demasiado pequeño."
 * data-max-message="El valor es demasiado grande."
 * data-type-message="El formato no es válido."
 * data-match-message="Los valores no coinciden."
 * data-valid-message="Campo válido."
 *
 *
 * ==========================================================
 * API PÚBLICA
 * ==========================================================
 *
 * Form.validate(form)
 * Form.validateField(field)
 * Form.reset(form)
 * Form.loading(button, true, "Guardando...")
 * Form.loading(button, false)
 * Form.lock(form)
 * Form.unlock(form)
 * Form.submit(form, button, "Guardando...")
 * Form.refresh(form)
 *
 */


(function () {

    "use strict";


    /*
     * ==========================================================
     * CONFIGURACIÓN GENERAL
     * ==========================================================
     */

    const CONFIG = {

        formSelector:
            ".js-form",

        fieldSelector:
            ".js-form-field",

        submitButtonSelector:
            ".js-form-submit, button[type='submit'], input[type='submit']",

        fieldContainerSelector:
            ".form-field, .js-form-field-group, .mb-3, .mb-4",

        validClass:
            "is-valid",

        invalidClass:
            "is-invalid",

        loadingClass:
            "is-loading",

        submittingClass:
            "is-submitting",

        lockedClass:
            "is-locked",

        validateOnBlur:
            true,

        validateOnInputAfterInteraction:
            true,

        validateOnChange:
            true,

        focusFirstInvalid:
            true,

        scrollToFirstInvalid:
            true,

        scrollBehavior:
            "smooth",

        scrollBlock:
            "center",

        defaultLoadingText:
            "Procesando...",

        loadingSpinnerClass:
            "spinner-border spinner-border-sm",

        modalHiddenEvent:
            "hidden.bs.modal"

    };


    /*
     * ==========================================================
     * EXPRESIONES REGULARES
     * ==========================================================
     */

    const PATTERNS = {

        letters:
            /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\s'-]+$/,

        name:
            /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\s'-]+$/,

        numbers:
            /^\d+$/,

        integer:
            /^\d+$/,

        phone:
            /^\d{10}$/,

        email:
            /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/,

        alphanumeric:
            /^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\s._\-/#]+$/,

        code:
            /^[A-Za-z0-9._-]+$/,

        decimal:
            /^\d+([.,]\d+)?$/,

        price:
            /^\d+([.,]\d{1,2})?$/,

        rfc:
            /^[A-ZÑ&]{3,4}\d{6}[A-Z0-9]{3}$/i,

        password:
            /^(?=.*[A-Za-z])(?=.*\d).{8,72}$/,

        url:
            /^(https?:\/\/)?([\w-]+\.)+[\w-]{2,}(\/\S*)?$/i,

        date:
            /^\d{4}-\d{2}-\d{2}$/

    };


    /*
     * ==========================================================
     * MENSAJES PREDETERMINADOS
     * ==========================================================
     */

    const DEFAULT_MESSAGES = {

        required:
            "{label} es obligatorio.",

        invalid:
            "{label} no es válido.",

        letters:
            "{label} solo puede contener letras y espacios.",

        name:
            "{label} solo puede contener letras, espacios, apóstrofes y guiones.",

        numbers:
            "{label} solo puede contener números.",

        integer:
            "{label} debe ser un número entero.",

        phone:
            "{label} debe contener exactamente 10 dígitos.",

        email:
            "Ingrese un correo electrónico válido.",

        alphanumeric:
            "{label} contiene caracteres no permitidos.",

        code:
            "{label} solo puede contener letras, números, puntos, guiones y guiones bajos.",

        decimal:
            "{label} debe ser un número decimal válido.",

        price:
            "{label} debe ser un importe válido con máximo dos decimales.",

        rfc:
            "Ingrese un RFC válido.",

        password:
            "La contraseña debe tener entre 8 y 72 caracteres e incluir letras y números.",

        url:
            "Ingrese una dirección web válida.",

        date:
            "Ingrese una fecha válida.",

        minlength:
            "{label} debe tener al menos {minlength} caracteres.",

        maxlength:
            "{label} no puede superar {maxlength} caracteres.",

        min:
            "{label} debe ser mayor o igual a {min}.",

        max:
            "{label} debe ser menor o igual a {max}.",

        step:
            "{label} contiene un valor no permitido.",

        type:
            "Ingrese un valor válido para {labelLower}.",

        match:
            "Los valores no coinciden.",

        badInput:
            "{label} contiene un valor incorrecto."

    };


    /*
     * ==========================================================
     * ESTADO INTERNO
     * ==========================================================
     */

    const initializedForms =
        new WeakSet();

    const initializedFields =
        new WeakSet();

    const originalButtonContent =
        new WeakMap();

    const formState =
        new WeakMap();


    /*
     * ==========================================================
     * INICIALIZACIÓN
     * ==========================================================
     */

    document.addEventListener(
        "DOMContentLoaded",
        function () {

            initializeForms(document);

        }
    );


    /*
     * Inicializa formularios dentro de un contenedor.
     *
     * Puede recibir:
     *
     * document
     * modalElement
     * sectionElement
     * formElement
     */

    /**
     * Inicializa los eventos y el estado del módulo.
     *
     * @param {*} root valor de root requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function initializeForms(root) {

        const container =
            root || document;

        let forms = [];

        if (
            container instanceof HTMLFormElement
            && container.matches(CONFIG.formSelector)
        ) {

            forms = [container];

        } else {

            forms = Array.from(
                container.querySelectorAll(
                    CONFIG.formSelector
                )
            );

        }

        forms.forEach(
            function (form) {

                initializeForm(form);

            }
        );

    }


    /*
     * ==========================================================
     * CONFIGURAR FORMULARIO
     * ==========================================================
     */

    /**
     * Inicializa los eventos y el estado del módulo.
     *
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function initializeForm(form) {

        if (
            !form
            || initializedForms.has(form)
        ) {
            return;
        }

        initializedForms.add(form);

        form.noValidate = true;

        formState.set(
            form,
            {
                submitted: false,
                locked: false
            }
        );

        getFields(form).forEach(
            function (field) {

                initializeField(field);

            }
        );

        form.addEventListener(
            "submit",
            function (event) {

                handleFormSubmit(
                    event,
                    form
                );

            }
        );

        form.addEventListener(
            "reset",
            function () {

                window.setTimeout(
                    function () {

                        resetForm(form);

                    },
                    0
                );

            }
        );

        configureModalReset(form);

    }


    /*
     * ==========================================================
     * CONFIGURAR CAMPO
     * ==========================================================
     */

    /**
     * Inicializa los eventos y el estado del módulo.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function initializeField(field) {

        if (
            !field
            || initializedFields.has(field)
        ) {
            return;
        }

        initializedFields.add(field);

        configureInputRestrictions(field);
        configureFieldEvents(field);
        configureMatchingField(field);

    }


    /*
     * ==========================================================
     * EVENTOS DEL CAMPO
     * ==========================================================
     */

    /**
     * Ejecuta la operación configureFieldEvents del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function configureFieldEvents(field) {

        if (CONFIG.validateOnBlur) {

            field.addEventListener(
                "blur",
                function () {

                    markFieldAsTouched(field);

                    validateField(
                        field,
                        {
                            showValid: true,
                            showInvalid: true
                        }
                    );

                }
            );

        }

        field.addEventListener(
            "input",
            function () {

                if (
                    CONFIG.validateOnInputAfterInteraction
                    && (
                        isFieldTouched(field)
                        || field.classList.contains(
                            CONFIG.invalidClass
                        )
                        || field.classList.contains(
                            CONFIG.validClass
                        )
                    )
                ) {

                    validateField(
                        field,
                        {
                            showValid: true,
                            showInvalid: true
                        }
                    );

                }

                validateFieldsMatching(field);

            }
        );

        if (CONFIG.validateOnChange) {

            field.addEventListener(
                "change",
                function () {

                    markFieldAsTouched(field);

                    validateField(
                        field,
                        {
                            showValid: true,
                            showInvalid: true
                        }
                    );

                    validateFieldsMatching(field);

                }
            );

        }

    }


    /*
     * ==========================================================
     * RESTRICCIONES DE ENTRADA
     * ==========================================================
     */

    /**
     * Ejecuta la operación configureInputRestrictions del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function configureInputRestrictions(field) {

        const type =
            getFieldType(field);

        if (!type) {
            return;
        }

        field.addEventListener(
            "input",
            function () {

                const originalValue =
                    field.value;

                const sanitizedValue =
                    sanitizeValue(
                        originalValue,
                        type,
                        field
                    );

                if (
                    sanitizedValue
                    !== originalValue
                ) {

                    const cursorPosition =
                        field.selectionStart;

                    field.value =
                        sanitizedValue;

                    restoreCursorPosition(
                        field,
                        cursorPosition,
                        originalValue,
                        sanitizedValue
                    );

                }

            }
        );

    }


    /**
     * Ejecuta la operación sanitizeValue del módulo de interfaz.
     *
     * @param {*} value valor que se transformará o validará
     * @param {*} type valor de type requerido por la función
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function sanitizeValue(
        value,
        type,
        field
    ) {

        switch (type) {

            case "letters":
            case "name":

                return String(value)
                    .replace(
                        /[^A-Za-zÁÉÍÓÚáéíóúÑñÜü\s'-]/g,
                        ""
                    )
                    .replace(
                        /\s{2,}/g,
                        " "
                    );

            case "numbers":
            case "integer":

                return String(value)
                    .replace(/\D/g, "");

            case "phone": {

                const maximumLength =
                    getMaximumLength(
                        field,
                        10
                    );

                return String(value)
                    .replace(/\D/g, "")
                    .slice(
                        0,
                        maximumLength
                    );

            }

            case "decimal":
            case "price":

                return sanitizeDecimal(
                    value,
                    type === "price"
                        ? 2
                        : getDecimalPlaces(field)
                );

            case "code":

                return String(value)
                    .replace(
                        /[^A-Za-z0-9._-]/g,
                        ""
                    );

            case "alphanumeric":

                return String(value)
                    .replace(
                        /[^A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\s._\-/#]/g,
                        ""
                    );

            case "rfc":

                return String(value)
                    .toUpperCase()
                    .replace(
                        /[^A-ZÑ&0-9]/g,
                        ""
                    )
                    .slice(0, 13);

            default:

                return String(value);

        }

    }


    /**
     * Ejecuta la operación sanitizeDecimal del módulo de interfaz.
     *
     * @param {*} value valor que se transformará o validará
     * @param {*} maximumDecimals valor de maximumDecimals requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function sanitizeDecimal(
        value,
        maximumDecimals
    ) {

        let sanitized =
            String(value)
                .replace(",", ".")
                .replace(
                    /[^0-9.]/g,
                    ""
                );

        const firstDotIndex =
            sanitized.indexOf(".");

        if (firstDotIndex !== -1) {

            const integerPart =
                sanitized.slice(
                    0,
                    firstDotIndex
                );

            let decimalPart =
                sanitized
                    .slice(
                        firstDotIndex + 1
                    )
                    .replace(/\./g, "");

            if (
                Number.isInteger(maximumDecimals)
                && maximumDecimals >= 0
            ) {

                decimalPart =
                    decimalPart.slice(
                        0,
                        maximumDecimals
                    );

            }

            sanitized =
                integerPart
                + "."
                + decimalPart;

        }

        return sanitized;

    }


    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getDecimalPlaces(field) {

        const configuredPlaces =
            Number.parseInt(
                field.dataset.decimals,
                10
            );

        if (
            Number.isInteger(configuredPlaces)
            && configuredPlaces >= 0
        ) {
            return configuredPlaces;
        }

        return null;

    }


    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} defaultValue valor que se transformará o validará
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getMaximumLength(
        field,
        defaultValue
    ) {

        if (
            Number.isInteger(field.maxLength)
            && field.maxLength > 0
        ) {
            return field.maxLength;
        }

        return defaultValue;

    }


    /**
     * Ejecuta la operación restoreCursorPosition del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} previousPosition valor de previousPosition requerido por la función
     * @param {*} previousValue valor que se transformará o validará
     * @param {*} newValue valor que se transformará o validará
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function restoreCursorPosition(
        field,
        previousPosition,
        previousValue,
        newValue
    ) {

        if (
            typeof previousPosition !== "number"
            || typeof field.setSelectionRange
            !== "function"
        ) {
            return;
        }

        const removedCharacters =
            previousValue.length
            - newValue.length;

        const newPosition =
            Math.max(
                0,
                previousPosition
                - removedCharacters
            );

        window.requestAnimationFrame(
            function () {

                try {

                    field.setSelectionRange(
                        newPosition,
                        newPosition
                    );

                } catch (error) {

                    /*
                     * Algunos tipos de input no permiten
                     * modificar manualmente la selección.
                     */

                }

            }
        );

    }


    /*
     * ==========================================================
     * VALIDAR FORMULARIO
     * ==========================================================
     */

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @param {*} options valor de options requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateForm(
        form,
        options
    ) {

        if (!form) {
            return false;
        }

        const settings =
            Object.assign(
                {
                    focusInvalid:
                    CONFIG.focusFirstInvalid,

                    scrollInvalid:
                    CONFIG.scrollToFirstInvalid
                },
                options || {}
            );

        const fields =
            getFields(form);

        let formIsValid =
            true;

        let firstInvalidField =
            null;

        fields.forEach(
            function (field) {

                const fieldIsValid =
                    validateField(
                        field,
                        {
                            showValid: true,
                            showInvalid: true
                        }
                    );

                if (!fieldIsValid) {

                    formIsValid = false;

                    if (!firstInvalidField) {

                        firstInvalidField =
                            field;

                    }

                }

            }
        );

        if (
            !formIsValid
            && firstInvalidField
        ) {

            if (settings.scrollInvalid) {

                scrollToField(
                    firstInvalidField
                );

            }

            if (settings.focusInvalid) {

                window.setTimeout(
                    function () {

                        focusField(
                            firstInvalidField
                        );

                    },
                    150
                );

            }

        }

        form.classList.toggle(
            "was-validated",
            !formIsValid
        );

        return formIsValid;

    }


    /*
     * ==========================================================
     * VALIDAR CAMPO
     * ==========================================================
     */

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} options valor de options requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateField(
        field,
        options
    ) {

        if (
            !field
            || shouldIgnoreField(field)
        ) {
            return true;
        }

        const settings =
            Object.assign(
                {
                    showValid: true,
                    showInvalid: true
                },
                options || {}
            );

        const value =
            getFieldValue(field);

        const optionalAndEmpty =
            !field.required
            && value === "";

        clearNativeCustomValidity(field);

        if (optionalAndEmpty) {

            clearFieldState(field);
            return true;

        }

        const validationResult =
            runValidations(
                field,
                value
            );

        if (!validationResult.valid) {

            if (settings.showInvalid) {

                showInvalid(
                    field,
                    validationResult.message
                );

            }

            return false;

        }

        if (settings.showValid) {

            showValid(field);

        } else {

            clearFieldState(field);

        }

        return true;

    }


    /*
     * ==========================================================
     * EJECUTAR VALIDACIONES
     * ==========================================================
     */

    /**
     * Ejecuta la operación runValidations del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} value valor que se transformará o validará
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function runValidations(
        field,
        value
    ) {

        const label =
            getFieldLabel(field);

        if (
            field.required
            && isEmptyField(field)
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "required",
                    interpolate(
                        DEFAULT_MESSAGES.required,
                        {
                            label: label
                        }
                    )
                )
            );

        }

        if (
            value !== ""
            && field.type === "email"
            && !PATTERNS.email.test(value)
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "type",
                    DEFAULT_MESSAGES.email
                )
            );

        }

        if (
            value !== ""
            && field.validity
            && field.validity.typeMismatch
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "type",
                    interpolate(
                        DEFAULT_MESSAGES.type,
                        {
                            label: label,
                            labelLower:
                                label.toLowerCase()
                        }
                    )
                )
            );

        }

        const minimumLengthResult =
            validateMinimumLength(
                field,
                value,
                label
            );

        if (!minimumLengthResult.valid) {
            return minimumLengthResult;
        }

        const maximumLengthResult =
            validateMaximumLength(
                field,
                value,
                label
            );

        if (!maximumLengthResult.valid) {
            return maximumLengthResult;
        }

        const rangeResult =
            validateNumberRange(
                field,
                value,
                label
            );

        if (!rangeResult.valid) {
            return rangeResult;
        }

        const typeResult =
            validateConfiguredType(
                field,
                value,
                label
            );

        if (!typeResult.valid) {
            return typeResult;
        }

        const nativePatternResult =
            validateNativePattern(
                field,
                label
            );

        if (!nativePatternResult.valid) {
            return nativePatternResult;
        }

        const matchingResult =
            validateMatchingField(
                field
            );

        if (!matchingResult.valid) {
            return matchingResult;
        }

        const customFunctionResult =
            validateCustomFunction(
                field,
                value
            );

        if (!customFunctionResult.valid) {
            return customFunctionResult;
        }

        return validResult();

    }


    /*
     * ==========================================================
     * LONGITUD MÍNIMA
     * ==========================================================
     */

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} value valor que se transformará o validará
     * @param {*} label valor de label requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateMinimumLength(
        field,
        value,
        label
    ) {

        if (
            value === ""
            || !Number.isInteger(
                field.minLength
            )
            || field.minLength < 0
        ) {
            return validResult();
        }

        if (
            value.length
            < field.minLength
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "minlength",
                    interpolate(
                        DEFAULT_MESSAGES.minlength,
                        {
                            label: label,
                            minlength:
                            field.minLength
                        }
                    )
                )
            );

        }

        return validResult();

    }


    /*
     * ==========================================================
     * LONGITUD MÁXIMA
     * ==========================================================
     */

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} value valor que se transformará o validará
     * @param {*} label valor de label requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateMaximumLength(
        field,
        value,
        label
    ) {

        if (
            value === ""
            || !Number.isInteger(
                field.maxLength
            )
            || field.maxLength < 0
        ) {
            return validResult();
        }

        if (
            value.length
            > field.maxLength
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "maxlength",
                    interpolate(
                        DEFAULT_MESSAGES.maxlength,
                        {
                            label: label,
                            maxlength:
                            field.maxLength
                        }
                    )
                )
            );

        }

        return validResult();

    }


    /*
     * ==========================================================
     * RANGO NUMÉRICO
     * ==========================================================
     */

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} value valor que se transformará o validará
     * @param {*} label valor de label requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateNumberRange(
        field,
        value,
        label
    ) {

        if (value === "") {
            return validResult();
        }

        const numericValue =
            Number(
                String(value)
                    .replace(",", ".")
            );

        const minimum =
            field.min !== ""
                ? Number(field.min)
                : null;

        const maximum =
            field.max !== ""
                ? Number(field.max)
                : null;

        if (
            minimum !== null
            && Number.isFinite(minimum)
            && numericValue < minimum
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "min",
                    interpolate(
                        DEFAULT_MESSAGES.min,
                        {
                            label: label,
                            min: minimum
                        }
                    )
                )
            );

        }

        if (
            maximum !== null
            && Number.isFinite(maximum)
            && numericValue > maximum
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "max",
                    interpolate(
                        DEFAULT_MESSAGES.max,
                        {
                            label: label,
                            max: maximum
                        }
                    )
                )
            );

        }

        if (
            field.validity
            && field.validity.stepMismatch
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "step",
                    interpolate(
                        DEFAULT_MESSAGES.step,
                        {
                            label: label
                        }
                    )
                )
            );

        }

        return validResult();

    }


    /*
     * ==========================================================
     * VALIDAR TIPO CONFIGURADO
     * ==========================================================
     */

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} value valor que se transformará o validará
     * @param {*} label valor de label requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateConfiguredType(
        field,
        value,
        label
    ) {

        const type =
            getFieldType(field);

        if (
            !type
            || value === ""
        ) {
            return validResult();
        }

        const pattern =
            PATTERNS[type];

        if (!pattern) {
            return validResult();
        }

        if (!pattern.test(value)) {

            const defaultMessage =
                DEFAULT_MESSAGES[type]
                || DEFAULT_MESSAGES.invalid;

            return invalidResult(
                getMessage(
                    field,
                    "pattern",
                    interpolate(
                        defaultMessage,
                        {
                            label: label,
                            labelLower:
                                label.toLowerCase()
                        }
                    )
                )
            );

        }

        return validResult();

    }


    /*
     * ==========================================================
     * PATTERN NATIVO
     * ==========================================================
     */

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} label valor de label requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateNativePattern(
        field,
        label
    ) {

        if (
            field.validity
            && field.validity.patternMismatch
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "pattern",
                    interpolate(
                        DEFAULT_MESSAGES.invalid,
                        {
                            label: label
                        }
                    )
                )
            );

        }

        return validResult();

    }


    /*
     * ==========================================================
     * COINCIDENCIA ENTRE CAMPOS
     * ==========================================================
     */

    /**
     * Ejecuta la operación configureMatchingField del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function configureMatchingField(field) {

        const matchingSelector =
            field.dataset.match;

        if (!matchingSelector) {
            return;
        }

        const matchingField =
            resolveFieldReference(
                field,
                matchingSelector
            );

        if (!matchingField) {
            return;
        }

        matchingField.addEventListener(
            "input",
            function () {

                if (
                    isFieldTouched(field)
                    || field.value !== ""
                ) {

                    validateField(
                        field,
                        {
                            showValid: true,
                            showInvalid: true
                        }
                    );

                }

            }
        );

    }


    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateMatchingField(field) {

        const matchingSelector =
            field.dataset.match;

        if (!matchingSelector) {
            return validResult();
        }

        const matchingField =
            resolveFieldReference(
                field,
                matchingSelector
            );

        if (!matchingField) {

            console.warn(
                `No se encontró el campo de coincidencia: ${matchingSelector}`
            );

            return validResult();

        }

        if (
            field.value
            !== matchingField.value
        ) {

            return invalidResult(
                getMessage(
                    field,
                    "match",
                    DEFAULT_MESSAGES.match
                )
            );

        }

        return validResult();

    }


    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} selector valor de selector requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function resolveFieldReference(
        field,
        selector
    ) {

        if (!selector) {
            return null;
        }

        const form =
            field.closest("form");

        try {

            if (form) {

                const fieldInForm =
                    form.querySelector(
                        selector
                    );

                if (fieldInForm) {
                    return fieldInForm;
                }

            }

            return document.querySelector(
                selector
            );

        } catch (error) {

            const id =
                selector.replace(
                    /^#/,
                    ""
                );

            return document.getElementById(id);

        }

    }


    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateFieldsMatching(field) {

        const form =
            field.closest("form");

        if (!form) {
            return;
        }

        getFields(form).forEach(
            function (candidate) {

                if (!candidate.dataset.match) {
                    return;
                }

                const referencedField =
                    resolveFieldReference(
                        candidate,
                        candidate.dataset.match
                    );

                if (
                    referencedField === field
                    && (
                        isFieldTouched(candidate)
                        || candidate.value !== ""
                    )
                ) {

                    validateField(
                        candidate,
                        {
                            showValid: true,
                            showInvalid: true
                        }
                    );

                }

            }
        );

    }


    /*
     * ==========================================================
     * VALIDADOR PERSONALIZADO
     * ==========================================================
     *
     * El HTML puede incluir:
     *
     * data-validator="nombreFuncion"
     *
     * La función debe existir en window y regresar:
     *
     * true
     *
     * o
     *
     * {
     *     valid: false,
     *     message: "Mensaje"
     * }
     */

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} value valor que se transformará o validará
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validateCustomFunction(
        field,
        value
    ) {

        const validatorName =
            field.dataset.validator;

        if (!validatorName) {
            return validResult();
        }

        const validator =
            window[validatorName];

        if (
            typeof validator
            !== "function"
        ) {

            console.warn(
                `No existe el validador personalizado: ${validatorName}`
            );

            return validResult();

        }

        try {

            const result =
                validator(
                    value,
                    field
                );

            if (result === true) {
                return validResult();
            }

            if (result === false) {

                return invalidResult(
                    getMessage(
                        field,
                        "invalid",
                        interpolate(
                            DEFAULT_MESSAGES.invalid,
                            {
                                label:
                                    getFieldLabel(field)
                            }
                        )
                    )
                );

            }

            if (
                result
                && typeof result === "object"
            ) {

                return {
                    valid:
                        result.valid !== false,

                    message:
                        result.message || ""
                };

            }

            return validResult();

        } catch (error) {

            console.error(
                `Error en el validador ${validatorName}:`,
                error
            );

            return invalidResult(
                "No fue posible validar este campo."
            );

        }

    }


    /*
     * ==========================================================
     * ESTADOS VISUALES
     * ==========================================================
     */

    /**
     * Muestra el componente visual solicitado y prepara sus datos.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} message valor de message requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function showInvalid(
        field,
        message
    ) {

        field.classList.add(
            CONFIG.invalidClass
        );

        field.classList.remove(
            CONFIG.validClass
        );

        field.setAttribute(
            "aria-invalid",
            "true"
        );

        updateGroupedControlState(
            field,
            "invalid"
        );

        const feedback =
            getFeedbackElement(
                field,
                "invalid"
            );

        if (feedback) {

            feedback.textContent =
                message;

            feedback.style.display =
                "block";

        }

        const validFeedback =
            getFeedbackElement(
                field,
                "valid"
            );

        if (validFeedback) {

            validFeedback.style.display =
                "none";

        }

        updateFieldContainerState(
            field,
            "invalid"
        );

    }


    /**
     * Muestra el componente visual solicitado y prepara sus datos.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function showValid(field) {

        field.classList.remove(
            CONFIG.invalidClass
        );

        field.classList.add(
            CONFIG.validClass
        );

        field.setAttribute(
            "aria-invalid",
            "false"
        );

        updateGroupedControlState(
            field,
            "valid"
        );

        const invalidFeedback =
            getFeedbackElement(
                field,
                "invalid"
            );

        if (invalidFeedback) {

            invalidFeedback.textContent =
                "";

            invalidFeedback.style.display =
                "none";

        }

        const validFeedback =
            getFeedbackElement(
                field,
                "valid"
            );

        if (validFeedback) {

            const configuredMessage =
                field.dataset.validMessage;

            if (configuredMessage) {

                validFeedback.textContent =
                    configuredMessage;

            }

            validFeedback.style.display =
                "block";

        }

        updateFieldContainerState(
            field,
            "valid"
        );

    }


    /**
     * Retira o limpia la información indicada de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function clearFieldState(field) {

        if (!field) {
            return;
        }

        field.classList.remove(
            CONFIG.validClass,
            CONFIG.invalidClass
        );

        field.removeAttribute(
            "aria-invalid"
        );

        field.removeAttribute(
            "data-form-touched"
        );

        updateGroupedControlState(
            field,
            null
        );

        const invalidFeedback =
            getFeedbackElement(
                field,
                "invalid"
            );

        if (invalidFeedback) {

            invalidFeedback.textContent =
                "";

            invalidFeedback.style.display =
                "none";

        }

        const validFeedback =
            getFeedbackElement(
                field,
                "valid"
            );

        if (validFeedback) {

            validFeedback.style.display =
                "none";

        }

        updateFieldContainerState(
            field,
            null
        );

        clearNativeCustomValidity(field);

    }

    /**
     * Actualiza la representación visual o el estado asociado al componente.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} state valor de state requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function updateGroupedControlState(
        field,
        state
    ) {

        if (!field) {
            return;
        }

        const group =
            field.closest(
                ".form-password-group, .js-form-control-group"
            );

        if (!group) {
            return;
        }

        group.classList.remove(
            "is-valid",
            "is-invalid"
        );

        group.removeAttribute(
            "aria-invalid"
        );

        if (state === "valid") {

            group.classList.add(
                "is-valid"
            );

            group.setAttribute(
                "aria-invalid",
                "false"
            );

        }

        if (state === "invalid") {

            group.classList.add(
                "is-invalid"
            );

            group.setAttribute(
                "aria-invalid",
                "true"
            );

        }

    }


    /**
     * Actualiza la representación visual o el estado asociado al componente.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} state valor de state requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function updateFieldContainerState(
        field,
        state
    ) {

        const container =
            getFieldContainer(field);

        if (!container) {
            return;
        }

        container.classList.remove(
            "field-valid",
            "field-invalid"
        );

        if (state === "valid") {

            container.classList.add(
                "field-valid"
            );

        }

        if (state === "invalid") {

            container.classList.add(
                "field-invalid"
            );

        }

    }


    /*
     * ==========================================================
     * OBTENER FEEDBACK
     * ==========================================================
     */

    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} type valor de type requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getFeedbackElement(
        field,
        type
    ) {

        const selector =
            type === "valid"
                ? ".valid-feedback"
                : ".invalid-feedback";

        const container =
            getFieldContainer(field);

        if (container) {

            const feedback =
                container.querySelector(
                    selector
                );

            if (feedback) {
                return feedback;
            }

        }

        const inputGroup =
            field.closest(
                ".input-group"
            );

        if (inputGroup) {

            const feedback =
                inputGroup.querySelector(
                    selector
                );

            if (feedback) {
                return feedback;
            }

        }

        return null;

    }


    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getFieldContainer(field) {

        return field.closest(
            CONFIG.fieldContainerSelector
        );

    }


    /*
     * ==========================================================
     * MENSAJES
     * ==========================================================
     */

    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @param {*} type valor de type requerido por la función
     * @param {*} defaultMessage valor de defaultMessage requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getMessage(
        field,
        type,
        defaultMessage
    ) {

        const datasetProperty =
            `${type}Message`;

        return field.dataset[
            datasetProperty
            ] || defaultMessage;

    }


    /**
     * Ejecuta la operación interpolate del módulo de interfaz.
     *
     * @param {*} template valor de template requerido por la función
     * @param {*} values valor que se transformará o validará
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function interpolate(
        template,
        values
    ) {

        return String(template)
            .replace(
                /\{(\w+)\}/g,
                function (
                    match,
                    key
                ) {

                    return Object.prototype
                        .hasOwnProperty.call(
                            values,
                            key
                        )
                        ? values[key]
                        : match;

                }
            );

    }


    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getFieldLabel(field) {

        const configuredLabel =
            field.dataset.label;

        if (configuredLabel) {
            return configuredLabel;
        }

        if (field.id) {

            const label =
                document.querySelector(
                    `label[for="${escapeSelector(field.id)}"]`
                );

            if (label) {

                const labelClone =
                    label.cloneNode(true);

                labelClone
                    .querySelectorAll(
                        ".text-danger, .required-marker"
                    )
                    .forEach(
                        function (element) {

                            element.remove();

                        }
                    );

                const labelText =
                    labelClone.textContent
                        .replace(/\*/g, "")
                        .trim();

                if (labelText) {
                    return labelText;
                }

            }

        }

        if (field.name) {

            return formatFieldName(
                field.name
            );

        }

        return "Este campo";

    }


    /**
     * Convierte el valor al formato utilizado para su presentación.
     *
     * @param {*} name valor de name requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function formatFieldName(name) {

        const formatted =
            String(name)
                .replace(
                    /([a-z])([A-Z])/g,
                    "$1 $2"
                )
                .replace(
                    /[_-]+/g,
                    " "
                )
                .trim();

        return formatted
            ? formatted.charAt(0)
                .toUpperCase()
            + formatted.slice(1)
            : "Este campo";

    }

    /*
 * ==========================================================
 * ENVÍO DEL FORMULARIO
 * ==========================================================
 */

    /**
     * Procesa el evento de interfaz asociado a esta función.
     *
     * @param {*} event evento del navegador que originó la operación
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function handleFormSubmit(event, form) {

        if (!event || !form) {
            return;
        }

        const submitMode =
            String(
                form.dataset.submitMode || "automatic"
            )
                .trim()
                .toLowerCase();

        /*
         * Siempre detenemos primero el envío tradicional.
         *
         * De esta forma ningún formulario registrado por esta
         * librería podrá recargar accidentalmente la página.
         */
        event.preventDefault();

        /*
         * El modo manual significa:
         *
         * - form.js valida visualmente.
         * - form.js NO envía la petición.
         * - El módulo específico realizará fetch.
         *
         * Ejemplos:
         * metric.js
         * product.js
         * provider.js
         */
        if (submitMode === "manual") {

            validateForm(form);

            return;
        }

        /*
         * Modo automático tradicional.
         *
         * Se conserva para módulos que todavía no han sido
         * migrados a fetch.
         */
        if (!validateForm(form)) {
            return;
        }

        const button =
            getSubmitButton(
                form,
                event.submitter
            );

        submitForm(
            form,
            button
        );
    }


    /*
     * Envía un formulario de forma tradicional.
     *
     * Este método se conserva temporalmente para los módulos
     * que todavía no han sido migrados a fetch.
     *
     * Los formularios con data-submit-mode="manual"
     * nunca deben llegar a este método.
     */
    /**
     * Valida y envía la información capturada por el usuario.
     *
     * @param {*} form valor de form requerido por la función
     * @param {*} button valor de button requerido por la función
     * @param {*} loadingText valor de loadingText requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function submitForm(
        form,
        button,
        loadingText
    ) {

        if (!form) {
            return false;
        }

        if (!validateForm(form)) {
            return false;
        }

        const state =
            formState.get(form) || {
                submitted: false,
                locked: false
            };

        /*
         * Bloqueo contra doble envío.
         */
        if (state.submitted) {
            return false;
        }

        state.submitted = true;

        formState.set(
            form,
            state
        );

        setButtonLoading(
            button || getSubmitButton(form),
            true,
            loadingText
        );

        lockForm(form);

        /*
         * Envía el formulario sin volver a disparar
         * el evento submit.
         *
         * Solo se utiliza para formularios que aún trabajan
         * mediante envío tradicional.
         */
        HTMLFormElement.prototype
            .submit.call(form);

        return true;
    }

    /*
     * ==========================================================
     * BOTÓN DE ENVÍO
     * ==========================================================
     */

    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @param {*} submitter valor de submitter requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getSubmitButton(
        form,
        submitter
    ) {

        if (submitter) {
            return submitter;
        }

        if (form.id) {

            const externalButton =
                document.querySelector(
                    `[type="submit"][form="${escapeSelector(form.id)}"]`
                );

            if (externalButton) {
                return externalButton;
            }

        }

        return form.querySelector(
            CONFIG.submitButtonSelector
        );

    }


    /*
     * ==========================================================
     * ESTADO DE CARGA
     * ==========================================================
     */

    /**
     * Actualiza la representación visual o el estado asociado al componente.
     *
     * @param {*} button valor de button requerido por la función
     * @param {*} loading valor de loading requerido por la función
     * @param {*} loadingText valor de loadingText requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function setButtonLoading(
        button,
        loading,
        loadingText
    ) {

        if (!button) {
            return;
        }

        if (loading) {

            if (
                !originalButtonContent.has(button)
            ) {

                originalButtonContent.set(
                    button,
                    button.innerHTML
                );

            }

            const text =
                loadingText
                || button.dataset.loadingText
                || CONFIG.defaultLoadingText;

            button.disabled = true;

            button.classList.add(
                CONFIG.loadingClass
            );

            button.setAttribute(
                "aria-busy",
                "true"
            );

            button.innerHTML =
                createLoadingContent(text);

            return;

        }

        button.disabled = false;

        button.classList.remove(
            CONFIG.loadingClass
        );

        button.removeAttribute(
            "aria-busy"
        );

        if (
            originalButtonContent.has(button)
        ) {

            button.innerHTML =
                originalButtonContent.get(
                    button
                );

        }

    }


    /**
     * Valida y envía la información capturada por el usuario.
     *
     * @param {*} text valor de text requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function createLoadingContent(text) {

        return `
            <span
                class="${CONFIG.loadingSpinnerClass}"
                aria-hidden="true"
            ></span>

            <span class="form-loading-text">
                ${escapeHtml(text)}
            </span>
        `;

    }


    /*
     * ==========================================================
     * BLOQUEAR FORMULARIO
     * ==========================================================
     */

    /**
     * Ejecuta la operación lockForm del módulo de interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function lockForm(form) {

        if (!form) {
            return;
        }

        const state =
            formState.get(form)
            || {};

        state.locked = true;

        formState.set(
            form,
            state
        );

        form.classList.add(
            CONFIG.submittingClass,
            CONFIG.lockedClass
        );

        form.setAttribute(
            "aria-busy",
            "true"
        );

        getInteractiveElements(form)
            .forEach(
                function (element) {

                    preserveOriginalState(
                        element
                    );

                    /*
                     * No usamos disabled para inputs,
                     * selects o textareas porque los
                     * elementos disabled no se envían
                     * al servidor.
                     */

                    if (
                        element instanceof HTMLInputElement
                        || element instanceof HTMLTextAreaElement
                    ) {

                        element.readOnly =
                            true;

                    }

                    element.style.pointerEvents =
                        "none";

                    element.setAttribute(
                        "aria-disabled",
                        "true"
                    );

                    element.classList.add(
                        "is-form-locked"
                    );

                }
            );

    }


    /*
     * ==========================================================
     * DESBLOQUEAR FORMULARIO
     * ==========================================================
     */

    /**
     * Ejecuta la operación unlockForm del módulo de interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function unlockForm(form) {

        if (!form) {
            return;
        }

        const state =
            formState.get(form)
            || {};

        state.locked = false;
        state.submitted = false;

        formState.set(
            form,
            state
        );

        form.classList.remove(
            CONFIG.submittingClass,
            CONFIG.lockedClass
        );

        form.removeAttribute(
            "aria-busy"
        );

        getInteractiveElements(form)
            .forEach(
                function (element) {

                    restoreOriginalState(
                        element
                    );

                    element.style.pointerEvents =
                        "";

                    element.removeAttribute(
                        "aria-disabled"
                    );

                    element.classList.remove(
                        "is-form-locked"
                    );

                }
            );

        const submitButton =
            getSubmitButton(form);

        setButtonLoading(
            submitButton,
            false
        );

    }


    /**
     * Ejecuta la operación preserveOriginalState del módulo de interfaz.
     *
     * @param {*} element elemento del DOM relacionado con la operación
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function preserveOriginalState(
        element
    ) {

        if (
            element.dataset.formOriginalReadonly
            === undefined
        ) {

            element.dataset.formOriginalReadonly =
                String(
                    Boolean(element.readOnly)
                );

        }

        if (
            element.dataset.formOriginalDisabled
            === undefined
        ) {

            element.dataset.formOriginalDisabled =
                String(
                    Boolean(element.disabled)
                );

        }

    }


    /**
     * Ejecuta la operación restoreOriginalState del módulo de interfaz.
     *
     * @param {*} element elemento del DOM relacionado con la operación
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function restoreOriginalState(
        element
    ) {

        if (
            element.dataset.formOriginalReadonly
            !== undefined
        ) {

            element.readOnly =
                element.dataset
                    .formOriginalReadonly
                === "true";

            delete element.dataset
                .formOriginalReadonly;

        }

        if (
            element.dataset.formOriginalDisabled
            !== undefined
        ) {

            element.disabled =
                element.dataset
                    .formOriginalDisabled
                === "true";

            delete element.dataset
                .formOriginalDisabled;

        }

    }


    /*
     * ==========================================================
     * REINICIAR FORMULARIO
     * ==========================================================
     */

    /**
     * Ejecuta la operación resetForm del módulo de interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @param {*} options valor de options requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function resetForm(
        form,
        options
    ) {

        if (!form) {
            return;
        }

        const settings =
            Object.assign(
                {
                    resetValues: false,
                    unlock: true
                },
                options || {}
            );

        if (settings.resetValues) {

            HTMLFormElement.prototype
                .reset.call(form);

        }

        getFields(form).forEach(
            function (field) {

                clearFieldState(field);

            }
        );

        form.classList.remove(
            "was-validated"
        );

        if (settings.unlock) {

            unlockForm(form);

        }

        const state =
            formState.get(form)
            || {};

        state.submitted = false;

        formState.set(
            form,
            state
        );

    }


    /*
     * ==========================================================
     * REINICIAR AL CERRAR MODAL
     * ==========================================================
     */

    /**
     * Ejecuta la operación configureModalReset del módulo de interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function configureModalReset(form){
        const modal=form.closest(".modal");
        if(!modal)return;

        modal.addEventListener(CONFIG.modalHiddenEvent,function(){
            if(modal.dataset.preserveFormState==="true"){
                return;
            }

            const shouldResetValues=form.dataset.resetOnClose!=="false";

            resetForm(form,{
                resetValues:shouldResetValues,
                unlock:true
            });
        });
    }


    /*
     * ==========================================================
     * REFRESCAR FORMULARIO
     * ==========================================================
     *
     * Útil cuando los campos fueron agregados
     * dinámicamente después de cargar la página.
     */

    /**
     * Actualiza la representación visual o el estado asociado al componente.
     *
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function refreshForm(form) {

        if (!form) {
            return;
        }

        getFields(form).forEach(
            function (field) {

                initializeField(field);

            }
        );

    }


    /*
     * ==========================================================
     * FUNCIONES AUXILIARES
     * ==========================================================
     */

    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getFields(form) {

        if (!form) {
            return [];
        }

        return Array.from(
            form.querySelectorAll(
                CONFIG.fieldSelector
            )
        );

    }


    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getInteractiveElements(
        form
    ) {

        if (!form) {
            return [];
        }

        return Array.from(
            form.querySelectorAll(
                "input, select, textarea, button"
            )
        );

    }


    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getFieldType(field) {

        return String(
            field.dataset.type || ""
        )
            .trim()
            .toLowerCase();

    }


    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getFieldValue(field) {

        if (
            field.type === "checkbox"
        ) {

            return field.checked
                ? field.value || "true"
                : "";

        }

        if (
            field.type === "radio"
        ) {

            const form =
                field.closest("form");

            const checked =
                form
                    ? form.querySelector(
                        `input[type="radio"][name="${escapeSelector(field.name)}"]:checked`
                    )
                    : null;

            return checked
                ? checked.value
                : "";

        }

        return String(
            field.value || ""
        ).trim();

    }


    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function isEmptyField(field) {

        if (
            field.type === "checkbox"
        ) {

            return !field.checked;

        }

        if (
            field.type === "radio"
        ) {

            const form =
                field.closest("form");

            if (!form) {
                return !field.checked;
            }

            return !form.querySelector(
                `input[type="radio"][name="${escapeSelector(field.name)}"]:checked`
            );

        }

        return getFieldValue(field)
            === "";

    }


    /**
     * Ejecuta la operación shouldIgnoreField del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function shouldIgnoreField(field) {

        return Boolean(
            field.disabled
            || field.type === "hidden"
            || field.dataset.validate === "false"
            || field.closest(
                "[data-form-ignore='true']"
            )
        );

    }


    /**
     * Ejecuta la operación markFieldAsTouched del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function markFieldAsTouched(field) {

        field.dataset.formTouched =
            "true";

    }


    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function isFieldTouched(field) {

        return field.dataset.formTouched
            === "true";

    }


    /**
     * Retira o limpia la información indicada de la interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function clearNativeCustomValidity(
        field
    ) {

        if (
            field
            && typeof field.setCustomValidity
            === "function"
        ) {

            field.setCustomValidity("");

        }

    }


    /**
     * Ejecuta la operación scrollToField del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function scrollToField(field) {

        if (
            !field
            || typeof field.scrollIntoView
            !== "function"
        ) {
            return;
        }

        field.scrollIntoView(
            {
                behavior:
                CONFIG.scrollBehavior,

                block:
                CONFIG.scrollBlock
            }
        );

    }


    /**
     * Ejecuta la operación focusField del módulo de interfaz.
     *
     * @param {*} field valor de field requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function focusField(field) {

        if (
            !field
            || typeof field.focus
            !== "function"
        ) {
            return;
        }

        try {

            field.focus(
                {
                    preventScroll: true
                }
            );

        } catch (error) {

            field.focus();

        }

    }


    /**
     * Ejecuta la operación validResult del módulo de interfaz.
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function validResult() {

        return {
            valid: true,
            message: ""
        };

    }


    /**
     * Ejecuta la operación invalidResult del módulo de interfaz.
     *
     * @param {*} message valor de message requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function invalidResult(message) {

        return {
            valid: false,
            message:
                message || ""
        };

    }


    /**
     * Ejecuta la operación escapeHtml del módulo de interfaz.
     *
     * @param {*} value valor que se transformará o validará
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function escapeHtml(value) {

        const element =
            document.createElement("div");

        element.textContent =
            String(value || "");

        return element.innerHTML;

    }


    /**
     * Ejecuta la operación escapeSelector del módulo de interfaz.
     *
     * @param {*} value valor que se transformará o validará
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function escapeSelector(value) {

        if (
            window.CSS
            && typeof window.CSS.escape
            === "function"
        ) {

            return window.CSS.escape(
                String(value)
            );

        }

        return String(value)
            .replace(
                /(["\\])/g,
                "\\$1"
            );

    }


    /*
     * ==========================================================
     * API PÚBLICA
     * ==========================================================
     */

    window.Form = {

        /*
         * Inicializar formularios nuevos.
         */

        init:
        initializeForms,


        /*
         * Inicializar campos agregados dinámicamente.
         */

        refresh:
        refreshForm,


        /*
         * Validar formulario completo.
         */

        validate:
        validateForm,


        /*
         * Validar campo específico.
         */

        validateField:
        validateField,


        /*
         * Mostrar u ocultar carga.
         *
         * Form.loading(button, true, "Guardando...")
         * Form.loading(button, false)
         */

        loading:
        setButtonLoading,


        /*
         * Bloquear formulario.
         */

        lock:
        lockForm,


        /*
         * Desbloquear formulario.
         */

        unlock:
        unlockForm,


        /*
         * Reiniciar estados visuales.
         *
         * Form.reset(form)
         *
         * Form.reset(form, {
         *     resetValues: true
         * })
         */

        reset:
        resetForm,


        /*
         * Validar, activar carga, bloquear
         * y enviar el formulario.
         *
         * Form.submit(
         *     form,
         *     button,
         *     "Guardando..."
         * )
         */

        submit:
        submitForm,


        /*
         * Limpiar estado de un campo.
         */

        clearField:
        clearFieldState,


        /*
         * Mostrar error manual.
         *
         * Form.showError(
         *     field,
         *     "El correo ya está registrado."
         * )
         */

        showError:
        showInvalid,


        /*
         * Mostrar válido manualmente.
         */

        showValid:
        showValid,


        /*
         * Obtener configuración.
         */

        config:
        CONFIG,


        /*
         * Obtener patrones disponibles.
         */

        patterns:
        PATTERNS

    };

})();
