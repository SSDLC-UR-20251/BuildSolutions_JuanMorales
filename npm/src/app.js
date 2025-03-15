const fs = require('fs');
const path = require('path');

// Ruta global para el archivo de transacciones
const filePath = path.join(__dirname, '../data/transactions.txt');

// Constantes de mensajes de error
const ERROR_USUARIO_NO_EXISTE = (usuario) => `El usuario ${usuario} no existe.`;
const ERROR_MONTO_INVALIDO = 'El monto debe ser mayor a 0.';
const ERROR_SALDO_INSUFICIENTE = (usuario) => `Saldo insuficiente en la cuenta de origen (${usuario}).`;
const ERROR_CUENTA_ORIGEN = (cuenta) => `La cuenta de origen (${cuenta}) no existe.`;
const ERROR_CUENTA_DESTINO = (cuenta) => `La cuenta de destino (${cuenta}) no existe.`;

// Función para leer un archivo JSON
function leerArchivo(filePath) {
    try {
        const data = fs.readFileSync(filePath, 'utf8');
        return JSON.parse(data);
    } catch (error) {
        console.error('Error al leer el archivo:', error.message);
        return {}; // Retornar un objeto vacío en caso de error
    }
}

// Función para escribir un archivo JSON
function escribirArchivo(filePath, data) {
    try {
        const jsonData = JSON.stringify(data, null, 2); // Formato legible con indentación
        fs.writeFileSync(filePath, jsonData, 'utf8');
        console.log('Archivo actualizado correctamente.');
    } catch (error) {
        console.error('Error al escribir el archivo:', error.message);
    }
}

// Función para calcular el saldo actual de un usuario
function calcularSaldo(filePath, usuario) {
    const transacciones = leerArchivo(filePath);
    const transaccionesUsuario = transacciones[usuario];

    if (!transaccionesUsuario) {
        console.error(ERROR_USUARIO_NO_EXISTE(usuario));
        return 0;
    }

    // Validar y calcular el saldo
    return transaccionesUsuario.reduce((saldo, transaccion) => {
        const balance = parseFloat(transaccion.balance);
        if (isNaN(balance)) {
            console.warn(`Transacción inválida encontrada para ${usuario}.`);
            return saldo;
        }
        return saldo + balance;
    }, 0);
}

// Función para registrar una transacción
function registrarTransaccion(transacciones, usuario, balance, tipo) {
    const timestamp = new Date().toISOString(); // Generar timestamp
    transacciones[usuario].push({ balance, type: tipo, timestamp });
}

// Función para realizar una transferencia entre cuentas
function transferir(filePath, de, para, monto) {
    if (monto <= 0) {
        return { exito: false, mensaje: ERROR_MONTO_INVALIDO };
    }

    const transacciones = leerArchivo(filePath);
    const transaccionesDe = transacciones[de];
    const transaccionesPara = transacciones[para];

    // Validar existencia de las cuentas
    if (!transaccionesDe) {
        return { exito: false, mensaje: ERROR_CUENTA_ORIGEN(de) };
    }
    if (!transaccionesPara) {
        return { exito: false, mensaje: ERROR_CUENTA_DESTINO(para) };
    }

    // Validar saldo suficiente
    const saldoDe = calcularSaldo(filePath, de);
    if (saldoDe < monto) {
        return { exito: false, mensaje: ERROR_SALDO_INSUFICIENTE(de) };
    }

    // Registrar las transacciones
    registrarTransaccion(transacciones, de, `-${monto}`, 'Transfer');
    registrarTransaccion(transacciones, para, `${monto}`, 'Transfer');

    // Guardar las transacciones actualizadas
    escribirArchivo(filePath, transacciones);

    return { exito: true, mensaje: `Transferencia de ${monto} realizada correctamente de ${de} a ${para}.` };
}

// Ejemplo de uso:
const resultado = transferir(filePath, 'juan.jose@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', 50);
console.log(resultado.mensaje);

// Exportar las funciones para pruebas
module.exports = { transferir, calcularSaldo, leerArchivo, escribirArchivo };
