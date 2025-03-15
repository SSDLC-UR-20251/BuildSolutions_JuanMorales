const { transferir } = require('../src/app');

// Mock de las funciones de lectura y escritura de archivos
jest.mock('fs', () => ({
    readFileSync: jest.fn().mockReturnValue(JSON.stringify({
        'juan.jose@urosario.edu.co': [
            { balance: '50', type: 'Deposit', timestamp: '2025-02-11T14:17:21.921536Z' },
            { balance: '-20', type: 'Withdrawal', timestamp: '2025-02-15T10:30:15.123456Z' }
        ],
        'sara.palaciosc@urosario.edu.co': [
            { balance: '400', type: 'Deposit', timestamp: '2025-03-13T17:41:06.330219Z' },
        ]
    })),
    writeFileSync: jest.fn(),
}));

// Mock de la función calcularSaldo
jest.mock('../src/app', () => {
    const originalModule = jest.requireActual('../src/app');
    return {
        ...originalModule,
        calcularSaldo: jest.fn((filePath, usuario) => {
            if (usuario === 'juan.jose@urosario.edu.co') {
                return 30; // Saldo simulado para pruebas
            }
            return 400; // Saldo predeterminado para otros usuarios
        }),
    };
});

describe('Pruebas para la función transferir', () => {
    afterEach(() => {
        jest.clearAllMocks(); // Limpiar mocks tras cada prueba
    });

    test('Debe realizar una transferencia exitosa entre cuentas válidas', () => {
        const resultado = transferir('transactions.txt', 'juan.jose@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', 30);

        expect(resultado.exito).toBe(true);
        expect(resultado.mensaje).toBe('Transferencia de 30 realizada correctamente de juan.jose@urosario.edu.co a sara.palaciosc@urosario.edu.co.');

        // Validar que los mocks se llamaron correctamente
        expect(require('fs').readFileSync).toHaveBeenCalledWith('transactions.txt', 'utf8');
        expect(require('fs').writeFileSync).toHaveBeenCalled();

        // Validar datos escritos en el archivo
        const transaccionesActualizadas = JSON.parse(require('fs').writeFileSync.mock.calls[0][1]);
        expect(transaccionesActualizadas['juan.jose@urosario.edu.co']).toContainEqual({
            balance: '-30',
            type: 'Transfer',
            timestamp: expect.any(String),
        });
        expect(transaccionesActualizadas['sara.palaciosc@urosario.edu.co']).toContainEqual({
            balance: '30',
            type: 'Transfer',
            timestamp: expect.any(String),
        });
    });

    test('Debe fallar la transferencia por saldo insuficiente', () => {
        const resultado = transferir('transactions.txt', 'juan.jose@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', 100);

        expect(resultado.exito).toBe(false);
        expect(resultado.mensaje).toBe('Saldo insuficiente en la cuenta de origen (juan.jose@urosario.edu.co).');

        // Validar que no se escribieron datos
        expect(require('fs').writeFileSync).not.toHaveBeenCalled();
    });

    test('Debe fallar la transferencia si la cuenta de origen no existe', () => {
        const resultado = transferir('transactions.txt', 'no.existe@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', 30);

        expect(resultado.exito).toBe(false);
        expect(resultado.mensaje).toBe('La cuenta de origen (no.existe@urosario.edu.co) no existe.');
    });

    test('Debe fallar la transferencia si la cuenta de destino no existe', () => {
        const resultado = transferir('transactions.txt', 'juan.jose@urosario.edu.co', 'no.existe@urosario.edu.co', 30);

        expect(resultado.exito).toBe(false);
        expect(resultado.mensaje).toBe('La cuenta de destino (no.existe@urosario.edu.co) no existe.');
    });

    test('Debe fallar la transferencia con un monto inválido (negativo)', () => {
        const resultado = transferir('transactions.txt', 'juan.jose@urosario.edu.co', 'sara.palaciosc@urosario.edu.co', -50);

        expect(resultado.exito).toBe(false);
        expect(resultado.mensaje).toBe('El monto debe ser mayor a 0.');
    });
});
