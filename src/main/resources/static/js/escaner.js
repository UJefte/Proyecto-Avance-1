// Lector de QR con la camara (webcam o celular).
// Usa la libreria html5-qrcode que esta en js/libs.

const URL_VALIDAR = "/api/v1/accesos/validar";
const PAGINA_LISTA = "/panel/bitacora";
const SEGUNDOS_PARA_REGRESAR = 5;

const lector = new Html5Qrcode("lector");
let yaSeLeyo = false;


// ---------- 1. Abrir la camara ----------

function abrirCamara() {
    yaSeLeyo = false;
    document.getElementById("resultado").hidden = true;
    document.getElementById("otro").hidden = true;

    // la camara solo funciona en https o en localhost
    if (!window.isSecureContext) {
        mostrarAviso("El navegador no deja usar la cámara porque la página no es https. Ábrela desde localhost o desde la versión en la nube.");
        return;
    }

    // "environment" = camara de atras en el celular
    lector.start(
        { facingMode: "environment" },
        { fps: 10, qrbox: 250 },
        cuandoSeLeeElQr,
        function () { /* cada cuadro sin QR, no hacemos nada */ }
    ).catch(function (error) {
        mostrarAviso("No se pudo abrir la cámara: " + error);
    });
}


// ---------- 2. Evento: la camara ya leyo un QR ----------

function cuandoSeLeeElQr(textoDelQr) {
    // la camara sigue leyendo cuadros, solo nos quedamos con el primero
    if (yaSeLeyo) {
        return;
    }
    yaSeLeyo = true;

    lector.stop().then(function () {
        mandarAlServidor(textoDelQr);
    });
}


// ---------- 3. Mandar el codigo al servicio con fetch ----------

function mandarAlServidor(codigo) {
    fetch(URL_VALIDAR, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ codigo: codigo })
    })
        .then(function (respuesta) { return respuesta.json(); })
        .then(mostrarResultado)
        .catch(function () {
            mostrarAviso("No se pudo conectar con el servidor");
            document.getElementById("otro").hidden = false;
        });
}


// ---------- 4. Mostrar la respuesta y regresar a la lista ----------

function mostrarResultado(datos) {
    const caja = document.getElementById("resultado");
    caja.hidden = false;
    caja.className = "resultado " + (datos.resultado === "PERMITIDO" ? "permitido" : "denegado");

    document.getElementById("res-titulo").textContent = datos.resultado;
    document.getElementById("res-titular").textContent = datos.titular || "Desconocido";
    document.getElementById("res-tipo").textContent = datos.tipoPase ? "· " + datos.tipoPase.toLowerCase() : "";
    document.getElementById("res-detalle").textContent = datos.motivo;
    document.getElementById("otro").hidden = false;

    // cuenta regresiva y luego a la bitacora
    let faltan = SEGUNDOS_PARA_REGRESAR;
    const texto = document.getElementById("res-regreso");
    texto.textContent = "Regresando a la bitácora en " + faltan + "...";
    const reloj = setInterval(function () {
        faltan--;
        texto.textContent = "Regresando a la bitácora en " + faltan + "...";
        if (faltan <= 0) {
            clearInterval(reloj);
            window.location.href = PAGINA_LISTA;
        }
    }, 1000);

    // si quiere escanear otro, se cancela el regreso
    document.getElementById("otro").onclick = function () {
        clearInterval(reloj);
        abrirCamara();
    };
}

function mostrarAviso(mensaje) {
    const aviso = document.getElementById("aviso");
    aviso.textContent = mensaje;
    aviso.hidden = false;
}


// al cargar la pagina se abre la camara
document.getElementById("otro").onclick = abrirCamara;
abrirCamara();
