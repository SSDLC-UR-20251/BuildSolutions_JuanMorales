import sys
import random
import string

# TODO: Implementar la función para generar códigos de recuperación
def generar_codigo():
    """
    Debe generar un código de 6 caracteres alfanuméricos sin caracteres ambiguos.
    Caracteres ambiguos a evitar: '0', 'O', '1', 'l'
    """
    caracteres_validos = string.ascii_uppercase.replace('O', '') + string.ascii_lowercase.replace('l', '') + string.digits.replace('0', '1')
    
    # Generar un código aleatorio de 6 caracteres
    codigo = ''.join(random.choice(caracteres_validos) for _ in range(6))
    
    return codigo
    
if __name__ == "__main__":
    if len(sys.argv) != 2 or not sys.argv[1].isdigit():
        print("Uso: python main.py <cantidad_de_codigos>")
        sys.exit(1)

    cantidad = int(sys.argv[1])
    print("Generando códigos de recuperación...")

    for i in range(cantidad):
        print(f"Código {i+1}: {generar_codigo()}")
