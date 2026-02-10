package bancoservidor;

/**
 * Servicio en segundo plano que realiza auditorías periódicas.
 * No modifica datos, solo los consulta.
 */
public class ServicioAuditoria implements Runnable {

    private CuentaBancaria cuenta;

    public ServicioAuditoria(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }

    /**
     * Se ejecuta cada 3 segundos.
     * Simula una auditoría del sistema bancario.
     */
    @Override
    public void run() {
        System.out.println("[AUDITORÍA] Saldo actual auditado: " + cuenta.getSaldo());
    }
}
