package bancoservidor;

/**
 * Simula una operación bancaria automática.
 * Se ejecuta periódicamente mediante ScheduledExecutorService.
 */
public class OperacionBancaria implements Runnable {

    private CuentaBancaria cuenta;

    /**
     * Recibe la cuenta sobre la que se va a operar
     */
    public OperacionBancaria(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }

    /**
     * Método que se ejecuta cada segundo.
     * Simula ingresos automáticos al sistema.
     */
    @Override
    public void run() {
        cuenta.ingresar(10);
        System.out.println("[OPERACIÓN] Ingreso automático de 10€ | Saldo: " + cuenta.getSaldo());
    }
}
