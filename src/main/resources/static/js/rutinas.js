/* 
 * Archivo: rutinas.js
 * Funciones generales para TechShop
 */


/* Función para mostrar la imagen seleccionada antes de guardar */
function mostrarImagen(input) {
    if (input.files && input.files[0]) {

        const imagen = input.files[0];
        const maximo = 512 * 1024; // 512 KB

        if (imagen.size <= maximo) {

            const lector = new FileReader();

            lector.onload = function (e) {
                const vistaPrevia = document.getElementById('blah');

                if (vistaPrevia) {
                    vistaPrevia.src = e.target.result;
                    vistaPrevia.style.height = '200px';
                    vistaPrevia.style.display = 'block';
                }
            };

            lector.readAsDataURL(input.files[0]);

        } else {
            alert("La imagen seleccionada es muy grande. No debe superar los 512 KB.");
            input.value = "";

            const vistaPrevia = document.getElementById('blah');

            if (vistaPrevia) {
                vistaPrevia.src = "#";
                vistaPrevia.style.display = 'none';
            }
        }
    }
}


/* Para insertar información en el modal de eliminación según el registro seleccionado */
document.addEventListener('DOMContentLoaded', function () {

    const confirmModal = document.getElementById('confirmModal');

    if (confirmModal) {

        confirmModal.addEventListener('show.bs.modal', function (event) {

            const button = event.relatedTarget;

            if (!button) {
                return;
            }

            const id = button.getAttribute('data-bs-id');
            const descripcion = button.getAttribute('data-bs-descripcion');

            const modalId = document.getElementById('modalId');
            const modalDescripcion = document.getElementById('modalDescripcion');

            if (modalId) {
                modalId.value = id;
            }

            if (modalDescripcion) {
                modalDescripcion.textContent = descripcion;
            }
        });
    }
});


/* Para quitar los toast automáticamente después de 4 segundos */
document.addEventListener('DOMContentLoaded', function () {

    setTimeout(() => {
        document.querySelectorAll('.toast').forEach(toast => {
            toast.classList.remove('show');
        });
    }, 4000);

});