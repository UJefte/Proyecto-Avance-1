// Prueba de carga: varios guardias escaneando QR al mismo tiempo.
// Correr desde la carpeta pruebas:
//   k6 run k6/carga.js
//   k6 run -e BASE_URL=https://TU-APP.up.railway.app k6/carga.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.2/index.js';

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const JSON_H = { headers: { 'Content-Type': 'application/json' } };

// sube a 20 usuarios, luego a 50, y baja
export const options = {
  stages: [
    { duration: '20s', target: 20 },
    { duration: '40s', target: 50 },
    { duration: '20s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],      // menos del 5% de errores
    http_req_duration: ['p(95)<1000'],   // el 95% responde en menos de 1 segundo
  },
};

// antes de la prueba se crean 10 residentes con su pase
export function setup() {
  const salud = http.get(`${BASE}/actuator/health`);
  if (salud.status !== 200) {
    throw new Error(`El servidor no responde en ${BASE}`);
  }

  const depto = http.post(`${BASE}/api/v1/departamentos`,
    JSON.stringify({ numero: 'K6', torre: 'Z' }), JSON_H).json();

  const codigos = [];
  for (let i = 0; i < 10; i++) {
    const residente = http.post(`${BASE}/api/v1/residentes`, JSON.stringify({
      nombre: `Residente K6 ${i}`,
      correo: `k6_${Date.now()}_${i}@correo.com`,
      departamentoId: depto.id,
    }), JSON_H).json();
    const pase = http.post(`${BASE}/api/v1/pases/residente/${residente.id}`, null, JSON_H).json();
    codigos.push(pase.codigo);
  }
  return { codigos };
}

// lo que hace cada usuario virtual: escanear un QR y consultar los pases
export default function (data) {
  const codigo = data.codigos[Math.floor(Math.random() * data.codigos.length)];

  const res = http.post(`${BASE}/api/v1/accesos/validar`, JSON.stringify({ codigo }), JSON_H);
  check(res, {
    'validar responde 200': (r) => r.status === 200,
    'acceso permitido': (r) => r.json('resultado') === 'PERMITIDO',
  });

  const pases = http.get(`${BASE}/api/v1/pases`);
  check(pases, { 'lista de pases responde 200': (r) => r.status === 200 });

  sleep(1);
}

// guarda el reporte en HTML ademas de mostrarlo en consola
export function handleSummary(data) {
  return {
    'reportes/k6.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}
