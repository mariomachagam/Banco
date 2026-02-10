package bancoservidor;

import bancoservidor.persistencia.ServicioPersistencia;

public class MainCreateUser {
    public static void main(String[] args) {
        String usuario = "user1";
        String password = "pass123";

        boolean usuarioCreado = ServicioPersistencia.registrarUsuario("Prueba", usuario, password);

        if(usuarioCreado){
            System.out.println("Usuario creado: " + usuario);

            String iban = "ES" + (int)(Math.random()*100000000);
            CuentaBancaria cuenta = new CuentaBancaria(1000.0, iban);
            boolean cuentaCreada = ServicioPersistencia.registrarCuenta(usuario, cuenta);

            if(cuentaCreada) System.out.println("Cuenta creada: IBAN " + iban + " Saldo: " + cuenta.getSaldo());
            else System.out.println("Error creando la cuenta");
        } else System.out.println("El usuario ya existe");
    }
}
